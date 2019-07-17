package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.ViewCategory;


public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String identityName = null;
		String unitName = null;
		String topUnitName = null;
		Wi wi = null;
		CategoryInfo old_categoryInfo = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			identityName = wi.getIdentity();
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if ( StringUtils.isEmpty( identityName ) && !"xadmin".equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				try {
					identityName = userManagerService.getPersonIdentity( effectivePerson.getDistinguishedName(), identityName);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess(e, "系统获取人员身份名称时发生异常，指定身份：" + identityName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				identityName = "xadmin";
				unitName = "xadmin";
				topUnitName = "xadmin";
			}
		}
		
		if( check ) {
			if ( StringUtils.isEmpty( identityName ) ) {
				identityName = wi.getCreatorIdentity();
			}
		}
		
		if ( check && !"xadmin".equals( identityName ) ) {
			try {
				unitName = userManagerService.getUnitNameByIdentity(identityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在根据用户身份信息查询所属组织名称时发生异常。Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check && !"xadmin".equals( identityName )) {
			try {
				topUnitName = userManagerService.getTopUnitNameByIdentity(identityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在根据用户身份信息查询所属顶层组织名称时发生异常。Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getId() )) {
				wi.setId( CategoryInfo.createId() );
			}
			try {
				//先查询原来的分类信息，如果有的话，后续需要对比一下是否修改了分类名称，如果修改了分类名称 ，那么需要修改所有文档中记录的分类名称
				old_categoryInfo = categoryInfoServiceAdv.get( wi.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在根据ID查询分类信息时发生异常。ID:" + wi.getId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			wi.setCreatorIdentity( identityName );
			wi.setCreatorPerson( effectivePerson.getDistinguishedName() );
			wi.setCreatorUnitName(unitName);
			wi.setCreatorTopUnitName(topUnitName);
			
			try {
				categoryInfo = categoryInfoServiceAdv.save( wi, wi.getExtContent(), effectivePerson );

				Wo wo = new Wo();
				wo.setId(categoryInfo.getId());
				result.setData(wo);
				
				if( old_categoryInfo != null ) {
					if( !old_categoryInfo.getCategoryName().equalsIgnoreCase( categoryInfo.getCategoryName() ) || 
							 !old_categoryInfo.getCategoryAlias().equalsIgnoreCase( categoryInfo.getCategoryAlias() )	) {
						//修改了分类名称，增加删除栏目批量操作（对分类和文档）的信息
						new CmsBatchOperationPersistService().addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_CATEGORY, 
								CmsBatchOperationProcessService.OPT_TYPE_UPDATENAME,  categoryInfo.getId(), old_categoryInfo.getCategoryName(), "更新分类名称：ID=" + categoryInfo.getId() );
					}
					if(  permissionQueryService.hasDiffrentViewPermissionInCategory( old_categoryInfo, categoryInfo )) {
						//修改了栏目名称或者别名，增加删除栏目批量操作（对分类和文档）的信息
						new CmsBatchOperationPersistService().addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_CATEGORY, 
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  categoryInfo.getId(), categoryInfo.getCategoryName(), "变更分类可见权限：ID=" + categoryInfo.getId() );
					}
					new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "", "", "CATEGORY", "更新");
				}else {
					new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "", "", "CATEGORY", "新增");
				}
				
				ApplicationCache.notify(AppInfo.class);
				ApplicationCache.notify(CategoryInfo.class);
				ApplicationCache.notify(ViewCategory.class);
				ApplicationCache.notify(Document.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "分类信息在保存时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

		public static WrapCopier<Wi, CategoryInfo> copier = WrapCopierFactory.wi(Wi.class, CategoryInfo.class, null,
				JpaObject.FieldsUnmodify);

		@FieldDescribe("用户保存的身份")
		private String identity = null;
		
		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}

	}

	public static class Wo extends WoId {

	}
}