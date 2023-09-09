package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.Date;
import java.util.UUID;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.tools.FileTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;

public class ActionFileUploadWithUrl extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFileUploadWithUrl.class);

	@AuditLog(operation = "上传附件")
	protected ActionResult<Wo> execute(EffectivePerson effectivePerson,
										JsonElement jsonElement) throws Exception {
		logger.debug("ActionFileUploadWithUrl receive:{}.", jsonElement.toString());
		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isEmpty(wi.getDocId())){
			throw new ExceptionEntityFieldEmpty(Document.class, wi.getDocId());
		}
		if(StringUtils.isEmpty(wi.getFileName())){
			throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileName());
		}
		if(StringUtils.isEmpty(wi.getFileUrl())){
			throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileUrl());
		}
		if(StringUtils.isEmpty(wi.getSite())){
			throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getSite());
		}

		Document document = documentQueryService.get( wi.getDocId() );
		if (null == document) {
			throw new ExceptionDocumentNotExists(wi.getDocId());
		}

		AppInfo appInfo = appInfoServiceAdv.get(document.getAppId());
		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());

		String person = effectivePerson.getDistinguishedName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, document)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if(StringUtils.isNotEmpty(wi.getPerson()) && business.isManager(effectivePerson)){
				Person p = business.organization().person().getObject(wi.getPerson());
				if(p!=null){
					person = p.getDistinguishedName();
				}
			}
		}

		StorageMapping mapping = ThisApplication.context().storageMappings().random( FileInfo.class );
		FileInfo attachment = this.concreteAttachment( mapping, document, wi.getFileName(), person, wi.getSite() );
		byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
		if(bytes==null || bytes.length==0){
			throw new Exception("can not down file from url!");
		}

		FileTools.verifyConstraint(bytes.length, wi.getFileName(), null);

		attachment.setType((new Tika()).detect(bytes, wi.getFileName()));
		logger.debug("filename:{}, file type:{}.", attachment.getName(), attachment.getType());
		if (Config.query().getExtractImage() && ExtractTextTools.supportImage(attachment.getName()) && ExtractTextTools.available(bytes)) {
			attachment.setText(ExtractTextTools.image(bytes));
			logger.debug("filename:{}, file type:{}, text:{}.", attachment.getName(), attachment.getType(),
					attachment.getText());
		}

		attachment.saveContent(mapping, bytes, wi.getFileName(), Config.general().getStorageEncrypt());
		attachment = fileInfoServiceAdv.saveAttachment( wi.getDocId(), attachment );

		CacheManager.notify( FileInfo.class );
		CacheManager.notify( Document.class );

		Wo wo = new Wo();
		wo.setId( attachment.getId() );
		result.setData(wo);

		return result;
	}

	private FileInfo concreteAttachment(StorageMapping mapping, Document document, String name, String person, String site) throws Exception {
		FileInfo attachment = new FileInfo();
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		if ( StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
		}else{
			throw new ExceptionEmptyExtension(name);
		}
		if( name.indexOf( "\\" ) >0 ){
			name = StringUtils.substringAfterLast( name, "\\");
		}
		if( name.indexOf( "/" ) >0 ){
			name = StringUtils.substringAfterLast( name, "/");
		}
		attachment.setCreateTime( new Date() );
		attachment.setLastUpdateTime( new Date() );
		attachment.setExtension( extension );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorage( mapping.getName() );
		attachment.setAppId( document.getAppId() );
		attachment.setCategoryId( document.getCategoryId() );
		attachment.setDocumentId( document.getId() );
		attachment.setCreatorUid(person);
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFileType("ATTACHMENT");
		attachment.setFileExtType( getExtType( extension ) );
		attachment.setFilePath( "" );
		return attachment;
	}

	private String getExtType( String ext ){
		String type = "OTHER";
		if( "jpg".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("jpeg".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("png".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("tif".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("bmp".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("gif".equalsIgnoreCase( ext ) ){ type = "PICTURE";
		} else if("xls".equalsIgnoreCase( ext ) ){ type = "EXCLE";
		} else if("xlsx".equalsIgnoreCase( ext ) ){ type = "EXCLE";
		} else if("doc".equalsIgnoreCase( ext ) ){ type = "WORD";
		} else if("docx".equalsIgnoreCase( ext ) ){ type = "WORD";
		} else if("ppt".equalsIgnoreCase( ext ) ){ type = "PPT";
		} else if("pptx".equalsIgnoreCase( ext ) ){ type = "PPT";
		} else if("zip".equalsIgnoreCase( ext ) ){ type = "ZIP";
		} else if("rar".equalsIgnoreCase( ext ) ){ type = "ZIP";
		} else if("txt".equalsIgnoreCase( ext ) ){ type = "TXT";
		} else if("pdf".equalsIgnoreCase( ext ) ){ type = "PDF";
		}
		return type;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 5113558862906097036L;

		@FieldDescribe("*内容管理文档id.")
		private String docId;

		@FieldDescribe("*文件名称,带扩展名的文件名.")
		private String fileName;

		@FieldDescribe("*附件来源url地址.")
		private String fileUrl;

		@FieldDescribe("*附件分类.")
		private String site;

		@FieldDescribe("上传人员（仅对管理员生效）.")
		private String person;

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}
	}

	public static class Wo extends WoId {

	}
}
