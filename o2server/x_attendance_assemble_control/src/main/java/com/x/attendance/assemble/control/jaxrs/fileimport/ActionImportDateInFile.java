package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.DateRecord;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionImportDateInFile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionImportDateInFile.class);
	
	protected ActionResult<List<DateRecord>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String file_id ) throws Exception {
		ActionResult<List<DateRecord>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		
		logger.info("user[" + currentPerson.getDistinguishedName() + "] try to import detail from file{'id':'" + file_id + "'}......");
		StatusSystemImportOpt.getInstance().setDebugger( currentPerson.getDebugger() );
		
		logger.info("从全局数据检查缓存中获取文件[ID=" + file_id + "]的检查数据结果......");
		StatusImportFileDetail cacheImportFileStatus = StatusSystemImportOpt.getInstance().getCacheImportFileStatus( file_id );
		
		AttendanceDetail attendanceDetail = null;
		AttendanceDetail _attendanceDetail = null;
		
		Business business = null;
		List<String> ids = null;
		
		if ( cacheImportFileStatus != null && "success".equalsIgnoreCase(cacheImportFileStatus.getCheckStatus()) && cacheImportFileStatus.getDetailList() != null && cacheImportFileStatus.getDetailList().size() > 0) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				// 先删除这一个文件ID下的所有记录
				logger.info("查询batchName = " + file_id + "的所有数据记录......");
				ids = business.getAttendanceDetailFactory().listByBatchName(file_id);
				logger.info("查询结果" + ids.size() + "条！");
				logger.info("开始事务，删除batchName = " + file_id + "的" + ids.size() + "条数据记录......");
				emc.beginTransaction(AttendanceDetail.class);
				if (ListTools.isNotEmpty( ids )) {
					for (String id : ids) {
						_attendanceDetail = emc.find(id, AttendanceDetail.class);
						emc.remove(_attendanceDetail);
					}
				}
				emc.commit();

				logger.info("开始保存需要导入的数据，新增batchName = " + file_id + "的" + cacheImportFileStatus.getDetailList().size() + "条数据记录......");

				List<DateRecord> dateRecordList = new ArrayList<DateRecord>();
				boolean dateRecordExsist = false;

				// 对每一条进行检查，如果在不需要考勤的人员名单里，那么就不进行存储。
				for ( EntityImportDataDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ) {
					logger.info("查询数据库里是否有重复记录：姓名：" + cacheImportRowDetail.getEmployeeName() + "， 日期：" + cacheImportRowDetail.getRecordDateStringFormated());
					ids = business.getAttendanceDetailFactory().getByUserAndRecordDate( cacheImportRowDetail.getEmployeeName(), cacheImportRowDetail.getRecordDateStringFormated() );
					if (ListTools.isNotEmpty( ids )) {
						for (String id : ids) {
							logger.info("查询数据库里是否有重复记录：id=" + id);
							attendanceDetail = emc.find(id, AttendanceDetail.class);
							if (attendanceDetail != null) {
								logger.info("删除[" + cacheImportRowDetail.getEmployeeName() + "][" + cacheImportRowDetail.getRecordDateStringFormated() + "]已存在的打卡数据......");
								emc.beginTransaction(AttendanceDetail.class);
								emc.remove(attendanceDetail);
								emc.commit();
							}
						}
					}

					attendanceDetail = new AttendanceDetail();
					attendanceDetail.setEmpNo(cacheImportRowDetail.getEmployeeNo());
					attendanceDetail.setEmpName(cacheImportRowDetail.getEmployeeName());
					attendanceDetail.setYearString(cacheImportRowDetail.getRecordYearString());
					attendanceDetail.setMonthString(cacheImportRowDetail.getRecordMonthString());
					attendanceDetail.setRecordDate( cacheImportRowDetail.getRecordDate()  );
					attendanceDetail.setRecordDateString(cacheImportRowDetail.getRecordDateStringFormated());
					attendanceDetail.setOnDutyTime(cacheImportRowDetail.getOnDutyTimeFormated());
					attendanceDetail.setOffDutyTime(cacheImportRowDetail.getOffDutyTimeFormated());
					attendanceDetail.setRecordStatus(0);
					attendanceDetail.setBatchName(file_id);
					
					emc.beginTransaction(AttendanceDetail.class);
					emc.persist(attendanceDetail, CheckPersistType.all);
					emc.commit();

					dateRecordExsist = false;
					for (DateRecord dateRecord : dateRecordList) {
						if (dateRecord.getYear().equals(cacheImportRowDetail.getRecordYearString())
								&& dateRecord.getMonth().equals(cacheImportRowDetail.getRecordMonthString())) {
							// 说明已经存在了
							dateRecordExsist = true;
						}
					}
					if (!dateRecordExsist) {
						dateRecordList.add(new DateRecord(cacheImportRowDetail.getRecordYearString(),
								cacheImportRowDetail.getRecordMonthString()));
					}
					logger.info("保存[" + cacheImportRowDetail.getEmployeeName() + "]["
							+ cacheImportRowDetail.getRecordDateStringFormated() + "]新的打卡数据......");
				}

				if (dateRecordList != null) {
					for (DateRecord dateRecord : dateRecordList) {
						logger.info("需要进行统计的年月：" + dateRecord.getYear() + "-" + dateRecord.getMonth());
					}
				}
				result.setData(dateRecordList);
				logger.info("数据处理事务提交完成！");
			} catch (Exception e) {
				Exception exception = new ExceptionFileImportProcess(e, "数据导入时发生未知异常.ID:" + file_id );
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
			StatusSystemImportOpt.getInstance().removeCacheImportFileStatus( file_id );
		} else {
			Exception exception = new ExceptionDataCacheNotExists( file_id );
			result.error(exception);
		}
		return result;
	}
}