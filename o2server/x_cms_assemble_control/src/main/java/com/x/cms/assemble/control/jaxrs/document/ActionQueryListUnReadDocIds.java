package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionQueryListUnReadDocIds extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListUnReadDocIds.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		List<String> nonReadIds = null;
		Wo wo = new Wo();
		Wi wi = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if( ListTools.isEmpty( wi.getIds() )) {
				check = false;
				Exception exception = new ExceptionDocumentIdEmpty();
				result.error( exception );
			}
		}

		if (check) {	
			//查询在ID范围内用户已读的文档ID列表
			try {
				nonReadIds = documentViewRecordServiceAdv.listReadDocId( wi.getIds(), effectivePerson );
				
				//取差集
				wi.getIds().removeAll( nonReadIds );
				wo.setUnReadDocIds( wi.getIds() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "系统在根据指定ID列表查询未读文档ID列表时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "需要查询是否已阅读过的文档ID列表." )
		private List<String> ids = null;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "未读文档ID列表." )
		private List<String> unReadDocIds = null;

		public List<String> getUnReadDocIds() {
			return unReadDocIds;
		}

		public void setUnReadDocIds(List<String> unReadDocIds) {
			this.unReadDocIds = unReadDocIds;
		}
	}
}