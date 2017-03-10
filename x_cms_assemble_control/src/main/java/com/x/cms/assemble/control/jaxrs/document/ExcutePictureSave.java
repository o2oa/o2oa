package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPictureInfo;

public class ExcutePictureSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcutePictureSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, WrapInDocumentPictureInfo wrapIn, EffectivePerson effectivePerson) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Document document = null;
		DocumentPictureInfo picture = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn.getDocumentId() == null || wrapIn.getDocumentId().isEmpty() ){
				check = false;
				Exception exception = new DocumentIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getBase64() == null || wrapIn.getBase64().isEmpty() ){
				check = false;
				Exception exception = new DocumentPicBase64EmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				document = documentServiceAdv.get( wrapIn.getDocumentId() );
				if( document == null ){
					check = false;
					Exception exception = new DocumentNotExistsException( wrapIn.getDocumentId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentViewByIdException( e, wrapIn.getDocumentId(), effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				picture = documentServiceAdv.saveMainPicture( wrapIn );
				
				ApplicationCache.notify( DocumentPictureInfo.class );
				
				result.setData( new WrapOutId( picture.getId() ) );				
			}catch(Exception e){
				check = false;
				Exception exception = new ServiceLogicException( e, "系统在创建文档图片信息时发生异常！" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		return result;
	}
	
}