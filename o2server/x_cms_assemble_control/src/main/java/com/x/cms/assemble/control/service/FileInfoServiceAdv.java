package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

/**
 * 对文档附件文件信息进行管理的服务类（高级） 高级服务器可以利用Service完成事务控制
 *
 * @author O2LEE
 */
public class FileInfoServiceAdv {

	private FileInfoService fileInfoService = new FileInfoService();

	public FileInfo get(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return fileInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 只是删除一条文件附件信息
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteFileInfo(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id is null!");
		}
		FileInfo file = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(FileInfo.class);
			file = emc.find(id, FileInfo.class);
			if (file != null) {
				emc.remove(file, CheckRemoveType.all);
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public FileInfo saveAttachment(String docId, FileInfo attachment) throws Exception {
		if (StringUtils.isEmpty(docId)) {
			throw new Exception("docId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(docId, Document.class);
			if (document != null) {
				emc.beginTransaction(FileInfo.class);
				emc.persist(attachment, CheckPersistType.all);
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		return attachment;
	}

	public FileInfo updateAttachmentInfo(String id, FileInfo fileInfo) throws Exception {
		FileInfo fileInfo_old = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			fileInfo_old = emc.find(id, FileInfo.class);
			if (null == fileInfo_old) {
				throw new Exception("file info not exists.ID=" + id);
			}
			if (fileInfo == null) {
				return fileInfo_old;
			} else {
				if (ListTools.isNotEmpty(fileInfo.getReadIdentityList())) {
					fileInfo_old.setReadIdentityList(fileInfo.getReadIdentityList());
				} else {
					fileInfo_old.setReadIdentityList(new ArrayList<>());
				}
				if (ListTools.isNotEmpty(fileInfo.getReadUnitList())) {
					fileInfo_old.setReadUnitList(fileInfo.getReadUnitList());
				} else {
					fileInfo_old.setReadUnitList(new ArrayList<>());
				}
				if (ListTools.isNotEmpty(fileInfo.getEditIdentityList())) {
					fileInfo_old.setEditIdentityList(fileInfo.getEditIdentityList());
				} else {
					fileInfo_old.setEditIdentityList(new ArrayList<>());
				}
				if (ListTools.isNotEmpty(fileInfo.getEditUnitList())) {
					fileInfo_old.setEditUnitList(fileInfo.getEditUnitList());
				} else {
					fileInfo_old.setEditUnitList(new ArrayList<>());
				}
				if (ListTools.isNotEmpty(fileInfo.getControllerIdentityList())) {
					fileInfo_old.setControllerIdentityList(fileInfo.getControllerIdentityList());
				} else {
					fileInfo_old.setControllerIdentityList(new ArrayList<>());
				}
				if (ListTools.isNotEmpty(fileInfo.getControllerUnitList())) {
					fileInfo_old.setControllerUnitList(fileInfo.getControllerUnitList());
				} else {
					fileInfo_old.setControllerUnitList(new ArrayList<>());
				}
				// 密级标识
				fileInfo_old.setObjectSecurityClearance(fileInfo.getObjectSecurityClearance());
				emc.beginTransaction(FileInfo.class);
				emc.check(fileInfo_old, CheckPersistType.all);
				emc.commit();
			}
		}
		return fileInfo_old;
	}

	public FileInfo updateAttachment(String docId, String old_attId, FileInfo attachment, StorageMapping mapping)
			throws Exception {
		if (StringUtils.isEmpty(docId)) {
			throw new Exception("docId is null!");
		}
		if (StringUtils.isEmpty(old_attId)) {
			throw new Exception("old_attId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(docId, Document.class);
			FileInfo old_fileInfo = emc.find(old_attId, FileInfo.class);
			if (document != null) {
				emc.beginTransaction(FileInfo.class);
				old_fileInfo.setLastUpdateTime(new Date());
				old_fileInfo.setExtension(attachment.getExtension());
				old_fileInfo.setName(attachment.getName());
				old_fileInfo.setFileName(attachment.getFileName());
				old_fileInfo.setStorage(mapping.getName());
				old_fileInfo.setAppId(document.getAppId());
				old_fileInfo.setCategoryId(document.getCategoryId());
				old_fileInfo.setDocumentId(document.getId());
				old_fileInfo.setCreatorUid(attachment.getCreatorUid());
				old_fileInfo.setSite(attachment.getSite());
				old_fileInfo.setFileHost(attachment.getFileHost());
				old_fileInfo.setFileType("ATTACHMENT");
				old_fileInfo.setFileExtType(attachment.getFileExtType());
				old_fileInfo.setFilePath(attachment.getFilePath());
				old_fileInfo.setType(attachment.getType());
				old_fileInfo.setText(attachment.getText());
				old_fileInfo.setLength(attachment.getLength());
				emc.check(old_fileInfo, CheckPersistType.all);
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		return attachment;
	}

	public List<String> listIdsWithDocId(String documentId) throws Exception {
		if (StringUtils.isEmpty(documentId)) {
			throw new Exception("documentId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.getFileInfoFactory().listAttachmentByDocument(documentId);
		} catch (Exception e) {
			throw e;
		}
	}

}
