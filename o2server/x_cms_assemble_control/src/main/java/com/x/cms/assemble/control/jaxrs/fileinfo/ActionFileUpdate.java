package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.base.core.project.tools.FileTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

public class ActionFileUpdate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFileUpdate.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,
			String docId, String old_attId, String site, String fileName, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(fileName)) {
			fileName = this.fileName(disposition);
		}
		if( StringUtils.isEmpty(old_attId) ){
			throw new URLParameterGetException( new Exception("未获取到需要替换的附件ID") );
		}
		if( StringUtils.isEmpty(docId) ){
			throw new URLParameterGetException( new Exception("未获取到汇报ID") );
		}

		Document document = documentQueryService.get( docId );
		if (null == document) {
			throw  new ExceptionDocumentNotExists( docId );
		}

		CategoryInfo categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
		if (categoryInfo == null) {
			throw new ExceptionCategoryInfoNotExists(document.getCategoryId());
		}

		AppInfo appInfo = appInfoServiceAdv.get( categoryInfo.getAppId() );
		if (appInfo == null) {
			throw  new ExceptionAppInfoNotExists(categoryInfo.getAppId());
		}

		if ( !documentQueryService.getFileInfoManagerAssess( effectivePerson, document, categoryInfo, appInfo ) ) {
			throw  new ExceptionDocumentAccessDenied(effectivePerson.getDistinguishedName(), document.getTitle(), document.getId());
		}

		FileInfo attachment = fileInfoServiceAdv.get( old_attId );
		if (null == attachment) {
			throw new ExceptionFileInfoNotExists( old_attId );
		}

		FileTools.verifyConstraint(bytes.length, fileName, null);

		StorageMapping mapping = ThisApplication.context().storageMappings().get( FileInfo.class, attachment.getStorage());

		attachment = this.concreteAttachment( mapping, attachment, document, fileName, effectivePerson, site );

		attachment.setType((new Tika()).detect(bytes, fileName));
		logger.debug("filename:{}, file type:{}.", attachment.getName(), attachment.getType());
		if (Config.query().getExtractImage() && ExtractTextTools.supportImage(attachment.getName()) && ExtractTextTools.available(bytes)) {
			attachment.setText(ExtractTextTools.image(bytes));
			logger.debug("filename:{}, file type:{}, text:{}.", attachment.getName(), attachment.getType(),
					attachment.getText());
		}

		//文件存储
		attachment.updateContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
		//完成替换逻辑
		attachment = fileInfoServiceAdv.updateAttachment( docId, old_attId, attachment, mapping );

		CacheManager.notify( FileInfo.class );
		CacheManager.notify( Document.class );

		Wo wo = new Wo();
		wo.setId( attachment.getId() );
		result.setData(wo);
		return result;
	}

	private FileInfo concreteAttachment( StorageMapping mapping, FileInfo attachment, Document document, String name, EffectivePerson effectivePerson, String site) throws Exception {
		if ( attachment == null ) {
			attachment = new FileInfo();
		}

		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		if ( StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
		}else{
			throw new Exception("file extension is empty.");
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
		attachment.setCreatorUid( effectivePerson.getDistinguishedName() );
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

	public static class Wo extends WoId {

	}
}
