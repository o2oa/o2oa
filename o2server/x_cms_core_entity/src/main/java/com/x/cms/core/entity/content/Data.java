package com.x.cms.core.entity.content;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class Data extends ListOrderedMap<String, Object> {

	public static final String DOCUMENT_PROPERTY = "$document";

	private static final String ATTACHMENTLIST_PROPERTY = "$attachmentList";

	private static final long serialVersionUID = 8339934499479910171L;

	public void setDocument(Document document) {
		if (null != document) {
			DataDocument dataDocument = new DataDocument();
			DataDocument.documentCopier.copy(document, dataDocument);
			dataDocument.setDocId(document.getId());
			this.put(DOCUMENT_PROPERTY, dataDocument);
		}
	}

	public void setAttachmentList(List<FileInfo> attachmentList) {
		List<DataAttachment> list = new ArrayList<>();
		if (ListTools.isNotEmpty(attachmentList)) {
			DataAttachment.copier.copy(attachmentList, list);
		}
		this.put(ATTACHMENTLIST_PROPERTY, list);
	}

	public List<String> extractDistinguishedName(String path)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(path)) {
			Object o = PropertyUtils.getProperty(this, path);
			if (null != o) {
				if (o instanceof CharSequence) {
					list.add(o.toString());
				} else if (o instanceof Iterable) {
					for (Object v : (Iterable<?>) o) {
						if (null != v) {
							if ((v instanceof CharSequence)) {
								list.add(v.toString());
							} else {
								Object d = PropertyUtils.getProperty(v, JpaObject.DISTINGUISHEDNAME);
								String s = Objects.toString(d, "");
								if (StringUtils.isNotEmpty(s)) {
									list.add(s);
								}
							}
						}
					}
				} else {
					Object d = PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME);
					String s = Objects.toString(d, "");
					if (StringUtils.isNotEmpty(s)) {
						list.add(s);
					}
				}
			}
		}
		return list;
	}

	public Object find(String path) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return PropertyUtils.getProperty(this, path);
	}

	@SuppressWarnings("unchecked")
	public <T> T find(String path, Class<T> cls, T defaultValue)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = PropertyUtils.getProperty(this, path);
		if (null == o) {
			return defaultValue;
		}
		return (T) o;
	}

	public static class DataDocument extends GsonPropertyObject {

		private static final long serialVersionUID = -6298001381974881750L;

		private static WrapCopier<Document, DataDocument> documentCopier = WrapCopierFactory.wo(Document.class,
				DataDocument.class, null, JpaObject.FieldsInvisible);

		private String id;
		private String docId;
		private String title;
		private String documentType;
		private String appId;
		private String appName;
		private String categoryId;
		private String categoryName;
		private String categoryAlias;
		private String creatorPerson;
		private String creatorIdentity;
		private String creatorUnitName;
		private String creatorTopUnitName;
		private String docStatus;
		private String importBatchName;
		private Long viewCount = 0L;
		private Long commentCount = 0L;
		private Long commendCount = 0L;
		private Date createTime;
		private Date publishTime;
		private Date modifyTime;
		private Boolean isTop;

		public String getId() {
			return this.id;
		}

		public void setId(final String id) {
			this.id = id;
		}

		public Boolean getTop() {
			return this.isTop;
		}

		public void setTop(final Boolean top) {
			this.isTop = top;
		}

		public Boolean getIsTop() {
			return isTop;
		}

		public void setIsTop(Boolean isTop) {
			this.isTop = isTop;
		}

		public Date getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(Date modifyTime) {
			this.modifyTime = modifyTime;
		}

		public Long getViewCount() {
			return viewCount;
		}

		public void setViewCount(Long viewCount) {
			this.viewCount = viewCount;
		}

		public String getTitle() {
			return title;
		}

		public String getDocumentType() {
			return documentType;
		}

		public String getAppId() {
			return appId;
		}

		public String getAppName() {
			return appName;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public String getCategoryAlias() {
			return categoryAlias;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public String getCreatorIdentity() {
			return creatorIdentity;
		}

		public String getCreatorUnitName() {
			return creatorUnitName;
		}

		public String getCreatorTopUnitName() {
			return creatorTopUnitName;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public void setCategoryAlias(String categoryAlias) {
			this.categoryAlias = categoryAlias;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public void setCreatorIdentity(String creatorIdentity) {
			this.creatorIdentity = creatorIdentity;
		}

		public void setCreatorUnitName(String creatorUnitName) {
			this.creatorUnitName = creatorUnitName;
		}

		public void setCreatorTopUnitName(String creatorTopUnitName) {
			this.creatorTopUnitName = creatorTopUnitName;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public String getImportBatchName() {
			return importBatchName;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}

		public Long getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(Long commentCount) {
			this.commentCount = commentCount;
		}

		public Long getCommendCount() {
			return commendCount;
		}

		public void setCommendCount(Long commendCount) {
			this.commendCount = commendCount;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
	}

	public static class DataAttachment extends GsonPropertyObject {

		private static final long serialVersionUID = -3731780034896236921L;

		static WrapCopier<FileInfo, DataAttachment> copier = WrapCopierFactory.wo(FileInfo.class, DataAttachment.class,
				null, JpaObject.FieldsInvisible);

		private String id;
		private String name;
		private String extension;
		private String storage;
		private Long length;
		private String site;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public String getStorage() {
			return storage;
		}

		public void setStorage(String storage) {
			this.storage = storage;
		}

		public Long getLength() {
			return length;
		}

		public void setLength(Long length) {
			this.length = length;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

	}
}
