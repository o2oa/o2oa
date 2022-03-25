package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;


public class ActionSaveExtContent extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSaveExtContent.class);

	@AuditLog(operation = "保存分类扩展信息")
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		CategoryExt categoryExt = null;
		Wi wi = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if( StringUtils.isEmpty(wi.getId()) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}

		if( StringUtils.isEmpty(wi.getContent()) ){
			wi.setContent( "{}" );
		}

		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( wi.getId() );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( wi.getId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。ID:" + wi.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			categoryExt = categoryInfoServiceAdv.saveExtContent( wi.getId(), wi.getContent(), effectivePerson );

			CacheManager.notify(CategoryInfo.class);

			new LogService().log(null, effectivePerson.getDistinguishedName(),
					categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "",
					"", "CATEGORY", "保存扩展信息");
			
			Wo wo = new Wo();
			wo.setId(categoryExt.getId());
			result.setData(wo);
		}
		
		return result;
	}

	public static class Wi extends CategoryExt {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);
		
	}

	public static class Wo extends WoId {

	}
}