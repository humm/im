package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
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

    public static List<LogDto> getSvnLog(int times, int version, String modifyNo) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        SVNRepository repository = getSVNRepository(appConfigDto);
        List<LogDto> logList = new ArrayList<>();
        // 最后一次提交记录
        long endRevision = -1;
        SVNDirEntry lastSVNDirEntry = repository.info(SYMBOL_POINT, endRevision);
        // 开始版本号
        long startRevision = lastSVNDirEntry.getRevision() - Integer.valueOf(appConfigDto.getSvnMaxRevision());
        if (version != 0) {
            startRevision = version;
            endRevision = version;
        }
        if (StringUtils.isNotBlank(modifyNo)) {
            times = MAX_TIMES;
        }
        repository.log(new String[]{SYMBOL_EMPTY}, startRevision, endRevision, true, true, svnLogEntry -> {
            if (StringUtils.equals(svnLogEntry.getAuthor(), appConfigDto.getSvnUsername())) {
                LogDto svnLogDto = new LogDto();
                svnLogDto.setVersion(Long.valueOf(svnLogEntry.getRevision()).toString());
                svnLogDto.setTime(CommonUtils.getCurrentDateTime1(svnLogEntry.getDate()));
                Map<String, SVNLogEntryPath> logMap = svnLogEntry.getChangedPaths();
                svnLogDto.setNum(String.valueOf(logMap.size()));
                List<String> pathList = new ArrayList<>();
                svnLogDto.setFile(pathList);
                svnLogDto.setMsg(getSvnMsg(svnLogEntry, STR_0));
                String modifyMsg = getSvnMsg(svnLogEntry, STR_1);
                if (StringUtils.isNotBlank(modifyNo)) {
                    if (!StringUtils.equals(modifyNo.trim(), modifyMsg.trim())) {
                        return;
                    }
                }
                Iterator<String> iterator = logMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    SVNLogEntryPath value = logMap.get(key);
                    String path = value.getPath().replace(appConfigDto.getSvnDeletePrefix(), SYMBOL_EMPTY);
                    if (OPERATE_TYPE_DELETE.equals(String.valueOf(value.getType()))) {
                        path += NAME_DELETE;
                    }
                    pathList.add(path + SYMBOL_NEXT_LINE);
                }
                logList.add(svnLogDto);
            }
        });
        Collections.sort(logList);
        if (logList.size() >= times) {
            return logList.subList(0, times);
        }
        return logList;
    }

    public static LinkedHashMap<String, SvnStatDto> getSvnLog(Date start, Date end, LinkedHashMap<String, SvnStatDto> svnStat, boolean notice) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
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
                svnStatDto.setFirstTime(SYMBOL_EMPTY);
                svnStatDto.setLastTime(SYMBOL_EMPTY);
                svnStatDto.setSubmitTimes(STR_0);
                svnStatDto.setFileNum(STR_0);
                svnStatDto.setFileTimes(STR_0);
                svnStatDto.setFile(new LinkedHashMap<>());
                svnStatDto.setSvnNum(new LinkedHashMap<>());
                svnStat.put(userCode, svnStatDto);
            }
            if (notice) {
                SvnStatDto svnStatDto = new SvnStatDto();
                svnStatDto.setNotice(SYMBOL_EMPTY);
                svnStat.put(KEY_NOTICE, svnStatDto);
            }
        }
        Map<String, String> svnRep = appConfigDto.getSvnUrl();
        Iterator<String> svnIterator = svnRep.keySet().iterator();
        while (svnIterator.hasNext()) {
            String rep = svnIterator.next();
            item.setSvnRep(rep);
            SVNRepository repository = getSVNRepository(item);
            long startRevision = repository.getDatedRevision(start);
            long endRevision = repository.getDatedRevision(end);
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
                            String path = value.getPath().replace(appConfigDto.getSvnDeletePrefix(), SYMBOL_EMPTY);
                            if (svnStatDto.getFile().get(path) == null) {
                                svnStatDto.getFile().put(path, 1);
                                svnStatDto.setFileNum(String.valueOf(Integer.valueOf(svnStatDto.getFileNum()) + 1));
                                svnStatDto.setFileTimes(String.valueOf(Integer.valueOf(svnStatDto.getFileTimes()) + 1));
                            } else {
                                svnStatDto.setFileTimes(String.valueOf(Integer.valueOf(svnStatDto.getFileTimes()) + 1));
                            }
                        }
                        String msg = getSvnMsg(svnLogEntry, STR_0);
                        if (notice) {
                            String noticeMsg = String.format(MSG_SVN_REALTIME_STAT, userName, svnStatDto.getLastTime(), msg, svnLogEntry.getRevision());
                            svnStat.get(KEY_NOTICE).setNotice(noticeMsg);
                        }
                    }
                }
            });
        }
        return svnStat;
    }

    private static String getSvnMsg(SVNLogEntry svnLogEntry, String type) {
        String indexMsg = NAME_SVN_DESCRIBE;
        if (STR_1.equals(type)) {
            indexMsg = NAME_SVN_MODIFY_NO;
        }
        String msg = svnLogEntry.getMessage();
        if (StringUtils.isNotBlank(msg)) {
            String[] message = msg.split(SYMBOL_NEXT_LINE);
            for (String item : message) {
                if (item.startsWith(indexMsg)) {
                    if (item.split(SYMBOL_BRACKETS_1_RIGHT).length <= 1) {
                        msg = item;
                    } else {
                        msg = item.split(SYMBOL_BRACKETS_1_RIGHT)[1];
                    }
                    break;
                }
            }
        }
        return msg;
    }

    private static SVNRepository getSVNRepository(AppConfigDto appConfigDto) throws Exception {
        String svnUrl = appConfigDto.getSvnUrl().get(appConfigDto.getSvnRep());
        String svnName = appConfigDto.getSvnUsername();
        String svnPassword = appConfigDto.getSvnPassword();
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnName, svnPassword);
        repository.setAuthenticationManager(authManager);
        return repository;
    }

    public static Long updateSvn(String workspace) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        String svnName = appConfigDto.getSvnUsername();
        String svnPassword = appConfigDto.getSvnPassword();
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, svnName, svnPassword);
        SVNUpdateClient svnUpdateClient = svnClientManager.getUpdateClient();
        svnUpdateClient.setIgnoreExternals(false);
        Long version = svnUpdateClient.doUpdate(new File(workspace), SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        return version;
    }
}
