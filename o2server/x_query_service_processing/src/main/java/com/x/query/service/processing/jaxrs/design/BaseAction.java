package com.x.query.service.processing.jaxrs.design;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.StringTools;

abstract class BaseAction extends StandardJaxrsAction {

    protected Map<Integer, String> patternLines(String id, String keyword, String content, Boolean caseSensitive, Boolean matchWholeWord, Boolean matchRegExp){
        Map<Integer, String> map = new LinkedHashMap<>();
        File file = readFile(id, content);
        if (file!=null){
            try (RandomAccessFile randomFile = new RandomAccessFile(file, "r")) {
                int curReadLine = 0;
                String tmp = "";
                while ((tmp = randomFile.readLine()) != null) {
                    curReadLine++;
                    byte[] bytes = tmp.getBytes("ISO8859-1");
                    String lineStr = new String(bytes);
                    if(StringUtils.isNotBlank(lineStr) && lineStr.length()>=keyword.length()){
                        if(StringTools.matchKeyword(keyword, lineStr, caseSensitive, matchWholeWord, matchRegExp)){
                            if(lineStr.length()>500){
                                lineStr = lineStr.substring(0, 500);
                            }
                            map.put(curReadLine, lineStr);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    private synchronized File readFile(String id, String content){
        try {
            File searchFile = new File(Config.base(), "local/search");
            FileTools.forceMkdir(searchFile);
            File file = new File(searchFile.getAbsolutePath(), id+".txt");
            if (!file.exists()){
                FileUtils.writeByteArrayToFile(file, content.getBytes(DefaultCharset.name));
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
