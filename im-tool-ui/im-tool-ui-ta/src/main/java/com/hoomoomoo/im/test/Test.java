package com.hoomoomoo.im.test;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {

    public static void main(String[] args) throws Exception {
        int second = 150 * 1000 / 1000;
        int minute = second / 60;
        if (minute > 0) {
            second = second % 60;
        }
        System.out.println(minute + " " + second);
    }
}
