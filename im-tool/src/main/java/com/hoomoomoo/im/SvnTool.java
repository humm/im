package com.hoomoomoo.im;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

/**
 * @author hoomoomoo
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/08/23
 */
public class SvnTool {

    public static void main(String[] args) {
        System.out.println(update(new File("D:\\workspace\\ta5\\self-ta\\SELFTA\\数据库升级脚本")));
    }

    public static long update(File updateFile) {
        try {
            DAVRepositoryFactory.setup();
            ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
            // 实例化客户端管理类
            SVNClientManager ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options,
                    "humm23693", "Mm20200722");
            // 获得updateClient的实例
            SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
            updateClient.setIgnoreExternals(false);
            // 执行更新操作
            return updateClient.doUpdate(updateFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        } catch (SVNException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
