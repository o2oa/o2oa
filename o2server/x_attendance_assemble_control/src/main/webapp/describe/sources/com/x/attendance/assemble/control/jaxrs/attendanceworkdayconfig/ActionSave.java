package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig.exception.ExceptionWorkDayConfigProcess;
import com.x.attendance.entity.AttendanceWorkDayConfig;
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
		// 获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		DateOperation dateOperation = new DateOperation();
		Boolean check = true;
		Date date = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceWorkDayConfig _attendanceWorkDayConfig = null;
				AttendanceWorkDayConfig attendanceWorkDayConfig = new AttendanceWorkDayConfig();
				if (wrapIn != null && wrapIn.getId() != null && wrapIn.getId().length() > 10) {
					if (wrapIn.getId() != null && wrapIn.getId().length() > 10) {
						// 根据ID查询信息是否存在，如果存在就update，如果不存在就create
						_attendanceWorkDayConfig = emc.find(wrapIn.getId(), AttendanceWorkDayConfig.class);
						if (_attendanceWorkDayConfig != null) {
							// 更新
							emc.beginTransaction(AttendanceWorkDayConfig.class);
							Wi.copier.copy(wrapIn, _attendanceWorkDayConfig);
							try {
								date = dateOperation.getDateFromString(_attendanceWorkDayConfig.getConfigDate());
								_attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
								_attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
								emc.check(_attendanceWorkDayConfig, CheckPersistType.all);
								emc.commit();
								result.setData(new Wo(_attendanceWorkDayConfig.getId()));
							} catch (Exception e) {
								Exception exception = new ExceptionWorkDayConfigProcess(e,
										"系统在格式化节假日配置的日期时发生异常.Date:" + _attendanceWorkDayConfig.getConfigDate());
								result.error(exception);
								logger.error(e, currentPerson, request, null);
							}
						} else {
							emc.beginTransaction(AttendanceWorkDayConfig.class);
							Wi.copier.copy(wrapIn, attendanceWorkDayConfig);
							attendanceWorkDayConfig.setId(wrapIn.getId());// 使用参数传入的ID作为记录的ID
							try {
								date = dateOperation.getDateFromString(attendanceWorkDayConfig.getConfigDate());
								attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
								attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
								emc.persist(attendanceWorkDayConfig, CheckPersistType.all);
								emc.commit();
								result.setData(new Wo(attendanceWorkDayConfig.getId()));
							} catch (Exception e) {
								Exception exception = new ExceptionWorkDayConfigProcess(e,
										"系统在格式化节假日配置的日期时发生异常.Date:" + attendanceWorkDayConfig.getConfigDate());
								result.error(exception);
								logger.error(e, currentPerson, request, null);
							}
						}
					} else {
						// 没有传入指定的ID
						emc.beginTransaction(AttendanceWorkDayConfig.class);
						Wi.copier.copy(wrapIn, attendanceWorkDayConfig);
						try {
							date = dateOperation.getDateFromString(attendanceWorkDayConfig.getConfigDate());
							attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
							attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
							emc.persist(attendanceWorkDayConfig, CheckPersistType.all);
							emc.commit();
							result.setData(new Wo(attendanceWorkDayConfig.getId()));
						} catch (Exception e) {
							Exception exception = new ExceptionWorkDayConfigProcess(e,
									"系统在格式化节假日配置的日期时发生异常.Date:" + attendanceWorkDayConfig.getConfigDate());
							result.error(exception);
							logger.error(e, currentPerson, request, null);
						}
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkDayConfigProcess(e, "系统保存节假日工作日配置信息对象时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceWorkDayConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceWorkDayConfig> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceWorkDayConfig.class, null, JpaObject.FieldsUnmodify);
		
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