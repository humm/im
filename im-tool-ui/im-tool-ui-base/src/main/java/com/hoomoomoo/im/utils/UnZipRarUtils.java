package com.hoomoomoo.im.utils;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.hoomoomoo.im.consts.BaseConst;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import static com.hoomoomoo.im.consts.BaseConst.NAME_CONFIG_FILE_SAME;
import static com.hoomoomoo.im.consts.BaseConst.NAME_CONFIG_LICENSE_DATE;


/**
 * @author hoomoomoo
 * @description 解压工具类
 * @package im
 * @date 2020/08/23
 */
public class UnZipRarUtils {

    private final static String ENCODING_GBK = "GBK";
    private final static Integer NUM_1024 = 1024;
    private final static String PATTERN = "yyyyMMddHHmmss";

    /**
     * 解压Zip
     *
     * @param file
     * @param outDir
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    public static void unZip(File file, String outDir) throws Exception {
        if (!file.exists()) {
            throw new Exception("解压文件不存在");
        }
        Long fileCreateDate = 0L;
        Long min = 0L;
        Long max = 0L;
        ZipFile zipFile = new ZipFile(file, ENCODING_GBK);
        Enumeration e = zipFile.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) e.nextElement();
            String fileName = zipEntry.getName();
            if (fileName.startsWith("com/hoomoomoo") || fileName.startsWith("conf")) {
                Long fileDate = zipEntry.getLastModifiedDate().getTime();
                if (fileCreateDate == 0) {
                    fileCreateDate = fileDate;
                    min = fileCreateDate - 3 * 60 * 1000;
                    max = fileCreateDate + 3 * 60 * 1000;
                } else {
                    if (fileDate < min || fileDate > max) {
                        LoggerUtils.info(String.format("文件[ %s ]已被修改", fileName));
                        throw new Exception("文件一致性校验不通过,请勿修改");
                    }
                }
            }
            if (zipEntry.isDirectory()) {
                String name = zipEntry.getName();
                name = name.substring(0, name.length() - 1);
                File f = new File(outDir + name);
                f.mkdirs();
            } else {
                File f = new File(outDir + zipEntry.getName());
                f.getParentFile().mkdirs();
                f.createNewFile();
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(f);
                int length;
                byte[] b = new byte[1024];
                while ((length = is.read(b, 0, NUM_1024)) != -1) {
                    fos.write(b, 0, length);
                }
                is.close();
                fos.close();
            }
        }
        if (zipFile != null) {
            zipFile.close();
        }
        LoggerUtils.info(String.format(BaseConst.MSG_CHECK, NAME_CONFIG_FILE_SAME));
    }

    /**
     * 解压Rar
     *
     * @param rarFile
     * @param outDir
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    public static void unRar(File rarFile, String outDir) throws Exception {
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()) {
            outFileDir.mkdirs();
        }
        Archive archive = new Archive(new FileInputStream(rarFile));
        FileHeader fileHeader = archive.nextFileHeader();
        while (fileHeader != null) {
            if (fileHeader.isDirectory()) {
                fileHeader = archive.nextFileHeader();
                continue;
            }
            File out = new File(outDir + fileHeader.getFileName());
            if (!out.exists()) {
                if (!out.getParentFile().exists()) {
                    out.getParentFile().mkdirs();
                }
                out.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(out);
            archive.extractFile(fileHeader, os);
            os.close();
            fileHeader = archive.nextFileHeader();
        }
        archive.close();
    }
}
