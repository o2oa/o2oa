package com.x.attendance.assemble.control.jaxrs.v2.workplace;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.workplace.BaseAction;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActionListAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);
	
	protected ActionResult<List<Wo>> execute() throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<AttendanceV2WorkPlace> list = emc.listAll(AttendanceV2WorkPlace.class);
			if (list != null) {
				wos = Wo.copier.copy(list);
			}
		}
		result.setData(wos);
		return result;
	}
	
	public static class Wo extends AttendanceV2WorkPlace {


		private static final long serialVersionUID = 2774545021305443024L;
		public static WrapCopier<AttendanceV2WorkPlace, Wo> copier =
				WrapCopierFactory.wo( AttendanceV2WorkPlace.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}