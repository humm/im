package com.hoomoomoo.im.utils;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

import static com.hoomoomoo.im.ImFileUtils.ERROR_COLOR;
import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;

/**
 * @author
 * @description svn工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/10
 */
public class SvnUtils {

    private final static String SVN_ERROR_CODE_E170001 = "E170001";
    private final static String SVN_ERROR_CODE_E175002 = "E175002";
    private final static String SVN_ERROR_CODE_E155004 = "E155004";
    private static Integer SVN_ERROR_TIMES = 0;

    /**
     * svn 更新
     *
     * @param svnClientManager
     * @param workspace
     * @param svnRevision
     * @param svnDepth
     * @author: hoomoomoo
     * @date: 2020/09/11
     * @return:
     */
    private static long update(boolean singleColor, SVNClientManager svnClientManager, File workspace,
                               SVNRevision svnRevision, SVNDepth svnDepth) {
        SVNUpdateClient svnUpdateClient = svnClientManager.getUpdateClient();
        svnUpdateClient.setIgnoreExternals(false);
        try {
            Long version = svnUpdateClient.doUpdate(workspace, svnRevision, svnDepth, false, false);
            SVN_ERROR_TIMES = 0;
            return version;
        } catch (SVNException e) {
            CommonUtils.println(singleColor, SYMBOL_EMPTY, SYMBOL_EMPTY, false);
            if (e.toString().contains(SVN_ERROR_CODE_E170001)) {
                CommonUtils.println(singleColor, "svn同步异常 请检查配置项[ svn.username ] [ svn.password ]", ERROR_COLOR);
            } else if (e.toString().contains(SVN_ERROR_CODE_E175002)) {
                CommonUtils.println(singleColor, "svn同步异常 请检查网络连接是否正常;svn目录是否存在", ERROR_COLOR);
            } else if (e.toString().contains(SVN_ERROR_CODE_E155004)) {
                if (SVN_ERROR_TIMES == 0) {
                    SVN_ERROR_TIMES++;
                    if (cleanUp(svnClientManager, workspace)) {
                        update(singleColor, svnClientManager, workspace, svnRevision, svnDepth);
                    } else {
                        CommonUtils.println(singleColor, String.format("svn同步异常 请至工作目录[ %s ]执行cleanUp并选择 Break write " +
                                "locks", workspace.getAbsolutePath()), ERROR_COLOR);
                    }
                } else {
                    CommonUtils.println(singleColor, String.format("svn同步异常 请至工作目录[ %s ]执行cleanUp并选择 Break write " +
                            "locks", workspace.getAbsolutePath()), ERROR_COLOR);
                }
            } else {
                e.printStackTrace();
            }
        }
        return 0L;
    }

    /**
     * svn 更新
     *
     * @param username
     * @param password
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/12
     * @return:
     */
    public static long update(boolean singleColor, String username, String password, String workspace) {
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, username, password);
        return update(singleColor, svnClientManager, new File(workspace), SVNRevision.HEAD, SVNDepth.INFINITY);
    }

    /**
     * svn 解锁
     *
     * @param svnClientManager
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/20
     * @return:
     */
    private static boolean cleanUp(SVNClientManager svnClientManager, File workspace) {
        SVNWCClient svnwcClient = svnClientManager.getWCClient();
        try {
            svnwcClient.doCleanup(workspace);
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * svn 解锁
     *
     * @param username
     * @param password
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/20
     * @return:
     */
    public static boolean cleanUp(String username, String password, String workspace) {
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, username, password);
        return cleanUp(svnClientManager, new File(workspace));
    }
}
