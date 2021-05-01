package com.hoomoomoo.im.utils;


import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnLogDto;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.*;


/**
 * @author
 * @description svn工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    public static List<SvnLogDto> getSvnLog(int times) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        final List<SvnLogDto> logList = new ArrayList<>();
        String svnUrl = appConfigDto.getSvnUrl();
        String svnName = appConfigDto.getSvnUsername();
        String svnpPssword = appConfigDto.getSvnPassword();
        // HEAD (the latest) revision
        long endRevision = -1;
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnName, svnpPssword);
        repository.setAuthenticationManager(authManager);
        // 最后一次提交记录
        SVNDirEntry lastSVNDirEntry = repository.info(".", endRevision);
        // 开始版本号
        long startRevision = lastSVNDirEntry.getRevision() - Integer.valueOf(appConfigDto.getSvnMaxRevision());
        repository.log(new String[]{""}, startRevision, endRevision, true, true, svnlogentry -> {
            if (svnlogentry.getAuthor().equals(svnName)) {
                SvnLogDto svnLogDto = new SvnLogDto();
                logList.add(svnLogDto);
                svnLogDto.setVersion(svnlogentry.getRevision());
                svnLogDto.setTime(CommonUtils.getCurrentDateTime1(svnlogentry.getDate()));
                Map<String, SVNLogEntryPath> logMap = svnlogentry.getChangedPaths();
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
}
