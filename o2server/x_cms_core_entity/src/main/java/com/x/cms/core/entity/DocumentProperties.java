package com.x.cms.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.query.DocumentNotify;

import java.util.List;

/**
 * 文档扩展配置
 * @author sword
 */
public class DocumentProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("消息通知对象")
	private DocumentNotify documentNotify;

	@FieldDescribe("文档内容图片列表")
	private List<String> cloudPictures;

	public DocumentNotify getDocumentNotify() {
		return documentNotify;
	}

	public void setDocumentNotify(DocumentNotify documentNotify) {
		this.documentNotify = documentNotify;
	}

	public List<String> getCloudPictures() {
		return cloudPictures;
	}

	public void setCloudPictures(List<String> cloudPictures) {
		this.cloudPictures = cloudPictures;
	}
}
