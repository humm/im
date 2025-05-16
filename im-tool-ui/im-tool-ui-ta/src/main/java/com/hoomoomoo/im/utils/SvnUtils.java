package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;


/**
 * @author
 * @description svn工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    /**
     *
     * @param times  获取提交记录次数
     * @param modifyNo 任务单编号
     * @return
     * @throws Exception
     */
    public static List<LogDto> getSvnLog(int times, String modifyNo) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String svnUrl = getSvnUrl(appConfigDto);
        final List<LogDto> logList = new ArrayList<>();
        if (!svnUrl.contains(KEY_HTTPS)) {
            // git
            int current;
            File fileParent = new File(svnUrl);
            if (fileParent.isDirectory()) {
                File[] fileList = fileParent.listFiles();
                outer: for (File file : fileList) {
                    current = 0;
                    if (!file.isDirectory()) {
                        continue;
                    }
                    if (!FileUtils.isSuffixDirectory(file, FILE_TYPE_GIT, false, false)) {
                        continue;
                    }
                    Git git = getGit(file.getAbsolutePath() + STR_SLASH + FILE_TYPE_GIT);
                    Iterable<RevCommit> logs = git.log().setMaxCount(Integer.valueOf(appConfigDto.getSvnMaxRevision())).call();
                    for (RevCommit commit : logs) {
                        if (StringUtils.equals(commit.getAuthorIdent().getName(), appConfigDto.getSvnUsername())) {
                            if (current > times) {
                                break;
                            }
                            current++;
                            String commitMessage = commit.getFullMessage();
                            if (commitMessage.trim().startsWith("Merge branch")) {
                                continue;
                            }
                            LogDto svnLogDto = new LogDto();
                            String modifyMsg = getSvnMsg(commitMessage, STR_1);
                            svnLogDto.setTaskNo(modifyMsg);
                            if (StringUtils.isNotBlank(modifyNo)) {
                                if (!StringUtils.equals(modifyNo.trim(), modifyMsg.trim())) {
                                    continue;
                                }
                            }
                            svnLogDto.setSubmitNo(commit.getName());
                            svnLogDto.setMsg(getSvnMsg(commitMessage, STR_0));
                            svnLogDto.setTime(CommonUtils.getCurrentDateTime1(commit.getAuthorIdent().getWhen()));
                            List<String> pathList = getGitCommitFile(file, commit.name());
                            svnLogDto.setNum(String.valueOf(pathList.size()));
                            svnLogDto.setFile(pathList);
                            svnLogDto.setCodeVersion(getVersion(getSvnMsg(commitMessage, STR_2)));
                            logList.add(svnLogDto);
                        }
                    }
                }
            }
        } else {
            // svn
            SVNRepository repository = getSVNRepository(appConfigDto);
            // 最后一次提交记录
            long endRevision = -1;
            SVNDirEntry lastSVNDirEntry = repository.info(STR_POINT, endRevision);
            // 开始版本
            long startRevision = lastSVNDirEntry.getRevision() - Integer.valueOf(appConfigDto.getSvnMaxRevision());
            repository.log(new String[]{STR_BLANK}, startRevision, endRevision, true, true, svnLogEntry -> {
                if (StringUtils.equals(svnLogEntry.getAuthor(), appConfigDto.getSvnUsername())) {
                    LogDto svnLogDto = new LogDto();
                    svnLogDto.setSubmitNo(Long.valueOf(svnLogEntry.getRevision()).toString());
                    svnLogDto.setTime(CommonUtils.getCurrentDateTime1(svnLogEntry.getDate()));
                    Map<String, SVNLogEntryPath> logMap = svnLogEntry.getChangedPaths();
                    svnLogDto.setNum(String.valueOf(logMap.size()));
                    List<String> pathList = new ArrayList<>();
                    svnLogDto.setFile(pathList);
                    String msg = svnLogEntry.getMessage();
                    svnLogDto.setMsg(getSvnMsg(msg, STR_0));
                    String modifyMsg = getSvnMsg(msg, STR_1);
                    svnLogDto.setTaskNo(modifyMsg);
                    if (StringUtils.isNotBlank(modifyNo)) {
                        if (!StringUtils.equals(modifyNo.trim(), modifyMsg.trim())) {
                            return;
                        }
                    }
                    Iterator<String> iterator = logMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        SVNLogEntryPath value = logMap.get(key);
                        String path = value.getPath().replace(appConfigDto.getSvnDeletePrefix(), STR_BLANK);
                        if (OPERATE_TYPE_DELETE.equals(String.valueOf(value.getType()))) {
                            path += NAME_DELETE;
                        }
                        svnLogDto.setCodeVersion(getVersion(path));
                        pathList.add(path + STR_NEXT_LINE);
                    }
                    logList.add(svnLogDto);
                }
            });
        }
        Collections.sort(logList);
        List<LogDto> res = logList;
        if (times > 0 && res.size() > times) {
            res = res.subList(0, times);
        }
        return res;
    }

    private static List<String> getGitCommitFile(File file, String commitId) throws IOException {
        List<String> fileList = new ArrayList<>();
        String content = CmdUtils.exe(file.getAbsolutePath(), "git show " + commitId);
        List<String> logList = Arrays.asList(content.split(STR_NEXT_LINE));
        for (int i=0; i<logList.size(); i++) {
            String item = logList.get(i).trim();
            if (item.startsWith(KEY_GIT_LOG_FILE)) {
                String name = item.split(KEY_GIT_LOG_FILE)[1].trim().split(STR_SPACE)[0].trim();
                if (name.startsWith(KEY_GIT_FILE_PREFIX)) {
                    name = name.substring(name.indexOf(KEY_GIT_FILE_PREFIX) + KEY_GIT_FILE_PREFIX.length());
                }
                fileList.add(file.getName() + STR_SLASH + name + STR_NEXT_LINE);
            }
        }
        return fileList;
    }

    public static LinkedHashMap<String, SvnStatDto> getSvnLog(Date start, Date end, LinkedHashMap<String, SvnStatDto> svnStat, boolean notice) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        AppConfigDto item = new AppConfigDto();
        copyProperties(item, appConfigDto);
        if (svnStat.isEmpty()) {
            // 构建数据
            LinkedHashMap<String, String> userList = appConfigDto.getSvnStatUser();
            Iterator<String> iterator = userList.keySet().iterator();
            while (iterator.hasNext()) {
                String userCode = iterator.next();
                String userName = userList.get(userCode);
                SvnStatDto svnStatDto = new SvnStatDto();
                svnStatDto.setUserCode(userCode);
                svnStatDto.setUserName(userName);
                svnStatDto.setFirstTime(STR_BLANK);
                svnStatDto.setLastTime(STR_BLANK);
                svnStatDto.setSubmitTimes(STR_0);
                svnStatDto.setFileNum(STR_0);
                svnStatDto.setFileTimes(STR_0);
                svnStatDto.setFile(new LinkedHashMap<>());
                svnStatDto.setSvnNum(new LinkedHashMap<>());
                svnStat.put(userCode, svnStatDto);
            }
            if (notice) {
                SvnStatDto svnStatDto = new SvnStatDto();
                svnStatDto.setNotice(STR_BLANK);
                svnStat.put(KEY_NOTICE, svnStatDto);
            }
        }
        Map<String, String> svnRep = appConfigDto.getSvnUrl();
        Iterator<String> svnIterator = svnRep.keySet().iterator();
        while (svnIterator.hasNext()) {
            String rep = svnIterator.next();
            List<String> repList = new ArrayList<>();
            if (KEY_TRUNK.equals(rep)) {
                repList.add(rep);
            } else {
                Map<String, String> svnVersionMap = appConfigDto.getCopyCodeVersion();
                if (MapUtils.isNotEmpty(svnVersionMap)) {
                    Iterator<String> version = svnVersionMap.keySet().iterator();
                    while (version.hasNext()) {
                        String ver = version.next();
                        if (KEY_DESKTOP.equals(ver) || KEY_TRUNK.equals(ver)) {
                            continue;
                        }
                        String svnUrl = appConfigDto.getSvnUrl().get(KEY_BRANCHES);
                        if (ver.contains(KEY_FUND)) {
                            svnUrl = TaCommonUtils.getSvnUrl(ver, svnUrl);
                            ver += KEY_SOURCES_TA_FUND;
                        }
                        repList.add(svnUrl + ver);
                    }
                }
            }
            for (String svn : repList) {
                item.setSvnRep(svn);
                SVNRepository repository = getSVNRepository(item);
                long startRevision, endRevision;
                try {
                    startRevision = repository.getDatedRevision(start);
                    endRevision = repository.getDatedRevision(end);
                } catch (Exception e) {
                    LoggerUtils.info(e);
                    if (notice) {
                        svnStat.get(KEY_NOTICE).setNotice(CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
                    }
                    continue;
                }
                repository.log(new String[]{""}, startRevision, endRevision, true, true, svnLogEntry -> {
                    String userName = appConfigDto.getSvnStatUser().get(svnLogEntry.getAuthor());
                    if (StringUtils.isNotBlank(userName)) {
                        SvnStatDto svnStatDto = svnStat.get(svnLogEntry.getAuthor());
                        if (svnStatDto != null && svnStatDto.getSvnNum().get(svnLogEntry.getRevision()) == null) {
                            svnStatDto.getSvnNum().put(svnLogEntry.getRevision(), svnLogEntry.getRevision());
                            if (StringUtils.isBlank(svnStatDto.getFirstTime())) {
                                svnStatDto.setFirstTime(CommonUtils.getCurrentDateTime8(svnLogEntry.getDate()));
                            }
                            svnStatDto.setLastTime(CommonUtils.getCurrentDateTime8(svnLogEntry.getDate()));
                            svnStatDto.setSubmitTimes(String.valueOf(Integer.valueOf(svnStatDto.getSubmitTimes()) + 1));
                            Map<String, SVNLogEntryPath> logMap = svnLogEntry.getChangedPaths();
                            Iterator<String> iterator = logMap.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                SVNLogEntryPath value = logMap.get(key);
                                String path = value.getPath().replace(appConfigDto.getSvnDeletePrefix(), STR_BLANK);
                                if (svnStatDto.getFile().get(path) == null) {
                                    svnStatDto.getFile().put(path, 1);
                                    svnStatDto.setFileNum(String.valueOf(Integer.valueOf(svnStatDto.getFileNum()) + 1));
                                    svnStatDto.setFileTimes(String.valueOf(Integer.valueOf(svnStatDto.getFileTimes()) + 1));
                                } else {
                                    svnStatDto.setFileTimes(String.valueOf(Integer.valueOf(svnStatDto.getFileTimes()) + 1));
                                }
                            }
                            String msg = getSvnMsg(svnLogEntry.getMessage(), STR_0);
                            if (notice) {
                                String noticeMsg = String.format(MSG_SVN_REALTIME_STAT, userName, svnStatDto.getLastTime(), msg, svnLogEntry.getRevision());
                                svnStat.get(KEY_NOTICE).setNotice(noticeMsg);
                            }
                        }
                    }
                });
            }
        }
        return svnStat;
    }

    private static String getVersion(String path) {
        String version = KEY_TRUNK;
        if (path.contains(KEY_FIX) && path.contains(KEY_SOURCES)) {
            path = path.replace(KEY_FUND_SLASH, STR_BLANK).replace(KEY_TEMP, STR_BLANK);
            version = path.substring(path.indexOf(KEY_FIX) + KEY_FIX.length() + 1, path.indexOf(KEY_SOURCES) - 1);
        }
        return version;
    }

    private static String getVersion(AppConfigDto appConfigDto, String ver) {
        ver = CommonUtils.getComplexVer(ver);
        String resVer;
        boolean isTrunk = true;
        if (ver.contains(KEY_FUND)) {
            Map<String, String> svnVersionMap = appConfigDto.getCopyCodeVersion();
            if (MapUtils.isNotEmpty(svnVersionMap)) {
                Iterator<String> version = svnVersionMap.keySet().iterator();
                while (version.hasNext()) {
                    String verTmp = version.next();
                    if (KEY_DESKTOP.equals(verTmp) || KEY_TRUNK.equals(verTmp) || !verTmp.contains(KEY_FUND)) {
                        continue;
                    }
                    if (!ver.endsWith("000")) {
                        isTrunk = false;
                        break;
                    }
                    if (ver.compareTo(verTmp) <= 0) {
                        isTrunk = false;
                        break;
                    }
                }
            }
        } else {
            isTrunk = false;
        }
        if (isTrunk) {
            resVer = KEY_TRUNK;
        } else {
            resVer = TaCommonUtils.changeVersion(appConfigDto, ver);
        }
        return resVer;
    }

    private static String getSvnMsg(String msg, String type) {
        String indexMsg = NAME_SVN_DESCRIBE;
        if (STR_1.equals(type)) {
            indexMsg = NAME_SVN_MODIFY_NO;
        } else if (STR_2.equals(type)) {
            indexMsg = NAME_SVN_VERSION_NO;
        }
        if (StringUtils.isNotBlank(msg)) {
            String[] message = msg.split(STR_NEXT_LINE);
            for (String item : message) {
                if (item.startsWith(indexMsg)) {
                    if (item.split(STR_BRACKETS_1_RIGHT).length <= 1) {
                        msg = item;
                    } else {
                        msg = item.split(STR_BRACKETS_1_RIGHT)[1];
                    }
                    break;
                }
            }
        }
        return msg;
    }

    private static String getSvnUrl(AppConfigDto appConfigDto) {
        String svnUrl = appConfigDto.getSvnUrl().get(appConfigDto.getSvnRep());
        if (StringUtils.isBlank(svnUrl)) {
            svnUrl = appConfigDto.getSvnRep();
        }
        return svnUrl;
    }

    private static Git getGit(String gitPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setGitDir(new File(gitPath));
        Repository repo = builder.build();
        Git git = new Git(repo);
        return git;
    }

    private static SVNRepository getSVNRepository(AppConfigDto appConfigDto) throws Exception {
        String svnUrl = getSvnUrl(appConfigDto);
        String svnName = appConfigDto.getSvnUsername();
        String svnPassword = appConfigDto.getSvnPassword();
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnName, svnPassword);
        repository.setAuthenticationManager(authManager);
        return repository;
    }

    public static String updateSvn(String workspace, TextArea fileLog) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String svnName = appConfigDto.getSvnUsername();
        String svnPassword = appConfigDto.getSvnPassword();
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, svnName, svnPassword);
        SVNUpdateClient svnUpdateClient = svnClientManager.getUpdateClient();
        svnUpdateClient.setIgnoreExternals(false);
        Long version = null;
        try {
            version = svnUpdateClient.doUpdate(new File(workspace), SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e) + STR_NEXT_LINE);
        }
        return version == null ? null : String.valueOf(version);
    }

    public static void initSvnRep(AppConfigDto appConfigDto, String version) {
        String versionValue = getVersion(appConfigDto, version);
        String versionValueYear = versionValue.replaceAll(STR_VERSION_PREFIX, STR_BLANK).split("\\.")[0];
        if (KEY_TRUNK.equals(versionValue) || (versionValueYear.compareTo("2025") >= 0 && !versionValue.contains("2022"))) {
            String url = appConfigDto.getSvnUrl().get(KEY_TRUNK);
            if (!versionValue.endsWith("000")) {
                url = appConfigDto.getSvnUrl().get(KEY_GIT_BRANCHES);
            }
            LoggerUtils.info("git仓库地址为: " + url);
            if (StringUtils.isNotBlank(url)) {
                appConfigDto.setSvnRep(url);
            }
        } else {
            String svnUrl = appConfigDto.getSvnUrl().get(KEY_BRANCHES);
            if (versionValue.contains(KEY_FUND)) {
                svnUrl = TaCommonUtils.getSvnUrl(versionValue, svnUrl);
                versionValue += KEY_SOURCES_TA_FUND;
            }
            String svnRep = svnUrl + versionValue;
            LoggerUtils.info("svn仓库地址为: " + svnRep);
            appConfigDto.setSvnRep(svnRep);
        }
    }
}
