package com.x.attendance.assemble.control.jaxrs.v2.workplace;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.workplace.BaseAction;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActionListWithWorkPlaceObject extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionListWithWorkPlaceObject.class);
	
	protected ActionResult<List<Wo>> execute(JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (wi.getIdList() == null || wi.getIdList().isEmpty()) {
			throw new ExceptionEmptyParameter("工作地点id");
		}
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (String id : wi.getIdList()) {
				AttendanceV2WorkPlace workPlace = emc.find(id, AttendanceV2WorkPlace.class);
				if (workPlace != null) {
					wos.add(Wo.copier.copy(workPlace));
				}
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

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 3497915092528751450L;
		@FieldDescribe("工作地点id")
		private List<String> idList = new ArrayList<>();


		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}
	}
}