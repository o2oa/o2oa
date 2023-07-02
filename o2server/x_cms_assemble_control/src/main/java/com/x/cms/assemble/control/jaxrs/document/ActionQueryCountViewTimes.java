package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionQueryCountViewTimes extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionQueryCountViewTimes.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Long count = 0L;
		Boolean check = true;
		Wo wo = new Wo();
		
		if( StringUtils.isEmpty(id) ){
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				count = documentQueryService.getViewCount( id );
				if( count == null ){
					count = 0L;
				}
				wo.setCount(count);
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "系统在查询文档访问次数时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	
	public static class Wo extends GsonPropertyObject {

		public Wo(Long count) throws Exception {
			this.count = count;
		}

		public Wo(Integer count) throws Exception {
			this.count = count.longValue();
		}

		public Wo() {
		}

		private Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public void setCount(Integer count) {
			this.count = count.longValue();
		}

	}

}