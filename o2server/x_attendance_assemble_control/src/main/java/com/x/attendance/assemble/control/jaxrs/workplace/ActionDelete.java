package com.x.attendance.assemble.control.jaxrs.workplace;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				Exception exception = new ExceptionWorkPlaceIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceWorkPlaceServiceAdv.delete(id);
				result.setData(new Wo(id));
			} catch (Exception e) {
				Exception exception = new ExceptionWorkPlaceProcess(e, "工作场所名称不允许为空，无法进行数据保存。ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}