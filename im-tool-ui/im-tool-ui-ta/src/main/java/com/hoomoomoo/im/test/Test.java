package com.hoomoomoo.im.test;


import com.hoomoomoo.im.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {

    private static int num;

    public static void main(String[] args) throws IOException {
        File file = new File("E:\\workspace\\ta6\\server\\ta-web-manager-fund-core\\");
        readFile(file);
        System.out.println(num);
    }

    private static void readFile(File file) throws IOException {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (File item : list) {
                readFile(item);
            }
        } else {
            List<String> content = FileUtils.readNormalFile(file.getAbsolutePath(), false);
            for (String line : content) {
                if (line.contains("queryLogDataService")) {
                    num++;
                    System.out.println(file.getAbsolutePath());
                    break;
                }
            }
        }
    }
}
