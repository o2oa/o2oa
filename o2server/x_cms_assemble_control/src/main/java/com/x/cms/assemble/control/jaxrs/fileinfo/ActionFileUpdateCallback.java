package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class ActionFileUpdateCallback extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionFileUpdateCallback.class);

	protected ActionResult<Wo<WoObject>> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String docId, String old_attId, String callback, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo<WoObject>> result = new ActionResult<>();
		FileInfo attachment = null;
		Document document = null;
		StorageMapping mapping = null;
		String fileName = null;
		Boolean check = true;		
		
		if( check ){
			if( StringUtils.isEmpty(docId) ){
				check = false;
				Exception exception = new URLParameterGetExceptionCallback( callback, new Exception("未获取到文档ID") );
				result.error( exception );
			}
		}
		if( check ){
			if( StringUtils.isEmpty(old_attId) ){
				check = false;
				Exception exception = new URLParameterGetExceptionCallback( callback, new Exception("未获取到需要替换的附件ID") );
				result.error( exception );
			}
		}
		
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Boolean isManager = false;
		if (check) {
			try {
				if ( effectivePerson.isManager() ) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "判断用户是否是系统管理员时发生异常！user:" + effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){//判断文档信息是否已经存在
			try {
				document = documentQueryService.get( docId );
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExistsCallback( callback, docId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
				/** 禁止不带扩展名的文件上传 */
				if (StringUtils.isEmpty(fileName)) {
					check = false;
					Exception exception = new ExceptionEmptyExtensionCallback( callback, fileName );
					result.error( exception );
				} 
			} catch (Exception e) {
				check = false;
				result.error( e );
			}
		}
		
		if( check ){
			try {
				mapping = ThisApplication.context().storageMappings().random( FileInfo.class );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在获取存储的时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				attachment = this.concreteAttachment( mapping, document, fileName, effectivePerson, site );
				
				attachment.setType((new Tika()).detect(bytes, fileName));
				logger.debug("filename:{}, file type:{}.", attachment.getName(), attachment.getType());
				if (Config.query().getExtractImage() && ExtractTextTools.supportImage(attachment.getName()) && ExtractTextTools.available(bytes)) {
					attachment.setText(ExtractTextTools.image(bytes));
					logger.debug("filename:{}, file type:{}, text:{}.", attachment.getName(), attachment.getType(),
							attachment.getText());
				}
				
				attachment.saveContent(mapping, bytes, fileName);
				attachment = fileInfoServiceAdv.updateAttachment( docId, old_attId, attachment, mapping );
//				
//				List<String> keys = new ArrayList<>();
//				keys.add( "file.all" ); //清除文档的附件列表缓存
//				keys.add( "file." + old_attId  ); //清除指定ID的附件信息缓存
//				keys.add( ApplicationCache.concreteCacheKey( "document", document.getId(), isAnonymous, isManager ) ); //清除文档的附件列表缓存
//				ApplicationCache.notify( FileInfo.class, keys );
//				
//				keys.clear();
//				keys.add(  ApplicationCache.concreteCacheKey( document.getId(), "view", isAnonymous, isManager ) ); //清除文档阅读缓存
//				keys.add( ApplicationCache.concreteCacheKey( document.getId(), "get", isManager )  ); //清除文档信息获取缓存
//				ApplicationCache.notify( Document.class, keys );			
//				
				ApplicationCache.notify( FileInfo.class );
				ApplicationCache.notify( Document.class );		
				
				WoObject woObject = new WoObject();
				woObject.setId(attachment.getId());
				Wo<WoObject> wo = new Wo<>(callback, woObject);
				result.setData(wo);
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在保存文件和更新数据时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		return result;
	}

	private FileInfo concreteAttachment(StorageMapping mapping, Document document, String name, EffectivePerson effectivePerson, String site) throws Exception {
		FileInfo attachment = new FileInfo();
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
	
	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
	}
}
