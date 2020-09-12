package com.hoomoomoo.im.utils;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    /**
     * svn 更新
     *
     * @param svnClientManager
     * @param workspacePath
     * @param svnRevision
     * @param svnDepth
     * @author: humm23693
     * @date: 2020/09/11
     * @return:
     */
    private static Long update(SVNClientManager svnClientManager, File workspacePath,
                               SVNRevision svnRevision, SVNDepth svnDepth) {
        SVNUpdateClient svnUpdateClient = svnClientManager.getUpdateClient();
        svnUpdateClient.setIgnoreExternals(false);
        try {
            return svnUpdateClient.doUpdate(workspacePath, svnRevision, svnDepth, false, false);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * svn 更新
     *
     * @param username
     * @param password
     * @param workspace
     * @author: humm23693
     * @date: 2020/09/12
     * @return:
     */
    public static Long update(String username, String password, String workspace) {
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, username, password);
        return update(svnClientManager, new File(workspace), SVNRevision.HEAD, SVNDepth.INFINITY);
    }

}
