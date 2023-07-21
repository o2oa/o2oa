package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ActionQueryGetControl extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryGetControl.class);

	/**
	 * "control": {
      "allowVisit": true,
      "allowReadProcessing": false,
      "allowDelete": true
    },
	 * @param request
	 * @param id
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WoControl woControl = new WoControl();
		Long reviewCount = null;
		Boolean isAppAdmin = false;
		Boolean isCategoryAdmin = false;
		Boolean isManager = effectivePerson.isManager();
		Boolean isCreator = false;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		Document document = documentQueryService.get(id);
		if(document == null){
			throw new ExceptionDocumentNotExists(id);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), effectivePerson.getDistinguishedName(), id, isManager );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			woControl = wo.getControl();
		} else {
			reviewCount = documentQueryService.getViewableReview(id, personName);
			if (reviewCount > 0 ) {
				woControl.setAllowVisit(true);
			}
			if (StringUtils.equals( personName, document.getCreatorPerson())) {
				isCreator = true;
				woControl.setAllowVisit(true);
			}
			wo.setControl(woControl);
			CacheManager.put(cacheCategory, cacheKey, wo );
		}

		//不管是从缓存还是数据库查出来，都要重新进行处理权限判断
		List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
		List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );

		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;

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

		if ( check ) {
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

		//判断用户是否是分类的管理者，分类管理者是有权限编辑文档的
		if (check) {
			try {
				if ( categoryInfoServiceAdv.isCategoryInfoManager( categoryInfo, personName, unitNames, groupNames ) ) {
					isCategoryAdmin = true;
					woControl.setAllowEdit(true);
					woControl.setAllowDelete(true);
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
				if ( appInfoServiceAdv.isAppInfoManager( appInfo, personName, unitNames, groupNames )) {
					isAppAdmin = true;
					woControl.setAllowEdit(true);
					woControl.setAllowDelete(true);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否是栏目管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (isManager || isAppAdmin || isCategoryAdmin || isCreator) {
				woControl.setAllowEdit(true);
				woControl.setAllowDelete(true);
			} else {
				// 判断当前登录者是不是该文档的可编辑者
				try {
					if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
						if ( document.getAuthorPersonList().contains( getShortTargetFlag(personName) )) {
							woControl.setAllowVisit(true);
							woControl.setAllowEdit(true);
						}
					}
					if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
						if( ListTools.containsAny( getShortTargetFlag(unitNames) , document.getAuthorUnitList() )) {
							woControl.setAllowVisit(true);
							woControl.setAllowEdit(true);
						}
					}
					if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
						if( ListTools.containsAny( getShortTargetFlag(groupNames) , document.getAuthorGroupList() )) {
							woControl.setAllowVisit(true);
							woControl.setAllowEdit(true);
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
		wo.setControl(woControl);
		result.setData(wo);
		return result;
	}


	public static class Wo extends GsonPropertyObject {

		public static WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class, Wo.class, null, JpaObject.FieldsInvisible);

		private WoControl control;

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
	}

	public static class WoControl extends GsonPropertyObject {

		@FieldDescribe("是否允许查看.")
		private Boolean allowVisit = false;

		@FieldDescribe("是否允许编辑.")
		private Boolean allowEdit = false;

		@FieldDescribe("是否允许删除.")
		private Boolean allowDelete = false;

		public Boolean getAllowVisit() {
			return allowVisit;
		}

		public void setAllowVisit(Boolean allowVisit) {
			this.allowVisit = allowVisit;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowDelete() {
			return allowDelete;
		}

		public void setAllowDelete(Boolean allowDelete) {
			this.allowDelete = allowDelete;
		}
	}

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
//		public static WrapCopier<FileInfo, WoFileInfo> copier = WrapCopierFactory.wo(FileInfo.class, WoFileInfo.class,
//				null, JpaObject.FieldsInvisible);
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
//
//	public static class WoLog extends Log {
//
//		private static final long serialVersionUID = -5076990764713538973L;
//
//		public static List<String> Excludes = new ArrayList<String>();
//	}
//
//	public static class WoForm extends Form {
//
//		private static final long serialVersionUID = -5076990764713538973L;
//
//		public static List<String> Excludes = new ArrayList<String>();
//	}

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

//	private boolean read( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getReadIdentityList()) && ListTools.isEmpty(woFileInfo.getReadUnitList())) {
//			value = true;
//		} else if (ListTools.containsAny(identities, woFileInfo.getReadIdentityList()) || ListTools.containsAny(units, woFileInfo.getReadUnitList())) {
//			value = true;
//		} else if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList()) || ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList()) || ListTools.containsAny(units, woFileInfo.getControllerUnitList() )) {
//				value = true;
//			}
//		}
//		return value;
//	}
//
//	private boolean edit( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getEditIdentityList()) && ListTools.isEmpty(woFileInfo.getEditUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList()) || ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
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
//			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList()) || ListTools.containsAny(units, woFileInfo.getControllerUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
}
