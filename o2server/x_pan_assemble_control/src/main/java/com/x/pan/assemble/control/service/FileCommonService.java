package com.x.pan.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author sword
 */
public class FileCommonService {

    private static final long TWO_G = 1024L*1024L*1024L*2L;

    /**
     * 下载附件并打包为zip
     * @param emc
     * @param files
     * @param folders
     * @throws Exception
     */
    public void downToZip(EntityManagerContainer emc, List<Attachment2> files, List<Folder2> folders, OutputStream os) throws Exception {
        Map<String, OriginFile> filePathMap = new HashMap<>();
        List<String> emptyFolderList = new ArrayList<>();
        Business business = new Business( emc );
        /* 生成zip压缩文件内的目录结构 */
        if(folders!=null && !folders.isEmpty()) {
            for (Folder2 folder : folders) {
                String parentPath = "";
                generateFolderPath(business, emptyFolderList, filePathMap, parentPath, folder);
            }
        }
        if(files!=null) {
            for (Attachment2 att : files) {
                if(att.getLength().longValue() < TWO_G) {
                    String parentPath = "";
                    generateFilePath(business, filePathMap, parentPath, att);
                }
            }
        }
        ZipOutputStream zos = null;
        try{
            zos = new ZipOutputStream(os);
            for (Map.Entry<String, OriginFile> entry : filePathMap.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        entry.getValue().getStorage());
                try (ByteArrayOutputStream os1 = new ByteArrayOutputStream()) {
                    entry.getValue().readContent(mapping, os1);
                    byte[] bs = os1.toByteArray();
                    os1.close();
                    zos.write(bs);
                }
            }
            // 往zip里添加空文件夹
            for (String emptyFolder : emptyFolderList) {
                zos.putNextEntry(new ZipEntry(emptyFolder));
            }
        }finally {
            if(zos!=null){
                zos.close();
            }
        }
    }

    /**
     * 下载附件并打包为zip
     * @param emc
     * @param files
     * @param folders
     * @throws Exception
     */
    public void downToZip2(EntityManagerContainer emc, EffectivePerson effectivePerson, List<Attachment3> files, List<Folder3> folders, OutputStream os) throws Exception {
        Map<String, OriginFile> filePathMap = new HashMap<>();
        List<String> emptyFolderList = new ArrayList<>();
        Business business = new Business( emc );
        boolean readFlag = !business.controlAble(effectivePerson);
        if(readFlag){
            readFlag = business.getSystemConfig().getReadPermissionDown();
        }
        /* 生成zip压缩文件内的目录结构 */
        if(folders!=null) {
            for (Folder3 folder : folders) {
                String parentPath = "";
                generateFolderPath2(business, emptyFolderList, filePathMap, parentPath, folder, readFlag, effectivePerson.getDistinguishedName());
            }
        }
        if(files!=null) {
            for (Attachment3 att : files) {
                if(att.getLength().longValue() < TWO_G) {
                    String parentPath = "";
                    generateFilePath2(business, filePathMap, parentPath, att);
                }
            }
        }
        ZipOutputStream zos = null;
        try{
            zos = new ZipOutputStream(os);
            for (Map.Entry<String, OriginFile> entry : filePathMap.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        entry.getValue().getStorage());
                try (ByteArrayOutputStream os1 = new ByteArrayOutputStream()) {
                    entry.getValue().readContent(mapping, os1);
                    byte[] bs = os1.toByteArray();
                    os1.close();
                    zos.write(bs);
                }
            }
            // 往zip里添加空文件夹
            for (String emptyFolder : emptyFolderList) {
                zos.putNextEntry(new ZipEntry(emptyFolder));
            }
        }finally {
            if(zos!=null){
                zos.close();
            }
        }
    }

    private void generateFolderPath(Business business, List<String> emptyFolderList, Map<String, OriginFile> filePathMap, String parentPath, Folder2 folder) throws Exception {
        if (parentPath.length() > 0) {
            parentPath = parentPath + File.separator + folder.getName();
        } else {
            parentPath = folder.getName();
        }

        boolean emptyFolder = true;

        List<Folder2> subfolders =  business.folder2().listSubDirect1(folder.getId(), FileStatus.VALID.getName());
        for (Folder2 subfolder : subfolders) {
            emptyFolder = false;
            generateFolderPath(business, emptyFolderList, filePathMap, parentPath, subfolder);
        }

        List<Attachment2> subfiles = business.attachment2().listWithFolder2(folder.getId(), FileStatus.VALID.getName());
        for (Attachment2 subfile : subfiles) {
            if(subfile.getLength().longValue() < TWO_G) {
                emptyFolder = false;
                generateFilePath(business, filePathMap, parentPath, subfile);
            }
        }

        if (emptyFolder) {
            parentPath += File.separator;
            emptyFolderList.add(parentPath);
        }
    }

    private void generateFolderPath2(Business business, List<String> emptyFolderList, Map<String, OriginFile> filePathMap,
                                     String parentPath, Folder3 folder, boolean readFlag, String person) throws Exception {
        if (parentPath.length() > 0) {
            parentPath = parentPath + File.separator + folder.getName();
        } else {
            parentPath = folder.getName();
        }

        boolean emptyFolder = true;

        List<Folder3> subFolders =  business.folder3().listSubDirect1(folder.getId(), FileStatus.VALID.getName());
        for (Folder3 subfolder : subFolders) {
            if(readFlag) {
                if(business.folder3().isZoneReader(subfolder.getId(), person)) {
                    emptyFolder = false;
                    generateFolderPath2(business, emptyFolderList, filePathMap, parentPath, subfolder, true, person);
                }
            }else{
                emptyFolder = false;
                generateFolderPath2(business, emptyFolderList, filePathMap, parentPath, subfolder, false, person);
            }
        }

        List<Attachment3> subFiles = business.attachment3().listWithFolder2(folder.getId(), FileStatus.VALID.getName());
        for (Attachment3 subFile : subFiles) {
            if(subFile.getLength().longValue() < TWO_G) {
                emptyFolder = false;
                generateFilePath2(business, filePathMap, parentPath, subFile);
            }
        }

        if (emptyFolder) {
            // 需要在路径后面加一个分隔符才会生成空文件夹
            parentPath += File.separator;
            emptyFolderList.add(parentPath);
        }
    }

    private void generateFilePath(Business business, Map<String, OriginFile> filePathMap, String parentPath, Attachment2 att) throws Exception {
        String filename = att.getName();
        String filePath;
        if (parentPath.length() > 0) {
            filePath = parentPath + File.separator + filename;
        } else {
            filePath = filename;
        }
        OriginFile originFile = business.entityManagerContainer().find(att.getOriginFile(), OriginFile.class);
        if(originFile!=null) {
            filePathMap.put(filePath, originFile);
        }
    }

    private void generateFilePath2(Business business, Map<String, OriginFile> filePathMap, String parentPath, Attachment3 att) throws Exception {
        String filename = att.getName();
        String filePath;
        if (parentPath.length() > 0) {
            filePath = parentPath + File.separator + filename;
        } else {
            filePath = filename;
        }
        OriginFile originFile = business.entityManagerContainer().find(att.getOriginFile(), OriginFile.class);
        if(originFile!=null) {
            filePathMap.put(filePath, originFile);
        }
    }
}
