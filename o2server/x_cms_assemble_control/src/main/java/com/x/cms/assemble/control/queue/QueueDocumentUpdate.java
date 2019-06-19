package com.x.cms.assemble.control.queue;

import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;

/**
 * Document变更标题时也需要更新一下热点图片里的数据
 *
 */
public class QueueDocumentUpdate extends AbstractQueue<Document> {
	
	public void execute( Document document ) throws Exception {
		WrapInHotPictureInfo hotPictureInfo = new WrapInHotPictureInfo();
		hotPictureInfo.setApplication( "CMS" );
		hotPictureInfo.setTitle( document.getTitle() );
		hotPictureInfo.setInfoId( document.getId() );
		try {
			ThisApplication.context().applications().postQuery(
					x_hotpic_assemble_control.class, "changeTitle", hotPictureInfo
			);
		}catch( Exception e ) {
		}
	}
	
	
	public static class WrapInHotPictureInfo{
		
		@FieldDescribe("应用名称")
		private String application = "";

		@FieldDescribe("信息对象ID")
		private String infoId = "";

		@FieldDescribe("信息标题")
		private String title = "";

		@FieldDescribe("信息访问URL")
		private String url = "";

		@FieldDescribe("信息图片URL")
		private String picId = "";

		@FieldDescribe("创建者")
		private String creator = "";

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getInfoId() {
			return infoId;
		}

		public void setInfoId(String infoId) {
			this.infoId = infoId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public String getPicId() {
			return picId;
		}

		public void setPicId(String picId) {
			this.picId = picId;
		}

	}
}
