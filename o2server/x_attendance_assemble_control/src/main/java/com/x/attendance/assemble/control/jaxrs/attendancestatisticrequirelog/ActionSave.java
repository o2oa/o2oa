package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (wrapIn != null) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticRequireLog _attendanceStatisticRequireLog = null;
				AttendanceStatisticRequireLog attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				if (wrapIn.getId() != null && wrapIn.getId().length() > 10) {
					// 根据ID查询信息是否存在，如果存在就update，如果不存在就create
					_attendanceStatisticRequireLog = emc.find(wrapIn.getId(), AttendanceStatisticRequireLog.class);
					if (_attendanceStatisticRequireLog != null) {
						// 更新
						emc.beginTransaction(AttendanceStatisticRequireLog.class);
						Wi.copier.copy(wrapIn, _attendanceStatisticRequireLog);
						emc.check(_attendanceStatisticRequireLog, CheckPersistType.all);
						emc.commit();
					} else {
						emc.beginTransaction(AttendanceStatisticRequireLog.class);
						Wi.copier.copy(wrapIn, attendanceStatisticRequireLog);
						attendanceStatisticRequireLog.setId(wrapIn.getId());// 使用参数传入的ID作为记录的ID
						emc.persist(attendanceStatisticRequireLog, CheckPersistType.all);
						emc.commit();
					}
				} else {
					// 没有传入指定的ID
					emc.beginTransaction(AttendanceStatisticRequireLog.class);
					Wi.copier.copy(wrapIn, attendanceStatisticRequireLog);
					emc.persist(attendanceStatisticRequireLog, CheckPersistType.all);
					emc.commit();
				}
				result.setData(new Wo(attendanceStatisticRequireLog.getId()));
			} catch (Exception e) {
				Exception exception = new ExceptionStatisticRequireProcess(e, "系统保存统计周期信息对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceStatisticRequireLog {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceStatisticRequireLog> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceStatisticRequireLog.class, null, JpaObject.FieldsUnmodify);
		
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