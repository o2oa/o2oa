package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseServiceAdv {
	private AttendanceDetailAnalyseService detailAnalyseService = new AttendanceDetailAnalyseService();

	/**
	 * 根据员工姓名\开始日期\结束日期查询日期范围内所有的打卡记录信息ID列表<br/>
	 * 时间从开始日期0点，到结束日期23点59分
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> listAnalyseAttendanceDetailIds( String empName, Date startDate, Date endDate, Boolean debugger )throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.listAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对一组的打卡信息数据进行分析
	 * @param detailIds
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetailWithIds( List<String> detailIds, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetailWithIds( emc, detailIds, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对一组的打卡信息数据进行分析
	 * @param detailList
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( List<AttendanceDetail> detailList, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, detailList, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对一组的打卡信息数据进行分析
	 * @param detailList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, detailList, statisticalCycleMap, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据员工姓名，开始日期和结束日期重新分析所有的打卡记录，一般在用户补了休假记录的时候使用，补充了休假记录，需要将休假日期范围内的所有打卡记录重新分析，并且触发相关月份的统计
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetails( String empName, Date startDate, Date endDate, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, empName, startDate, endDate, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对单条的打卡数据进行分析
	 * @param detailId
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( String detailId, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detailId, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对单条的打卡数据进行分析
	 * @param detail
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( AttendanceDetail detail, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detail, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对单条的打卡数据进行详细分析
	 * @param detail
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( AttendanceDetail detail, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
