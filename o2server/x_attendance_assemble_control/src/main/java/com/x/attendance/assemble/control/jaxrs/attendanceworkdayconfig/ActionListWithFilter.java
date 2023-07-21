package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.factory.AttendanceWorkDayConfigFactory;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<String> ids = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Business business = null;
		AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory = null;
		String q_Name = null;
		String q_Year = null;
		String q_Month = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			q_Name = wrapIn.getQ_Name();
			q_Year = wrapIn.getQ_Year();
			q_Month = wrapIn.getQ_Month();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				attendanceWorkDayConfigFactory = business.getAttendanceWorkDayConfigFactory();
				// 获取所有应用列表
				if ( StringUtils.isNotEmpty( q_Year )) {
					if ( StringUtils.isNotEmpty( q_Month )) {
						// 根据年份月份获取所有节假日配置列表
						logger.debug(effectivePerson, ">>>>>>>>>>根据年份月份获取所有节假日配置列表");
						ids = attendanceWorkDayConfigFactory.listByYearAndMonth(q_Year, q_Month);
					}
					if ( StringUtils.isNotEmpty( q_Name )) {
						// 根据年份名称获取所有节假日配置列表
						logger.debug(effectivePerson, ">>>>>>>>>>根据年份名称获取所有节假日配置列表");
						ids = attendanceWorkDayConfigFactory.listByYearAndName(q_Year, q_Name);
					}
				} else {
					if ( StringUtils.isNotEmpty( q_Name )) {
						// 根据名称获取所有节假日配置列表
						logger.debug(effectivePerson, ">>>>>>>>>>根据名称获取所有节假日配置列表");
						ids = attendanceWorkDayConfigFactory.listByName(q_Name);
					}
				}
				// 查询ID IN ids 的所有应用信息列表
				attendanceWorkDayConfigList = attendanceWorkDayConfigFactory.list(ids);
				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = Wo.copier.copy(attendanceWorkDayConfigList);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String q_Name = null;

		@FieldDescribe("年")
		private String q_Year = null;

		@FieldDescribe("月")
		private String q_Month = null;

		public String getQ_Name() {
			return q_Name;
		}

		public void setQ_Name(String q_Name) {
			this.q_Name = q_Name;
		}

		public String getQ_Year() {
			return q_Year;
		}

		public void setQ_Year(String q_Year) {
			this.q_Year = q_Year;
		}

		public String getQ_Month() {
			return q_Month;
		}

		public void setQ_Month(String q_Month) {
			this.q_Month = q_Month;
		}
	}

	public static class Wo extends AttendanceWorkDayConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceWorkDayConfig, Wo> copier = WrapCopierFactory
				.wo(AttendanceWorkDayConfig.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}