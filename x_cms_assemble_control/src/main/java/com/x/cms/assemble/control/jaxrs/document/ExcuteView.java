package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.document.exception.CategoryInfoProcessException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentIdEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentNotExistsException;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ExcuteView extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteView.class );
	
	protected ActionResult<WrapOutDocumentComplex> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutDocumentComplex> result = new ActionResult<>();
		WrapOutDocumentComplex wrap = new WrapOutDocumentComplex();
		WrapOutDocument wrapOutDocument  = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		List<String> ids = null;
		List<FileInfo> attachmentList = null;
		Boolean isAppAdmin = false;
		Boolean isCategoryAdmin = false;
		Boolean isManager = false;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new DocumentIdEmptyException();
			result.error( exception );
		}
		String cacheKey = ApplicationCache.concreteCacheKey( id, "view", isManager, isAppAdmin, isCategoryAdmin );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutDocumentComplex ) element.getObjectValue();
			result.setData(wrap);
		} else {
			if( check ){
				try {
					document = documentServiceAdv.view( id, effectivePerson );
					if( document == null ){
						check = false;
						Exception exception = new DocumentNotExistsException( id );
						result.error( exception );
					}else{
						try {
							wrapOutDocument = WrapTools.document_wrapout_copier.copy( document );
						} catch (Exception e) {
							check = false;
							Exception exception = new DocumentInfoProcessException( e, "将查询出来的文档信息对象转换为可输出的数据信息时发生异常。" );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "文档信息访问操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					if( effectivePerson.isManager() ){
						isManager = true;
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "判断用户是否是系统管理员时发生异常！user:" + effectivePerson.getName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					ids = appCategoryAdminServiceAdv.listAppCategoryIdByCondition( "CATEGORY", wrapOutDocument.getCategoryId(), effectivePerson.getName() );
					if( ids != null && !ids.isEmpty() ){
						isCategoryAdmin = true;
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "判断用户是否是系统管理员时发生异常！user:" + effectivePerson.getName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				try {
					ids = appCategoryAdminServiceAdv.listAppCategoryIdByCondition( "APPINFO", wrapOutDocument.getAppId(), effectivePerson.getName() );
					if( ids != null && !ids.isEmpty() ){
						isAppAdmin = true;
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "判断用户是否是系统管理员时发生异常！user:" + effectivePerson.getName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			wrap.setIsManager(isManager);
			wrap.setIsAppAdmin(isAppAdmin);
			wrap.setIsCategoryAdmin(isCategoryAdmin);
			
			if( check ){
				if( wrapOutDocument != null ){
					try {
						categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
						wrapOutDocument.setForm( categoryInfo.getFormId() );
						wrapOutDocument.setFormName( categoryInfo.getFormName() );
						wrapOutDocument.setReadFormId( categoryInfo.getReadFormId() );
						wrapOutDocument.setReadFormName( categoryInfo.getReadFormName() );
						wrapOutDocument.setCategoryName( categoryInfo.getCategoryName());
						wrapOutDocument.setCategoryAlias( categoryInfo.getCategoryAlias());
						wrap.setDocument( wrapOutDocument );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoProcessException( e, "根据ID查询分类信息对象时发生异常。ID:"+document.getCategoryId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				if( wrapOutDocument != null ){
					try {
						wrap.setData( documentServiceAdv.getDocumentData( document ) );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentInfoProcessException( e, "系统获取文档数据内容信息时发生异常。Id:" + document.getCategoryId() );
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
					Exception exception = new DocumentInfoProcessException( e, "系统获取文档附件内容列表时发生异常。Id:" + document.getCategoryId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			cache.put(new Element( cacheKey, wrap ));
			result.setData( wrap );
		}
		if( check ){
			//记录该文档的访问记录
			try {
				documentViewRecordServiceAdv.addViewRecord( id, effectivePerson.getName() );
			} catch (Exception e) {
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}

}