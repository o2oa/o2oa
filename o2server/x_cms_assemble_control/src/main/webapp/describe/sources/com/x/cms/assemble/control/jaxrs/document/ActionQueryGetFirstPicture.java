package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ActionQueryGetFirstPicture extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionQueryGetFirstPicture.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> pictureInfoList = null;
		Wo wo = null;
		Boolean check = true;
		
		String cacheKey = getCacheKeyFormWrapInFilter( "firstpicture", id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wo = ( Wo ) element.getObjectValue();
			result.setData( wo );
			result.setCount( 1L );
		} else {
			if( check ){
				if(  StringUtils.isEmpty(id) ){
					check = false;
					Exception exception = new ExceptionDocumentIdEmpty();
					result.error( exception );
				}
			}
			
			if( check ){
				try {
					pictureInfoList = fileInfoServiceAdv.getAllPictureList( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( e, "系统在查询文档所有图片时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( pictureInfoList != null && !pictureInfoList.isEmpty() ){
					FileInfo file = pictureInfoList.get(0);
					
					wo = new Wo();
					wo.setId( file.getId() );
					wo.setCloudId( file.getCloudId() );
					wo.setFileName( file.getFileName() );
					wo.setFilePath( file.getFilePath() );
					wo.setFileType( file.getFileType() );
					
					cache.put( new Element( cacheKey, wo ) );
					
					result.setData( wo );
					result.setCount( 1L );
				}
			}	
		}
		return result;
	}
	
	private String getCacheKeyFormWrapInFilter( String flag, String id ) {
		String cacheKey = ApplicationCache.concreteCacheKey( id, flag );	
		return cacheKey;
	}
	
	public static class Wo{
		
		@FieldDescribe("ID")
		private String id;
		
		@FieldDescribe("云文件ID")
		private String cloudId;
		
		@FieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
		private String fileName;
		
		@FieldDescribe("文件存储路径")
		private String filePath;
		
		@FieldDescribe("文件类别：云文件（CLOUD） | 附件(ATTACHMENT)")
		private String fileType;

		public String getId() {
			return id;
		}

		public String getCloudId() {
			return cloudId;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFilePath() {
			return filePath;
		}

		public String getFileType() {
			return fileType;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setCloudId(String cloudId) {
			this.cloudId = cloudId;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
	}
}