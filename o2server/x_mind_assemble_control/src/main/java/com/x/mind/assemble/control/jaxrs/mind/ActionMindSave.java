package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

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
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindPersist;
import com.x.mind.entity.MindBaseInfo;

/**
 * 保存脑图信息
 * @author O2LEE
 *
 */
public class ActionMindSave extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindSave.class );

	@AuditLog(operation = "保存脑图文件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Wo wo = new Wo();
		MindBaseInfo mindBaseInfo = null;
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
			List<String> editorList = new ArrayList<>();
			editorList.add( effectivePerson.getDistinguishedName());
			try {
				wi.setCreator(effectivePerson.getDistinguishedName());
				wi.setCreatorUnit( creatorUnit );
				wi.setEditorList(editorList);
				mindBaseInfo = mindInfoService.save( Wi.copier.copy(wi),  wi.getContent(), 50 );
				if( mindBaseInfo != null ) {
					wo.setId(mindBaseInfo.getId());
					result.setData(wo);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindPersist(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends MindBaseInfo {
		private static final long serialVersionUID = -6314932919066148113L;
		public static WrapCopier<Wi, MindBaseInfo> copier = WrapCopierFactory.wi( Wi.class, MindBaseInfo.class, null, null );
		
		private String content = null;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
	}

	public static class Wo extends WoId {

	}
}