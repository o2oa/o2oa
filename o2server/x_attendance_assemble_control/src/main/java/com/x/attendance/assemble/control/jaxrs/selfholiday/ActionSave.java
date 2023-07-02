package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		List<AttendanceSelfHoliday> holidayList = null;

		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				AttendanceSelfHoliday attendanceSelfHoliday = null;

				if( wrapIn != null && StringUtils.isNoneEmpty( wrapIn.getEmployeeName() )
						&& StringUtils.isNoneEmpty( wrapIn.getLeaveType() )
						&& wrapIn.getStartTime() != null
						&& wrapIn.getEndTime() != null
				){
					if( wrapIn.getEmployeeName().indexOf("@P") < 0 ){//不是DistinguishedName
						if( wrapIn.getEmployeeName().indexOf("@I") > 0 ){
							String personName = userManagerService.getPersonNameByIdentity(wrapIn.getEmployeeName());
							if( StringUtils.isNotEmpty( personName ) ){
								wrapIn.setEmployeeName(personName);
							}
						}
					}

					if( StringUtils.isNotEmpty( wrapIn.getEmployeeName() )){
						Person person = userManagerService.getPersonObjByName(wrapIn.getEmployeeName());
						if( person != null ){
							wrapIn.setEmployeeName( person.getDistinguishedName() );
							if( StringUtils.isNotEmpty( person.getEmployee() )){
								wrapIn.setEmployeeNumber( person.getEmployee() );
							}else{
								wrapIn.setEmployeeNumber( person.getDistinguishedName() );
							}
						}

						//补充员工的组织信息
						Unit unit = userManagerService.getUnitWithPersonName( wrapIn.getEmployeeNumber() );
						Unit topUnit = null;
						String unitName = null;
						String unitOu = null;
						String topUnitName = null;
						String topUnitOu = null;
						if( unit != null ){
							unitName = unit.getName();
							unitOu = unit.getDistinguishedName();
							topUnit = userManagerService.getTopUnitWithUnitName(unitOu);
						}
						if( topUnit != null ){
							topUnitName = topUnit.getName();
							topUnitOu = topUnit.getDistinguishedName();
						}
						emc.beginTransaction( AttendanceSelfHoliday.class );
						//先根据batchFlag删除原来的数据，然后再进行新数据的保存
						if(StringUtils.isNotEmpty( wrapIn.getBatchFlag() ) ){
							holidayList = attendanceSelfHolidayServiceAdv.listWithBatchFlag( wrapIn.getBatchFlag() );
							if(ListTools.isNotEmpty( holidayList )){
								logger.info("先根据batchFlag删除原来的数据，然后再进行新数据的保存" );
								for( AttendanceSelfHoliday holiday : holidayList ){
									emc.remove( emc.find(holiday.getId(), AttendanceSelfHoliday.class ), CheckRemoveType.all );
								}
								logger.info("删除" + holidayList.size() + "条旧请假信息数据。" );
							}
						}
						if( StringUtils.isNotEmpty( wrapIn.getId() ) ){
							//根据ID查询信息是否存在，如果存在就update，如果不存在就create
							attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
							if( attendanceSelfHoliday != null ){
								//更新已经存在的信息
								wrapIn.copyTo( attendanceSelfHoliday );
								attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
								logger.info("更新：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

								attendanceSelfHoliday.setUnitName( unitName );
								attendanceSelfHoliday.setUnitOu( unitOu );
								attendanceSelfHoliday.setTopUnitName( topUnitName );
								attendanceSelfHoliday.setTopUnitOu( topUnitOu );

								emc.check( attendanceSelfHoliday, CheckPersistType.all);
							}else{
								attendanceSelfHoliday = new AttendanceSelfHoliday();
								wrapIn.copyTo( attendanceSelfHoliday );
								//使用参数传入的ID作为记录的ID
								attendanceSelfHoliday.setId( wrapIn.getId() );
								attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
								logger.info("新增：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

								attendanceSelfHoliday.setUnitName( unitName );
								attendanceSelfHoliday.setUnitOu( unitOu );
								attendanceSelfHoliday.setTopUnitName( topUnitName );
								attendanceSelfHoliday.setTopUnitOu( topUnitOu );

								emc.persist( attendanceSelfHoliday, CheckPersistType.all);
							}
						}else{
							//没有传入指定的ID
							attendanceSelfHoliday = new AttendanceSelfHoliday();

							wrapIn.copyTo( attendanceSelfHoliday );
							attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
							logger.debug("新增,无ID：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

							attendanceSelfHoliday.setUnitName( unitName );
							attendanceSelfHoliday.setUnitOu( unitOu );
							attendanceSelfHoliday.setTopUnitName( topUnitName );
							attendanceSelfHoliday.setTopUnitOu( topUnitOu );

							emc.persist( attendanceSelfHoliday, CheckPersistType.all);
							result.setData( new Wo( attendanceSelfHoliday.getId() ) );
						}
						emc.commit();
						result.setData( new Wo( attendanceSelfHoliday.getId() ) );

						//清除缓存
						//ApplicationCache.notify( AttendanceSelfHoliday.class );
						CacheManager.notify(AttendanceSelfHoliday.class);

						//根据员工休假数据来记录与这条数据相关的统计需求记录
						//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
						logger.debug("休假数据有变动，对该员工的该请假时间内的所有打卡记录进行分析......" );

						//休假数据有更新，对该员工的该请假时间内的所有打卡记录进行分析
						List<String> ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds(attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
						if( ListTools.isNotEmpty( ids ) ){
							for( String id : ids ){
								try { //分析保存好的考勤数据
									ThisApplication.detailAnalyseQueue.send( id );
								} catch ( Exception e1 ) {
									e1.printStackTrace();
								}
							}
						}
					}
				}else{
					if( jsonElement == null ){
						logger.debug("传入的员工请假信息JSON数据为空......" );
						Exception exception = new ExceptionSelfHolidayProcess( "传入的员工请假信息JSON数据为空，无法保存数据信息，请检查数据内容！" );
						result.error( exception );
					}else {
						logger.debug("传入的数据不符合请假数据接口要求......" );
						Exception exception = new ExceptionSelfHolidayProcess( "传入的数据不符合请假数据接口要求，请检查数据内容：" + jsonElement.getAsString() );
						result.error( exception );
					}

				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionSelfHolidayProcess( e, "系统在保存员工请假记录信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("ID，如果ID已存在，则为更新")
		private String id;

		@FieldDescribe("员工姓名：员工的标识，<font color='red'>必填</font>，员工的distinguishedName，如：张三@zhangsan@P")
		private String employeeName;

		private String employeeNumber;

		@FieldDescribe("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|出差|培训|其他，<font color='red'>必填</font>")
		private String leaveType;

		@FieldDescribe("开始时间，<font color='red'>必填</font>: yyyy-mm-dd hh24:mi:ss")
		private Date startTime;

		@FieldDescribe("结束时间，<font color='red'>必填</font>: yyyy-mm-dd hh24:mi:ss")
		private Date endTime;

		@FieldDescribe("请假天数，最小粒度，0.5天，<font color='red'>必填</font>")
		private Double leaveDayNumber = 0.0;

		@FieldDescribe("请假说明")
		private String description;

//		@FieldDescribe("流程WorkId")
//		private String docId;

		@FieldDescribe("录入批次标识：可以填写流程workId，jobId, CMS的文档ID，或者自定义信息，数据保存时会先根据batchFlag做删除，然后再保存新的数据")
		private String batchFlag;

		public String getId() { return id; }

		public void setId(String id) { this.id = id; }

		public String getEmployeeName() { return employeeName; }

		public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

		public String getEmployeeNumber() { return employeeNumber; }

		public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

		public String getLeaveType() { return leaveType; }

		public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

		public Date getStartTime() { return startTime; }

		public void setStartTime(Date startTime) { this.startTime = startTime; }

		public Date getEndTime() { return endTime; }

		public void setEndTime(Date endTime) { this.endTime = endTime; }

		public Double getLeaveDayNumber() { return leaveDayNumber; }

		public void setLeaveDayNumber(Double leaveDayNumber) { this.leaveDayNumber = leaveDayNumber; }

		public String getDescription() { return description; }

		public void setDescription(String description) { this.description = description; }

		public String getBatchFlag() { return batchFlag; }

		public void setBatchFlag(String batchFlag) { this.batchFlag = batchFlag; }

		public void copyTo(AttendanceSelfHoliday attendanceSelfHoliday) {
			attendanceSelfHoliday.setBatchFlag( this.batchFlag );
			attendanceSelfHoliday.setDescription( this.description );
//			attendanceSelfHoliday.setDocId( this.batchFlag );
			attendanceSelfHoliday.setEmployeeName( this.employeeName );
			attendanceSelfHoliday.setEmployeeNumber( this.employeeNumber );
			attendanceSelfHoliday.setStartTime( this.startTime );
			attendanceSelfHoliday.setEndTime( this.endTime );
			attendanceSelfHoliday.setLeaveDayNumber( this.leaveDayNumber );
			attendanceSelfHoliday.setLeaveType( this.leaveType );

		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}