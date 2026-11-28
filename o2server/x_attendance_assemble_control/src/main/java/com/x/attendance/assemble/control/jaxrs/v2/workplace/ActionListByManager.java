package com.x.attendance.assemble.control.jaxrs.v2.workplace;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.workplace.BaseAction;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ActionListByManager extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionListByManager.class);
	
	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			boolean isManager = business.isManager(effectivePerson);
			if(!isManager && (wi.getBelongs() == null || wi.getBelongs().isEmpty())){
				throw new ExceptionAccessDenied(effectivePerson.getName(), "您不是管理员.");
			}
			if (isManager) {
				List<AttendanceV2WorkPlace> list = emc.listAll(AttendanceV2WorkPlace.class);
				if (list != null) {
					wos = Wo.copier.copy(list);
				}
			} else {
				List<String> unitDNList = business.getAttendanceV2ManagerFactory().belongOrgs(wi.getBelongs(), business);
				List<AttendanceV2WorkPlace> list = emc.listIn(AttendanceV2WorkPlace.class, "belongOrgDn", unitDNList);
				if (list != null) {
					wos = Wo.copier.copy(list);
				}
			}
			// 用 createTime 降序排序
			wos.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
		}
		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 2421260448412274620L;
		@FieldDescribe("所属组织DN")
		private List<String> belongs;


		public List<String> getBelongs() {
			return belongs;
		}

		public void setBelongs(List<String> belongs) {
			this.belongs = belongs;
		}
	}
	
	public static class Wo extends AttendanceV2WorkPlace {


		private static final long serialVersionUID = 6140096299491870719L;
		public static WrapCopier<AttendanceV2WorkPlace, Wo> copier =
				WrapCopierFactory.wo( AttendanceV2WorkPlace.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}