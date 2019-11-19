package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Element;

public class ActionQueryViewDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryViewDocument.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean isManager = false;
		Boolean check = true;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		Long viewCount = 0L;

		if ( StringUtils.isEmpty(id)) {
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error(exception);
		}		
		
		if (check) {
			try {
				if ( effectivePerson.isManager() ) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否是系统管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( id, "view", isAnonymous, isManager, effectivePerson.getDistinguishedName() );
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			result = (ActionResult<Wo>) element.getObjectValue();
		} else {
			logger.debug(">>>>>>>>>>>>>view document '"+id+"' in database!" );
			//继续进行数据查询
			result = getDocumentQueryResult( id, request, effectivePerson, isManager );
			cache.put(new Element(cacheKey, result ));
		}
		
		if (check ) {
			//只要不是管理员访问，则记录该文档的访问记录
			if ( !"xadmin".equalsIgnoreCase( personName) ) {
				try {
					viewCount = documentViewRecordServiceAdv.addViewRecord( id, personName );
					result.getData().document.setViewCount( viewCount );
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			//异步更新item里的访问量，便于视图统计
			try {
				ThisApplication.queueDocumentViewCountUpdate.send( result.getData().getDocument() );
			} catch ( Exception e1 ) {
				e1.printStackTrace();
			}
			
		}
		return result;			
	}

	/**
	 * 获取需要返回的文档信息对象
	 * @param id
	 * @param request
	 * @param effectivePerson
	 * @param isManager 当前用户是否是系统管理或者CMS管理员
	 * @return
	 */
	private ActionResult<Wo> getDocumentQueryResult( String id, HttpServletRequest request, EffectivePerson effectivePerson, Boolean isManager ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WoDocument woOutDocument = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Boolean isAppAdmin = false;
		Boolean isCategoryAdmin = false;
		Boolean isEditor = false;
		Boolean isCreator = false;
		Boolean check = true;
		List<String> unitNames = null;
		List<String> groupNames = null;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		
		if( !isAnonymous ) {
			try {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );									
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "查询用户所有的组织和群组信息时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				document = documentQueryService.view( id, effectivePerson );
				if ( document == null ) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "文档信息访问操作时发生异常。Id:" + id + ", Name:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null );
			}
		}
		
		if (check) {
			try {
				appInfo = appInfoServiceAdv.get( document.getAppId() );
				if( appInfo == null ) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( document.getAppId()  );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "根据ID查询栏目信息对象时发生异常。ID:" + document.getAppId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
				if( categoryInfo == null ) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( document.getCategoryId() );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "根据ID查询分类信息对象时发生异常。ID:" + document.getCategoryId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( isAnonymous ) {
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( ( ListTools.isNotEmpty( document.getReadPersonList() ) && !document.getReadPersonList().contains( "所有人" ) )
						|| ListTools.isNotEmpty( document.getReadUnitList() ) || ListTools.isNotEmpty( document.getReadGroupList() ) ) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( "该文档不允许匿名访问。ID:" + id );
					result.error(exception);
				}
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( !appInfo.getAllPeopleView() ) {
					//栏目不可见
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( "栏目["+appInfo.getAppName()+"]不允许匿名访问。ID:" + document.getAppId());
					result.error(exception);
				}
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( !categoryInfo.getAllPeopleView() ) {
					//分类不可见
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( "分类["+categoryInfo.getCategoryName()+"]不允许匿名访问。ID:" + document.getCategoryId());
					result.error(exception);
				}
			}
		}
		
		
		if (check) {
			try {
				woOutDocument = WoDocument.copier.copy( document );
				
				if ( woOutDocument != null && categoryInfo != null ) {
					try {
						woOutDocument.setForm(categoryInfo.getFormId());
						woOutDocument.setFormName(categoryInfo.getFormName());
						woOutDocument.setReadFormId(categoryInfo.getReadFormId());
						woOutDocument.setReadFormName(categoryInfo.getReadFormName());
						woOutDocument.setCategoryName(categoryInfo.getCategoryName());
						woOutDocument.setCategoryAlias(categoryInfo.getCategoryAlias());
						
						if( woOutDocument.getCreatorPerson() != null && !woOutDocument.getCreatorPerson().isEmpty() ) {
							woOutDocument.setCreatorPersonShort( woOutDocument.getCreatorPerson().split( "@" )[0]);
						}
						if( woOutDocument.getCreatorUnitName() != null && !woOutDocument.getCreatorUnitName().isEmpty() ) {
							woOutDocument.setCreatorUnitNameShort( woOutDocument.getCreatorUnitName().split( "@" )[0]);
						}
						if( woOutDocument.getCreatorTopUnitName() != null && !woOutDocument.getCreatorTopUnitName().isEmpty() ) {
							woOutDocument.setCreatorTopUnitNameShort( woOutDocument.getCreatorTopUnitName().split( "@" )[0]);
						}
						wo.setDocument(woOutDocument);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e, "根据ID查询分类信息对象时发生异常。ID:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				
				if ( woOutDocument != null ) {
					try {						
						wo.setData( documentQueryService.getDocumentData( document ) );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e, "系统获取文档数据内容信息时发生异常。Id:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "将查询出来的文档信息对象转换为可输出的数据信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		//判断用户是否是文档的创建者，创建者是有权限编辑文档的
		if (check) {
			if( wo.getDocument() != null && wo.getDocument().getCreatorPerson() != null && wo.getDocument().getCreatorPerson().equals( personName )) {
					isCreator = true;
					wo.setIsCreator( isCreator );
			}
		}
	
		//判断用户是否是分类的管理者，分类管理者是有权限编辑文档的
		if (check) {
			try {
				if ( categoryInfoServiceAdv.isCategoryInfoManager( categoryInfo, personName, unitNames, groupNames )) {
					isCategoryAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否是分类管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		//判断用户是否是栏目的管理者，栏目管理者是有权限编辑文档的
		if (check) {
			try {
				if (appInfoServiceAdv.isAppInfoManager( appInfo, personName, unitNames, groupNames )) {
					isAppAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否是栏目管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( isManager || isAppAdmin || isCategoryAdmin || isCreator ) {
				isEditor = true;
			} else {
				// 判断当前登录者是不是该文档的可编辑者
				try {
					if( !isAnonymous ) {
						if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
							if( document.getAuthorPersonList().contains( personName ) ) {
								isEditor = true;
							}
						}
						if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
							if( ListTools.containsAny( unitNames, document.getAuthorUnitList())) {
								isEditor = true;
							}
						}
						if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
							if( ListTools.containsAny( groupNames, document.getAuthorGroupList())) {
								isEditor = true;
							}
						}
					}					
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否可编辑文档时发生异常！user:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		wo.setIsManager( isManager );
		wo.setIsAppAdmin( isAppAdmin );
		wo.setIsCategoryAdmin( isCategoryAdmin );
		wo.setIsEditor( isEditor );
		
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "作为输出的CMS文档数据对象." )
		private WoDocument document;

//		@FieldDescribe( "作为输出的CMS文档附件文件信息数据对象." )
//		private List<WoFileInfo> attachmentList;

		@FieldDescribe( "作为输出的CMS文档操作日志." )
		private List<WoLog> documentLogList;

		@FieldDescribe( "文档所有数据信息." )
		private Data data;
		
		@FieldDescribe( "作为编辑的CMS文档表单." )
		private WoForm form;
		
		@FieldDescribe( "作为查看的CMS文档表单." )
		private WoForm readForm;
		
		private Boolean isAppAdmin = false;
		private Boolean isCategoryAdmin = false;
		private Boolean isManager = false;
		private Boolean isCreator = false;
		private Boolean isEditor = false;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
			this.document = document;
		}

//		public List<WoFileInfo> getAttachmentList() {
//			return attachmentList;
//		}
//
//		public void setAttachmentList(List<WoFileInfo> attachmentList) {
//			this.attachmentList = attachmentList;
//		}

		public List<WoLog> getDocumentLogList() {
			return documentLogList;
		}

		public void setDocumentLogList(List<WoLog> documentLogList) {
			this.documentLogList = documentLogList;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
		}

		public WoForm getReadForm() {
			return readForm;
		}

		public void setReadForm(WoForm readForm) {
			this.readForm = readForm;
		}

		public Boolean getIsAppAdmin() {
			return isAppAdmin;
		}

		public Boolean getIsCategoryAdmin() {
			return isCategoryAdmin;
		}

		public Boolean getIsManager() {
			return isManager;
		}

		public void setIsAppAdmin(Boolean isAppAdmin) {
			this.isAppAdmin = isAppAdmin;
		}

		public void setIsCategoryAdmin(Boolean isCategoryAdmin) {
			this.isCategoryAdmin = isCategoryAdmin;
		}

		public void setIsManager(Boolean isManager) {
			this.isManager = isManager;
		}

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}
		
	}
	
	public static class WoDocument extends Document {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null,JpaObject.FieldsInvisible);
		
		/**
		 * 只作显示用
		 */
		private String creatorPersonShort = "";
		
		private String creatorUnitNameShort = "";
		
		private String creatorTopUnitNameShort = "";

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}
	}
//	
//	public static class WoFileInfo extends FileInfo {
//		
//		private static final long serialVersionUID = -5076990764713538973L;
//		
//		public static List<String> Excludes = new ArrayList<String>();
//		
//		private WoControl control = new WoControl();
//
//		public WoControl getControl() {
//			return control;
//		}
//
//		public void setControl(WoControl control) {
//			this.control = control;
//		}
//		
//		public static WrapCopier<FileInfo, WoFileInfo> copier = WrapCopierFactory.wo( FileInfo.class, WoFileInfo.class, null, JpaObject.FieldsInvisible);
//		
//		private Long referencedCount;
//		
//		public Long getReferencedCount() {
//			return referencedCount;
//		}
//		
//		public void setReferencedCount(Long referencedCount) {
//			this.referencedCount = referencedCount;
//		}
//	}
	
	public static class WoLog extends Log {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
	}
	
	public static class WoForm extends Form {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
	}
	
//	public static class WoControl extends GsonPropertyObject {
//
//		private Boolean allowRead = false;
//		private Boolean allowEdit = false;
//		private Boolean allowControl = false;
//
//		public Boolean getAllowRead() {
//			return allowRead;
//		}
//
//		public void setAllowRead(Boolean allowRead) {
//			this.allowRead = allowRead;
//		}
//
//		public Boolean getAllowEdit() {
//			return allowEdit;
//		}
//
//		public void setAllowEdit(Boolean allowEdit) {
//			this.allowEdit = allowEdit;
//		}
//
//		public Boolean getAllowControl() {
//			return allowControl;
//		}
//
//		public void setAllowControl(Boolean allowControl) {
//			this.allowControl = allowControl;
//		}
//	}
//	
//	private boolean read( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getReadIdentityList()) && ListTools.isEmpty(woFileInfo.getReadUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getReadIdentityList()) || ListTools.containsAny(units, woFileInfo.getReadUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
//
//	private boolean edit( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
//			throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getEditIdentityList()) && ListTools.isEmpty(woFileInfo.getEditUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList())
//					|| ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
//
//	private boolean control( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
//			throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getControllerUnitList()) && ListTools.isEmpty(woFileInfo.getControllerIdentityList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList())
//					|| ListTools.containsAny(units, woFileInfo.getControllerUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
}