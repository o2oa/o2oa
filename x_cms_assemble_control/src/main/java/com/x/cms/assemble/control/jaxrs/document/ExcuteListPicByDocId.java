package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.DocumentPictureInfo;

import net.sf.ehcache.Element;

public class ExcuteListPicByDocId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListPicByDocId.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutDocumentPictureInfo>> execute( HttpServletRequest request, String docId, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutDocumentPictureInfo>> result = new ActionResult<>();
		List<WrapOutDocumentPictureInfo> wraps = null;
		List<DocumentPictureInfo> pictures = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "document", docId );
		Element element = pic_cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutDocumentPictureInfo> ) element.getObjectValue();
			result.setData( wraps );
		} else {
			if( docId == null || docId.isEmpty() ){
				check = false;
				Exception exception = new DocumentIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			if( check ){
				try {
					pictures = documentServiceAdv.listMainPictureByDocId( docId );
				} catch (Exception e) {
					check = false;
					Exception exception = new ServiceLogicException( e, "系统在根据文档ID查询所有的大图信息列表时发生异常！" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				if( pictures != null && !pictures.isEmpty() ){
					try {
						wraps = WrapTools.picture_wrapout_copier.copy( pictures );
						pic_cache.put(new Element( cacheKey, wraps ));
						result.setData( wraps );
					} catch (Exception e) {
						check = false;
						Exception exception = new ServiceLogicException( e, "系统在将查询结果转换为可输出的数据信息时发生异常！" );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
		}
		
		return result;
	}

}