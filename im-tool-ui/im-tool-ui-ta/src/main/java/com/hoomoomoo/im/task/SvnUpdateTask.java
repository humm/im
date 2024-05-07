package com.hoomoomoo.im.task;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;

import java.util.concurrent.Callable;

public class SvnUpdateTask implements Callable<SvnUpdateTaskParam> {

    private SvnUpdateTaskParam svnUpdateTaskParam;

    public SvnUpdateTask(SvnUpdateTaskParam svnUpdateTaskParam) {
        this.svnUpdateTaskParam = svnUpdateTaskParam;
    }

    @Override
    public SvnUpdateTaskParam call() throws Exception {
        svnUpdateTaskParam.getSvnUpdateController().getSvnUpdate();
        return null;
    }
}
