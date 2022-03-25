package com.x.mind.assemble.control.jaxrs.folder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionEntityCanNotDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderPersist;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.entity.MindFolderInfo;

/**
 * 保存脑图文件夹信息
 * @author O2LEE
 *
 */
public class ActionFolderSave extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFolderSave.class );

	@AuditLog(operation = "保存文件目录")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Wo wo = new Wo();
		MindFolderInfo mindFolderInfo = null;
		Boolean check = true;
		String creatorUnit = userManagerService.getUnitNameWithPerson(effectivePerson.getDistinguishedName());
		
		if( StringUtils.isEmpty( creatorUnit )) {
			check = false;
			Exception exception = new ExceptionEntityCanNotDelete("请为创建者分配组织后再进行此操作！");
			result.error(exception);
		}
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			if(wi.getId() != null && wi.getId().length()<10) {
				wi.setId(MindFolderInfo.createId());
			}
		}
		if( check ){
			try {
				wi.setCreator(effectivePerson.getDistinguishedName());
				wi.setCreatorUnit( creatorUnit );
				mindFolderInfo = mindFolderInfoService.save( Wi.copier.copy(wi) );
				if( mindFolderInfo != null ) {
					wo.setId(mindFolderInfo.getId());
					result.setData(wo);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderPersist(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends MindFolderInfo {
		private static final long serialVersionUID = -6314932919066148113L;
		public static WrapCopier<Wi, MindFolderInfo> copier = WrapCopierFactory.wi( Wi.class, MindFolderInfo.class, null, null );
	}

	public static class Wo extends WoId {

	}
}