package com.hoomoomoo.im.test;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.hoomoomoo.im.consts.BaseConst.STR_BRACKETS_3_LEFT;
import static com.hoomoomoo.im.consts.BaseConst.STR_BRACKETS_3_RIGHT;

public class Test {

    public static void main(String[] args) throws Exception {
        String taskName = "【缺陷:FUNDTAVI-22939】【赎回持有期限制(按投资者)方案设置】赎回持有期限制(按投资者)方案设置中 赎回至少天数只能输入999，和基金信息设置该字段不一致";
        System.out.println(taskName.substring(taskName.indexOf(STR_BRACKETS_3_LEFT) , taskName.indexOf(STR_BRACKETS_3_RIGHT) + 1));
    }
}
