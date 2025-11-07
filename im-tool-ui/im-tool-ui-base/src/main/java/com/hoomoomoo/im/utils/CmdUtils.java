package com.hoomoomoo.im.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/3/31
 */
public class CmdUtils {

    /**
     * @param dir
     * @param command
     * @return
     */
    public static String exe(String dir, String command) {
        StringBuilder content = new StringBuilder();
        try {
            // 创建 ProcessBuilder 对象
            ProcessBuilder processBuilder = new ProcessBuilder();
            // 设置命令执行的目录，这里可以修改为你需要的目录路径
            processBuilder.directory(new File(dir));
            // 设置要执行的命令，例如 dir 命令
            processBuilder.command("cmd.exe", "/c", command);
            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                content.append(line).append(STR_NEXT_LINE);
            }
            // 等待进程执行结束
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LoggerUtils.error(e);
        }
        return content.toString();
    }


    public static String exeByFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            // 获取 Runtime 实例
            Runtime runtime = Runtime.getRuntime();
            // 执行 .bat 文件
            Process process = runtime.exec(filePath);
            // 获取命令执行的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(STR_NEXT_LINE);
            }
            // 等待命令执行完成
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LoggerUtils.error(e);
        }
        return content.toString();
    }

}
