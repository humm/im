package com.hoomoomoo.im.main;

import java.io.IOException;

/**
 * 检查所有
 */
public class CheckAll {

    public static void main(String[] args) throws IOException {
        CheckWebFundCoreCallParameterCore.executeCheck();
        CheckFrontMsg.executeCheck();
        CheckQueryConditionTitle.executeCheck();
        CheckConfirmColumn.executeCheck();
        CheckImportFunctionNotUse.executeCheck();
    }
}
