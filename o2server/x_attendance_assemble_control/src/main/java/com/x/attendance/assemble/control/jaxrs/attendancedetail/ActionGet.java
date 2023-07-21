package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		AttendanceDetail attendanceDetail = null;
		Boolean check = true;

		if (check) {
			if (id == null) {
				check = false;
				Exception exception = new ExceptionDetailIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在根据ID查询员工打卡信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (attendanceDetail == null) {
				check = false;
				Exception exception = new ExceptionDetailNotExists(id);
				result.error(exception);
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy(attendanceDetail);

				//判断并补充申诉信息
				List<AttendanceAppealInfo> appealInfos = null;
				AttendanceAppealAuditInfo appealAuditInfo = null;
				List<WoAttendanceAppealInfo> woAppealInfos = null;
				if( wrap.getAppealStatus() != 0 ){
					//十有八九已经提过申诉了，查询申诉信息
					appealInfos = attendanceAppealInfoServiceAdv.listWithDetailId( wrap.getId() );
					if(ListTools.isNotEmpty( appealInfos ) ){
						woAppealInfos = WoAttendanceAppealInfo.copier.copy( appealInfos );
					}
					if(ListTools.isNotEmpty( woAppealInfos ) ){
						for( WoAttendanceAppealInfo woAppealInfo : woAppealInfos ){
							appealAuditInfo = attendanceAppealInfoServiceAdv.getAppealAuditInfo( woAppealInfo.getId() );
							if( appealAuditInfo != null ){
								woAppealInfo.setAppealAuditInfo( WoAttendanceAppealAuditInfo.copier.copy( appealAuditInfo ));
							}
						}
					}
					wrap.setAppealInfos(woAppealInfos);
				}

				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工打卡信息为输出对象时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);

		@FieldDescribe("考勤申诉内容")
		private List<WoAttendanceAppealInfo> appealInfos = null;

		public List<WoAttendanceAppealInfo> getAppealInfos() { return appealInfos; }

		public void setAppealInfos(List<WoAttendanceAppealInfo> appealInfos) { this.appealInfos = appealInfos; }
	}

}