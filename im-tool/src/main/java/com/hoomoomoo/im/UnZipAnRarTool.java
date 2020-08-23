package com.hoomoomoo.im;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.*;
import java.util.Enumeration;

/**
 * @author hoomoomoo
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/08/23
 */
public class UnZipAnRarTool {

    /**
     * 解压Zip
     *
     * @param zipFile
     * @param outDir
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    public static void unZip(File zipFile, String outDir) throws Exception {
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()) {
            outFileDir.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration enumeration = zip.getEntries(); enumeration.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) enumeration.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            // 处理压缩文件包含文件夹的情况
            if (entry.isDirectory()) {
                File fileDir = new File(outDir + zipEntryName);
                fileDir.mkdir();
                continue;
            }
            File file = new File(outDir, zipEntryName);
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        }
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
