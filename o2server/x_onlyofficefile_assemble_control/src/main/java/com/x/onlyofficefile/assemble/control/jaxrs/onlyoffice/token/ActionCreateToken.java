package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.token;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;

public class ActionCreateToken extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(ActionCreateToken.class);
	
	ActionResult<Wo> execute(HttpServletRequest request,EffectivePerson effectivePerson,String flag,JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			result.setData(wo);
			
			String token = "";
			if (DocumentManager.tokenEnabled()){
				Map<String, Object> map = new HashMap<>();
				map.put("type", wi.getType());
				map.put("documentType", wi.getDocumentType());
				map.put("document", wi.getDocument());
				map.put("editorConfig", wi.getEditorConfig());
				token = DocumentManager.createToken(map);
			}
			
			wo.setValue(token);
			return result;
		}

	}
	
	
	public static class Wo extends WrapString {
		
	}
	
	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("使用终端类型")
		public String type = "desktop";
		@FieldDescribe("文件类型")
		public String documentType;
		@FieldDescribe("文档属性")
		public Document document;
		@FieldDescribe("文档编辑属性")
		public EditorConfig editorConfig;
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public Document getDocument() {
			return document;
		}

		public void setDocument(Document document) {
			this.document = document;
		}

		public EditorConfig getEditorConfig() {
			return editorConfig;
		}

		public void setEditorConfig(EditorConfig editorConfig) {
			this.editorConfig = editorConfig;
		}

	
		public class Document {
			public String title;
			public String url;
			public String fileType;
			public String key;
			public Permissions permissions;
		}

		public class Permissions {
			public Boolean comment;
			public Boolean download;
			public Boolean edit;
			public Boolean fillForms;
			public Boolean modifyFilter;
			public Boolean modifyContentControl;
			public Boolean review;
		}

		public class EditorConfig {
			public String mode = "edit";
			public String callbackUrl;
			public String lang = "en";
			public User user;
			public Customization customization;
			public Embedded embedded;

			public class User {
				public String id = "uid-1";
				public String name = "John Smith";
			}

			public class Customization {
				public Goback goback;

				public Customization() {
					goback = new Goback();
				}

				public class Goback {
					public String url;
				}
			}

			public class Embedded {
				public String saveUrl;
				public String embedUrl;
				public String shareUrl;
				public String toolbarDocked;
				public String getSaveUrl() {
					return saveUrl;
				}
				public void setSaveUrl(String saveUrl) {
					this.saveUrl = saveUrl;
				}
				public String getEmbedUrl() {
					return embedUrl;
				}
				public void setEmbedUrl(String embedUrl) {
					this.embedUrl = embedUrl;
				}
				public String getShareUrl() {
					return shareUrl;
				}
				public void setShareUrl(String shareUrl) {
					this.shareUrl = shareUrl;
				}
				public String getToolbarDocked() {
					return toolbarDocked;
				}
				public void setToolbarDocked(String toolbarDocked) {
					this.toolbarDocked = toolbarDocked;
				}
				
			}
		}
	}
	
}
