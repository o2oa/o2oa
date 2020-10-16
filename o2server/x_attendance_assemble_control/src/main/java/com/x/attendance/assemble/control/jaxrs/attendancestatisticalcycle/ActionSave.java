package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		// 获取到当前用户信息
		List<String> ids = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (wrapIn != null) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticalCycle _attendanceStatisticalCycle = null;
				AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
				Business business = new Business(emc);
				if (wrapIn.getUnitName() == null || wrapIn.getUnitName().isEmpty()) {
					wrapIn.setUnitName("*");
				}
				if (wrapIn.getTopUnitName() == null || wrapIn.getTopUnitName().isEmpty()) {
					wrapIn.setTopUnitName("*");
				}
				if (wrapIn.getCycleYear() == null || wrapIn.getCycleYear().isEmpty()) {
					wrapIn.setCycleYear("*");
				}

				ids = business.getAttendanceStatisticalCycleFactory().listByParameter(wrapIn.getTopUnitName(),
						wrapIn.getUnitName(), wrapIn.getCycleYear(), wrapIn.getCycleMonth());
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				if ( ListTools.isNotEmpty(ids) ) {
					// 说明有重复的
					_attendanceStatisticalCycle = emc.find(wrapIn.getId(), AttendanceStatisticalCycle.class);
					if (_attendanceStatisticalCycle != null) {
						Wi.copier.copy(wrapIn, _attendanceStatisticalCycle);
						_attendanceStatisticalCycle.setCycleEndDate(
								dateOperation.getDateFromString(_attendanceStatisticalCycle.getCycleEndDateString()));
						_attendanceStatisticalCycle.setCycleStartDate(dateOperation.getDateFromString(
								_attendanceStatisticalCycle.getCycleStartDateString() + " 23:59:59"));

						emc.check(_attendanceStatisticalCycle, CheckPersistType.all);
					}
				} else {
					// 新增就行了
					if ( wrapIn.getId() != null && wrapIn.getId().length() > 10 ) {
						attendanceStatisticalCycle.setId(wrapIn.getId());
					}
					Wi.copier.copy(wrapIn, attendanceStatisticalCycle);
					attendanceStatisticalCycle.setId(wrapIn.getId());// 使用参数传入的ID作为记录的ID
					emc.persist(attendanceStatisticalCycle, CheckPersistType.all);
				}
				emc.commit();

				CacheManager.notify( AttendanceStatisticalCycle.class );

				result.setData(new Wo(attendanceStatisticalCycle.getId()));
			} catch (Exception e) {
				e.printStackTrace();
				Exception exception = new ExceptionStatisticCycleProcess(e, "系统保存统计周期信息对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceStatisticalCycle {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceStatisticalCycle> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceStatisticalCycle.class, null, JpaObject.FieldsUnmodify);
		
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {
		public Wo(String id) {
			setId(id);
		}
	}
}