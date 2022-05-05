package com.x.base.core.project.tools;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip工具类
 * @author sword
 */
public class ZipTools {
    private static final int BUFFER_SIZE = 2 * 1024;

    public static void unZip(File source, List<String> subs, File dist, boolean asNew, Charset charset) {
        try{
            ZipFile zipFile = new ZipFile(source);
            if(charset == null){
                charset = DefaultCharset.charset;
            }
            zipFile.setCharset(charset);
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
            for (FileHeader fileHeader : fileHeaderList){
                if(isFromExtraData(fileHeader) && DefaultCharset.charset.name() == charset.name()){
                    unZip(source, subs, dist, asNew, DefaultCharset.charset_gbk);
                    return;
                }
                String name = fileHeader.getFileName();
                if (name.length() < 2) {
                    continue;
                }
                if (subs != null) {
                    boolean flag = false;
                    for (String sub : subs) {
                        if (StringUtils.startsWith(name, sub)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                }
                if (fileHeader.isDirectory()) {
                    File dir = new File(dist, name);
                    if (dir.exists() && name.indexOf("/") == name.lastIndexOf("/") && asNew) {
                        FileUtils.cleanDirectory(dir);
                    }
                    FileUtils.forceMkdir(dir);
                } else {
                    zipFile.extractFile(fileHeader, dist.getAbsolutePath());
                }
            }
            fileHeaderList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isFromExtraData(FileHeader fileHeader) {
        if(fileHeader.getExtraDataRecords()!=null){
            for (ExtraDataRecord extraDataRecord : fileHeader.getExtraDataRecords()) {
                long identifier = extraDataRecord.getHeader();
                if (identifier == 0x7075) {
                    byte[] bytes = extraDataRecord.getData();
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    byte version = buffer.get();
                    assert (version == 1);
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * 压缩成ZIP 方法1
     * @param sourceFile 待压缩文件夹
     * @param out        压缩包输出流
     * @param fileList   压缩的文件列表，可以为null
     * @return boolean
     */
    public static boolean toZip(File sourceFile, OutputStream out, List<String> fileList) {
        try (ZipOutputStream zos = new ZipOutputStream(out)){
            compress(sourceFile, zos, "", fileList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩成ZIP 方法2
     * @param srcFiles 需要压缩的文件列表
     * @param out           压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, List<String> fileList) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            if(StringUtils.isBlank(name)) {
                name = sourceFile.getName();
            }
            if(sourceFile != null){
                if(name.startsWith(File.separator)) {
                    fileList.add(name.substring(1));
                }else{
                    fileList.add(name);
                }
            }
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                if(StringUtils.isNotBlank(name)) {
                    zos.putNextEntry(new ZipEntry(name + File.separator ));
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    compress(file, zos, name + File.separator  + file.getName(), fileList);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        File zipFile = new File("/Users/chengjian/dev/tmp/test.zip");
        List<String> list = new ArrayList<>();
        ZipTools.toZip(new File("/Users/chengjian/dev/tmp/test"), new FileOutputStream(zipFile), list);
        System.out.println(1);
    }
}
