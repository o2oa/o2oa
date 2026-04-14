package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileUtility{

    public static FileType GetFileType(String fileName){
        String ext = getFileExtension(fileName).toLowerCase();

        if (extDocumentList.contains(ext)) {
            return FileType.word;
        }
        if (extSpreadsheetList.contains(ext)) {
            return FileType.cell;
        }
        if (extPresentationList.contains(ext)) {
            return FileType.slide;
        }
        return FileType.word;
    }

    private static final List<String> extDocumentList = Arrays.asList
            (
                    ".doc", ".docx", ".docm",
                    ".dot", ".dotx", ".dotm",
                    ".odt", ".fodt", ".ott", ".rtf", ".txt",
                    ".html", ".htm", ".mht",
                    ".pdf", ".djvu", ".fb2", ".epub", ".xps",
                    ".wps", ".wpt"
            );

    private static final List<String> extSpreadsheetList = Arrays.asList
            (
                    ".xls", ".xlsx", ".xlsm", ".xml",
                    ".xlt", ".xltx", ".xltm", ".xlsb",
                    ".ods", ".fods", ".ots", ".csv",
                    ".et", ".ett"
            );

    private static final List<String> extPresentationList = Arrays.asList
            (
                    ".pps", ".ppsx", ".ppsm",
                    ".ppt", ".pptx", ".pptm",
                    ".pot", ".potx", ".potm",
                    ".odp", ".fodp", ".otp",
                    ".dps", ".dpt"
            );


    public static String getFileExtension(String fileName){
        String fileExt = fileName.substring(fileName.lastIndexOf("."));
        return fileExt.toLowerCase();
    }

    /**
     * 将文件转换成byte数组
     * @param tradeFile
     * @return
     */
    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

}
