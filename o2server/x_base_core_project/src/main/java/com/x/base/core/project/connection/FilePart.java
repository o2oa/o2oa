package com.x.base.core.project.connection;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class FilePart extends GsonPropertyObject {

    public FilePart(String fileName, byte[] bytes, String contentType, String name) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.contentType = contentType;
        this.name = name;
    }

    private String name;

    private String fileName;

    private byte[] bytes;

    private String contentType;

    public String getName() {
        return StringUtils.isEmpty(name) ? "file_" + StringTools.uniqueToken() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}