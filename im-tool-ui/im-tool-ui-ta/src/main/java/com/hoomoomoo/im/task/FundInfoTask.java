package com.hoomoomoo.im.task;

import java.util.concurrent.Callable;

public class FundInfoTask implements Callable<FundInfoTaskParam> {

    private FundInfoTaskParam fundInfoTaskParam;

    public FundInfoTask(FundInfoTaskParam fundInfoTaskParam) {
        this.fundInfoTaskParam = fundInfoTaskParam;
    }

    @Override
    public FundInfoTaskParam call() throws Exception {
        fundInfoTaskParam.getFundInfoController().generateScript();
        return null;
    }
}
