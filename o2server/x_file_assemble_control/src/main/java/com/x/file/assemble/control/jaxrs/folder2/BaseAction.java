package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean exist(Business business, EffectivePerson effectivePerson, String name, String superior,
			String excludeId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Folder_.name), name));
		p = cb.and(p, cb.equal(root.get(Folder_.superior), StringUtils.trimToEmpty(superior)));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Folder_.id), excludeId));
		}
		cq.select(cb.count(root)).where(p);
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected void setCount(Business business, AbstractWoFolder wo) throws Exception {
		List<String> ids = business.attachment().listWithFolder(wo.getId());
		long count = 0;
		long size = 0;
		for (Attachment o : business.entityManagerContainer().fetch(ids, Attachment.class,
				ListTools.toList(Attachment.length_FIELDNAME))) {
			count++;
			size += o.getLength();
		}
		wo.setAttachmentCount(count);
		wo.setSize(size);
		wo.setFolderCount(business.folder().countSubDirect(wo.getId()));
	}

	/**
	 * 下载附件并打包为zip
	 * @param emc
	 * @param business
	 * @param files
	 * @param folders
	 * @throws Exception
	 */
	protected void downToZip(EntityManagerContainer emc, Business business, List<Attachment2> files, List<Folder2> folders, OutputStream os) throws Exception {
		Map<String, OriginFile> filePathMap = new HashMap<>();
		List<String> emptyFolderList = new ArrayList<>();
		/* 生成zip压缩文件内的目录结构 */
		if(folders!=null) {
			for (Folder2 folder : folders) {
				String parentPath = ""; // 初始路径为空
				generateFolderPath(emc, business, emptyFolderList, filePathMap, parentPath, folder);
			}
		}
		if(files!=null) {
			for (Attachment2 att : files) {
				String parentPath = ""; // 初始路径为空
				generateFilePath(emc, filePathMap, parentPath, att);
			}
		}
		ZipOutputStream zos = null;
		try{
			zos = new ZipOutputStream(os);
			for (Entry<String, OriginFile> entry : filePathMap.entrySet()) {
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

	private void generateFolderPath(EntityManagerContainer emc, Business business, List<String> emptyFolderList,Map<String, OriginFile> filePathMap, String parentPath, Folder2 folder) throws Exception {
		if (parentPath.length() > 0) {
			parentPath = parentPath + File.separator + folder.getName();
		} else {
			parentPath = folder.getName();
		}

		boolean emptyFolder = true;

		List<Folder2> subfolders =  business.folder2().listSubDirect1(folder.getId());
		for (Folder2 subfolder : subfolders) {
			emptyFolder = false;
			generateFolderPath(emc ,business, emptyFolderList, filePathMap, parentPath, subfolder);
		}

		List<Attachment2> subfiles = business.attachment2().listWithFolder2(folder.getId(),"正常");
		for (Attachment2 subfile : subfiles) {
			emptyFolder = false;
			generateFilePath(emc, filePathMap, parentPath, subfile);
		}

		if (emptyFolder) {
			parentPath += File.separator;// 需要在路径后面加一个分隔符才会生成空文件夹
			emptyFolderList.add(parentPath);
		}
	}

	private void generateFilePath(EntityManagerContainer emc, Map<String, OriginFile> filePathMap, String parentPath, Attachment2 att) throws Exception {
		String filename = att.getName();
		String filePath;
		if (parentPath.length() > 0) {
			filePath = parentPath + File.separator + filename;
		} else {
			filePath = filename;
		}
		OriginFile originFile = emc.find(att.getOriginFile(),OriginFile.class);
		if(originFile!=null) {
			filePathMap.put(filePath, originFile);
		}
	}

	public static class AbstractWoFolder extends Folder2 {

		private static final long serialVersionUID = -3416878548938205004L;

		@FieldDescribe("附件数量")
		private Long attachmentCount;
		@FieldDescribe("字节数")
		private Long size;
		@FieldDescribe("目录数量")
		private Long folderCount;

		public Long getAttachmentCount() {
			return attachmentCount;
		}

		public void setAttachmentCount(Long attachmentCount) {
			this.attachmentCount = attachmentCount;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		public Long getFolderCount() {
			return folderCount;
		}

		public void setFolderCount(Long folderCount) {
			this.folderCount = folderCount;
		}

	}

}
