package com.hoomoomoo.im.task;

import com.hoomoomoo.im.controller.FundInfoController;
import lombok.Data;

@Data
public class FundInfoTaskParam {

    private FundInfoController fundInfoController;

    public FundInfoTaskParam(FundInfoController fundInfoController) {
        this.fundInfoController = fundInfoController;
    }
}
