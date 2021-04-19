package com.hoomoomoo.im.utils;


import com.hoomoomoo.im.dto.SvnLogDto;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 * @description svn工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    public static List<SvnLogDto> getSvnLog(int times) throws SVNException {
        final List<SvnLogDto> logList = new ArrayList<>();
        String svnUrl = "https://192.168.57.56/bank/depone/BTA6.0/trunk/Sources/app";
        String svnName = "humm23693";
        String svnpPssword = "MmMm20210216";
        // HEAD (the latest) revision
        long endRevision = -1;
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnName, svnpPssword);
        repository.setAuthenticationManager(authManager);
        // 最后一次提交记录
        SVNDirEntry lastSVNDirEntry = repository.info(".", endRevision);
        // 开始版本号
        long startRevision = lastSVNDirEntry.getRevision() - 500;
        repository.log(new String[]{""}, startRevision, endRevision, true, true, svnlogentry -> {
            if (svnlogentry.getAuthor().equals(svnName)) {
                SvnLogDto svnLogDto = new SvnLogDto();
                logList.add(svnLogDto);
                svnLogDto.setVersion(svnlogentry.getRevision());
                svnLogDto.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(svnlogentry.getDate()));
                Map<String, SVNLogEntryPath> logMap = svnlogentry.getChangedPaths();
                svnLogDto.setNum(logMap.size());
                List<String> pathList = new ArrayList<>();
                svnLogDto.setFile(pathList);
                Iterator<String> iterator = logMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    SVNLogEntryPath value = logMap.get(key);
                    String path = value.getPath().replace("/trunk", "");
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
