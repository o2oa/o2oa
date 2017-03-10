package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.log.WrapOutLog;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutDocumentComplex> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutDocumentComplex> result = new ActionResult<>();
		WrapOutDocumentComplex wrap = new WrapOutDocumentComplex();
		WrapOutDocument wrapOutDocument  = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		List<FileInfo> attachmentList = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new DocumentIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutDocumentComplex ) element.getObjectValue();
			result.setData(wrap);
		} else {
			if( check ){
				try {
					document = documentServiceAdv.get( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentViewByIdException( e, id, effectivePerson.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				if( document != null ){
					try {
						wrapOutDocument = WrapTools.document_wrapout_copier.copy( document );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentWrapOutException( e );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new DocumentNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				if( wrapOutDocument != null ){
					try {
						categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
						wrapOutDocument.setCategoryName( categoryInfo.getCategoryName());
						wrapOutDocument.setCategoryAlias( categoryInfo.getCategoryAlias());
						wrap.setDocument( wrapOutDocument );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoQueryByIdException( e, document.getCategoryId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				if( wrapOutDocument != null ){
					try {
						wrap.setData( documentServiceAdv.getDocumentData( document ) );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentDataContentGetException( e, document.getCategoryId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				try {
					attachmentList = documentServiceAdv.getAttachmentList( document );
					if( attachmentList != null && !attachmentList.isEmpty() ){
						wrap.setAttachmentList( WrapTools.fileForDoc_wrapout_copier.copy( attachmentList ) );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentAttachmentListException( e, document.getCategoryId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				wrap.setDocumentLogList( new ArrayList< WrapOutLog >() );
			}
			cache.put( new Element( cacheKey, wrap ) );
			result.setData( wrap );
		}
		
		return result;
	}

}