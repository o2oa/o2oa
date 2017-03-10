package com.x.attendance.assemble.control;

import com.x.attendance.assemble.control.factory.AttendanceAdminFactory;
import com.x.attendance.assemble.control.factory.AttendanceAppealInfoFactory;
import com.x.attendance.assemble.control.factory.AttendanceDetailFactory;
import com.x.attendance.assemble.control.factory.AttendanceDetailMobileFactory;
import com.x.attendance.assemble.control.factory.AttendanceDetailStatisticFactory;
import com.x.attendance.assemble.control.factory.AttendanceEmployeeConfigFactory;
import com.x.attendance.assemble.control.factory.AttendanceImportFileInfoFactory;
import com.x.attendance.assemble.control.factory.AttendanceScheduleSettingFactory;
import com.x.attendance.assemble.control.factory.AttendanceSelfHolidayFactory;
import com.x.attendance.assemble.control.factory.AttendanceSettingFactory;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.assemble.control.factory.AttendanceStatisticalCycleFactory;
import com.x.attendance.assemble.control.factory.AttendanceWorkDayConfigFactory;
import com.x.attendance.assemble.control.factory.AttendanceWorkPlaceFactory;
import com.x.attendance.assemble.control.factory.StatisticCompanyForDayFactory;
import com.x.attendance.assemble.control.factory.StatisticCompanyForMonthFactory;
import com.x.attendance.assemble.control.factory.StatisticDepartmentForDayFactory;
import com.x.attendance.assemble.control.factory.StatisticDepartmentForMonthFactory;
import com.x.attendance.assemble.control.factory.StatisticPersonForMonthFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	
	//人员组织业务处理类
	private Organization organization;
	//系统配置业务处理类
	private AttendanceSettingFactory attendanceSettingFactory;
	//工作场所配置业务处理类
	private AttendanceWorkPlaceFactory attendanceWorkPlaceFactory;
	//节假日工作日配置业务处理类
	private AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory;
	//人员考勤数据导入文件操作业务处理类
	private AttendanceImportFileInfoFactory attendanceImportFileInfoFactory;
	//人员考勤数据业务处理类
	private AttendanceDetailFactory attendanceDetailFactory;
	private AttendanceDetailMobileFactory attendanceDetailMobileFactory;
	//考勤管理员业务处理类
	private AttendanceAdminFactory attendanceAdminFactory;
	//排班管理业务处理类
	private AttendanceScheduleSettingFactory attendanceScheduleSettingFactory;
	//休假申请数据业务处理类
	private AttendanceSelfHolidayFactory attendanceSelfHolidayFactory;
	
	private StatisticCompanyForDayFactory statisticCompanyForDayFactory;
	
	private StatisticCompanyForMonthFactory statisticCompanyForMonthFactory;
	
	private StatisticDepartmentForDayFactory statisticDepartmentForDayFactory;
	
	private StatisticDepartmentForMonthFactory statisticDepartmentForMonthFactory;
	
	private StatisticPersonForMonthFactory statisticPersonForMonthFactory;
	
	private AttendanceAppealInfoFactory attendanceAppealInfoFactory;
	
	private AttendanceStatisticalCycleFactory attendanceStatisticalCycleFactory;
	
	private AttendanceEmployeeConfigFactory attendanceEmployeeConfigFactory;
	
	private AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory;
	
	private AttendanceDetailStatisticFactory attendanceDetailStatisticFactory;
	
	public AttendanceWorkPlaceFactory attendanceWorkPlaceFactory() throws Exception {
		if (null == this.attendanceWorkPlaceFactory) {
			this.attendanceWorkPlaceFactory = new AttendanceWorkPlaceFactory( this );
		}
		return attendanceWorkPlaceFactory;
	}
	
	public AttendanceDetailMobileFactory getAttendanceDetailMobileFactory() throws Exception {
		if (null == this.attendanceDetailMobileFactory) {
			this.attendanceDetailMobileFactory = new AttendanceDetailMobileFactory( this );
		}
		return attendanceDetailMobileFactory;
	}
	
	public AttendanceDetailStatisticFactory getAttendanceDetailStatisticFactory() throws Exception {
		if (null == this.attendanceDetailStatisticFactory) {
			this.attendanceDetailStatisticFactory = new AttendanceDetailStatisticFactory( this );
		}
		return attendanceDetailStatisticFactory;
	}
	
	public AttendanceEmployeeConfigFactory getAttendanceEmployeeConfigFactory() throws Exception {
		if (null == this.attendanceEmployeeConfigFactory) {
			this.attendanceEmployeeConfigFactory = new AttendanceEmployeeConfigFactory( this );
		}
		return attendanceEmployeeConfigFactory;
	}
	
	public AttendanceStatisticRequireLogFactory getAttendanceStatisticRequireLogFactory() throws Exception {
		if (null == this.attendanceStatisticRequireLogFactory) {
			this.attendanceStatisticRequireLogFactory = new AttendanceStatisticRequireLogFactory( this );
		}
		return attendanceStatisticRequireLogFactory;
	}
	
	public AttendanceStatisticalCycleFactory getAttendanceStatisticalCycleFactory() throws Exception {
		if (null == this.attendanceStatisticalCycleFactory) {
			this.attendanceStatisticalCycleFactory = new AttendanceStatisticalCycleFactory( this );
		}
		return attendanceStatisticalCycleFactory;
	}
	
	public AttendanceAppealInfoFactory getAttendanceAppealInfoFactory() throws Exception {
		if (null == this.attendanceAppealInfoFactory) {
			this.attendanceAppealInfoFactory = new AttendanceAppealInfoFactory( this );
		}
		return attendanceAppealInfoFactory;
	}
	
	public StatisticCompanyForDayFactory getStatisticCompanyForDayFactory() throws Exception {
		if (null == this.statisticCompanyForDayFactory) {
			this.statisticCompanyForDayFactory = new StatisticCompanyForDayFactory( this );
		}
		return statisticCompanyForDayFactory;
	}
	public StatisticCompanyForMonthFactory getStatisticCompanyForMonthFactory() throws Exception {
		if (null == this.statisticCompanyForMonthFactory) {
			this.statisticCompanyForMonthFactory = new StatisticCompanyForMonthFactory( this );
		}
		return statisticCompanyForMonthFactory;
	}
	public StatisticDepartmentForDayFactory getStatisticDepartmentForDayFactory() throws Exception {
		if (null == this.statisticDepartmentForDayFactory) {
			this.statisticDepartmentForDayFactory = new StatisticDepartmentForDayFactory( this );
		}
		return statisticDepartmentForDayFactory;
	}
	public StatisticDepartmentForMonthFactory getStatisticDepartmentForMonthFactory() throws Exception {
		if (null == this.statisticDepartmentForMonthFactory) {
			this.statisticDepartmentForMonthFactory = new StatisticDepartmentForMonthFactory( this );
		}
		return statisticDepartmentForMonthFactory;
	}
	public StatisticPersonForMonthFactory getStatisticPersonForMonthFactory() throws Exception {
		if (null == this.statisticPersonForMonthFactory) {
			this.statisticPersonForMonthFactory = new StatisticPersonForMonthFactory( this );
		}
		return statisticPersonForMonthFactory;
	}
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization();
		}
		return organization;
	}
	
	public AttendanceSettingFactory getAttendanceSettingFactory() throws Exception {
		if (null == this.attendanceSettingFactory) {
			this.attendanceSettingFactory = new AttendanceSettingFactory( this );
		}
		return attendanceSettingFactory;
	}
	
	public AttendanceWorkDayConfigFactory getAttendanceWorkDayConfigFactory() throws Exception {
		if (null == this.attendanceWorkDayConfigFactory) {
			this.attendanceWorkDayConfigFactory = new AttendanceWorkDayConfigFactory( this );
		}
		return attendanceWorkDayConfigFactory;
	}
	
	public AttendanceImportFileInfoFactory getAttendanceImportFileInfoFactory() throws Exception {
		if (null == this.attendanceImportFileInfoFactory) {
			this.attendanceImportFileInfoFactory = new AttendanceImportFileInfoFactory( this );
		}
		return attendanceImportFileInfoFactory;
	}
	
	public AttendanceDetailFactory getAttendanceDetailFactory() throws Exception {
		if (null == this.attendanceDetailFactory) {
			this.attendanceDetailFactory = new AttendanceDetailFactory( this );
		}
		return attendanceDetailFactory;
	}
	public AttendanceAdminFactory getAttendanceAdminFactory() throws Exception {
		if (null == this.attendanceAdminFactory) {
			this.attendanceAdminFactory = new AttendanceAdminFactory( this );
		}
		return attendanceAdminFactory;
	}
	
	public AttendanceScheduleSettingFactory getAttendanceScheduleSettingFactory() throws Exception {
		if (null == this.attendanceScheduleSettingFactory) {
			this.attendanceScheduleSettingFactory = new AttendanceScheduleSettingFactory( this );
		}
		return attendanceScheduleSettingFactory;
	}
	
	public AttendanceSelfHolidayFactory getAttendanceSelfHolidayFactory() throws Exception {
		if (null == this.attendanceSelfHolidayFactory) {
			this.attendanceSelfHolidayFactory = new AttendanceSelfHolidayFactory( this );
		}
		return attendanceSelfHolidayFactory;
	}
}
