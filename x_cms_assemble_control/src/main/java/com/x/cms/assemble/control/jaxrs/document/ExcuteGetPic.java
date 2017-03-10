package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.DocumentPictureInfo;

import net.sf.ehcache.Element;

public class ExcuteGetPic extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGetPic.class );
	
	protected ActionResult<WrapOutDocumentPictureInfo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutDocumentPictureInfo> result = new ActionResult<>();
		WrapOutDocumentPictureInfo wrap = new WrapOutDocumentPictureInfo();
		DocumentPictureInfo documentPictureInfo  = null;
		Boolean check = true;
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = pic_cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = (WrapOutDocumentPictureInfo) element.getObjectValue();
			result.setData( wrap );
		} else {
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new DocumentPicIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			if( check ){
				try {
					documentPictureInfo = documentServiceAdv.getDocumentPictureById( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentViewByIdException( e, id, effectivePerson.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				if( documentPictureInfo != null ){
					try {
						wrap = WrapTools.picture_wrapout_copier.copy( documentPictureInfo );
						pic_cache.put(new Element( cacheKey, wrap ));
						result.setData( wrap );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentWrapOutException( e );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new DocumentPictureNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		return result;
	}

}