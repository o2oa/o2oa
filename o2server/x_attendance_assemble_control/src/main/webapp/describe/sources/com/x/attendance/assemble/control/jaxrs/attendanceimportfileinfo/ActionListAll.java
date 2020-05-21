package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);
	private Gson gson = XGsonBuilder.instance();

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceImportFileInfo> attendanceSettingList = null;
		StatusImportFileDetail statusImportFileDetail = null;
		Boolean check = true;

		if (check) {
			try {
				attendanceSettingList = attendanceImportFileInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceImportFileProcess(e, "系统查询所有员工考勤数据导入文件信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && attendanceSettingList != null) {

			for (AttendanceImportFileInfo fileInfo : attendanceSettingList) {
				if (!"COMPLETED".equals(fileInfo.getCurrentProcessName())) {
					// 如果未完成 ，则在系统文件导入处理状态对象中创建该文件的详细对象
					try {
						if ( StringUtils.isNotEmpty( fileInfo.getDataContent() )) {
							// 还原为对象
							statusImportFileDetail = gson.fromJson(fileInfo.getDataContent(),
									StatusImportFileDetail.class);
							StatusSystemImportOpt.getInstance().addCacheImportFileStatus(statusImportFileDetail);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAttendanceEmployeeProcess(e, "将文件恢复到内存文件处理状态列表时发生异常.");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			try {
				wraps = Wo.copier.copy(attendanceSettingList);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e,
						"将所有查询出来的有状态的导入文件对象转换为可以输出的过滤过属性的对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceImportFileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static {
			Excludes.add("errorContent");
			Excludes.add("dataContent");
			Excludes.add("fileBody");
		}

		public static WrapCopier<AttendanceImportFileInfo, Wo> copier = WrapCopierFactory
				.wo(AttendanceImportFileInfo.class, Wo.class, null, Wo.Excludes);
	}
}