package com.hoomoomoo.im.utils;


import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
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
import java.util.*;


/**
 * @author
 * @description svn工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    public static List<LogDto> getSvnLog(int times) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        final List<LogDto> logList = new ArrayList<>();
        String svnUrl = appConfigDto.getSvnUrl();
        String svnName = appConfigDto.getSvnUsername();
        String svnPassword = appConfigDto.getSvnPassword();
        // HEAD (the latest) revision
        long endRevision = -1;
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnName, svnPassword);
        repository.setAuthenticationManager(authManager);
        // 最后一次提交记录
        SVNDirEntry lastSVNDirEntry = repository.info(".", endRevision);
        // 开始版本号
        long startRevision = lastSVNDirEntry.getRevision() - Integer.valueOf(appConfigDto.getSvnMaxRevision());
        repository.log(new String[]{""}, startRevision, endRevision, true, true, svnLogEntry -> {
            if (svnLogEntry.getAuthor().equals(svnName)) {
                LogDto svnLogDto = new LogDto();
                logList.add(svnLogDto);
                svnLogDto.setVersion(svnLogEntry.getRevision());
                svnLogDto.setTime(CommonUtils.getCurrentDateTime1(svnLogEntry.getDate()));
                Map<String, SVNLogEntryPath> logMap = svnLogEntry.getChangedPaths();
                svnLogDto.setNum(logMap.size());
                List<String> pathList = new ArrayList<>();
                svnLogDto.setFile(pathList);
                Iterator<String> iterator = logMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    SVNLogEntryPath value = logMap.get(key);
                    String path = value.getPath().replace(appConfigDto.getSvnDeletePrefix(), "");
                    if ("D".equals(String.valueOf(value.getType()))) {
                        path += " 删除";
                    }
                    pathList.add(path + "\n");
                }
            }
        });
        Collections.sort(logList);
        if (logList.size() >= times) {
            return logList.subList(0, times);
        }
        return logList;
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
