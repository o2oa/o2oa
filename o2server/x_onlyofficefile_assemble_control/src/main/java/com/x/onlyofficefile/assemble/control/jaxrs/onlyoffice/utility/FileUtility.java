package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;
import com.x.base.core.project.config.Config;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileType;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtility
{

    public static FileType GetFileType(String fileName)
    {
        String ext = getFileExtension(fileName).toLowerCase();

        if (ExtsDocument.contains(ext))
            return FileType.word;

        if (ExtsSpreadsheet.contains(ext))
            return FileType.cell;

        if (ExtsPresentation.contains(ext))
            return FileType.slide;

        return FileType.word;
    }

    public static List<String> ExtsDocument = Arrays.asList
            (
                    ".doc", ".docx", ".docm",
                    ".dot", ".dotx", ".dotm",
                    ".odt", ".fodt", ".ott", ".rtf", ".txt",
                    ".html", ".htm", ".mht",
                    ".pdf", ".djvu", ".fb2", ".epub", ".xps",
                    ".wps", ".wpt"
            );

    public static List<String> ExtsSpreadsheet = Arrays.asList
            (
                    ".xls", ".xlsx", ".xlsm", ".xml",
                    ".xlt", ".xltx", ".xltm", ".xlsb",
                    ".ods", ".fods", ".ots", ".csv",
                    ".et", ".ett"
            );

    public static List<String> ExtsPresentation = Arrays.asList
            (
                    ".pps", ".ppsx", ".ppsm",
                    ".ppt", ".pptx", ".pptm",
                    ".pot", ".potx", ".potm",
                    ".odp", ".fodp", ".otp",
                    ".dps", ".dpt"
            );


    public static String GetFileName(String url)
    {
        if (url == null) return null;
        String tempstorage;
		try {
			 tempstorage = ConfigManager.init(Config.base()).getDocserviceTempstorage();
			   if (!tempstorage.isEmpty() && url.startsWith(tempstorage))
		        {
		            Map<String, String> params = GetUrlParams(url);
		            return params == null ? null : params.get("filename");
		        }

		        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
		        return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		  return "";
    }

    public static String GetFileNameWithoutExtension(String url)
    {
        String fileName = GetFileName(url);
        if (fileName == null) return null;
        String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        return fileNameWithoutExt;
    }

    public static String getFileExtension(String url)
    {
        String fileName = GetFileName(url);
        if (fileName == null) return null;
        String fileExt = fileName.substring(fileName.lastIndexOf("."));
        return fileExt.toLowerCase();
    }

    public static Map<String, String> GetUrlParams(String url)
    {
        try
        {
            String query = new URL(url).getQuery();
            String[] params = query.split("&");
            Map<String, String> map = new HashMap<>();
            for (String param : params)
            {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
            return map;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * 将文件转换成byte数组
     * @param filePath
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

    public static String File2String(String filePath){
         File file = new File(filePath);
         StringBuilder result = new StringBuilder();
        try{
        	InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            int lineNum = 1;
            while((s = br.readLine())!=null){
                result.append("第" + lineNum + "行：" +s + System.lineSeparator());
                lineNum = lineNum + 1;
            }

            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void Delete(File f) throws Exception {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
            	Delete(c);
        }
        if (!f.delete()) {
        }
    }

    public static String getFileTypeByName(String fileName) {
        String[] arr = fileName.split("\\.");
        return arr[arr.length - 1];
    }

}
