package com.x.base.core.project.tools;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class ZipTools {
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
                //System.out.println(name);
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
}
