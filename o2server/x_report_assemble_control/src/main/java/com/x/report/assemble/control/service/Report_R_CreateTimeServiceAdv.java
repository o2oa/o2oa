package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.report.core.entity.Report_R_CreateTime;

/**
 * 记录所有类别的汇报上一次和下一次生成时间的信息服务类
 * @author O2LEE
 *
 */
public class Report_R_CreateTimeServiceAdv{

	private Report_R_CreateTimeService report_R_CreateTimeService = new Report_R_CreateTimeService();

	/**
	 * 根据指定的ID获取汇报生成时间记录信息
	 * @param emc
	 * @param ids 汇报生成时间记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public Report_R_CreateTime get( String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_R_CreateTimeService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取上一次月报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getLastMonthReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastMonthReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取上一次日周报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getLastWeekReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastWeekReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取上一次日报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getLastDayReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastDayReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取上一次月报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public String getLastMonthReportId() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastMonthReportId();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取上一次日周报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public String getLastWeekReportTimeId() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastWeekReportId();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取上一次日报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public String getLastDayReportId() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getLastDayReportId();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取下一次月报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getNextMonthReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getNextMonthReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取下一次日周报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getNextWeekReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getNextWeekReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 获取下一次日报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Date getNextDayReportTime() throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				return report_R_CreateTime.getNextDayReportTime();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 设置上一次月报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastMonthReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastMonthReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastMonthReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置上一次日周报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastWeekReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastWeekReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastWeekReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置上一次日报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastDayReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastDayReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastDayReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置上一次月报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastMonthReportId(String id) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastMonthReportId( id );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastMonthReportId( id );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置上一次日周报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastWeekReportId(String id) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastWeekReportId( id );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastWeekReportId( id );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置上一次日报生成信息ID
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setLastDayReportId( String id ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastDayReportId( id );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setLastDayReportId( id );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置下一次月报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setNextMonthReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextMonthReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextMonthReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置下一次日周报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setNextWeekReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextWeekReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextWeekReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置下一次日报生成时间
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public Boolean setNextDayReportTime( Date date ) throws Exception {
		List<Report_R_CreateTime> createTimeList = null;
		Report_R_CreateTime report_R_CreateTime = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			createTimeList = report_R_CreateTimeService.listAll( emc );
			if( createTimeList != null && !createTimeList.isEmpty() ) {
				report_R_CreateTime = createTimeList.get(0);
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextDayReportTime( date );
				emc.check( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}else {
				//没有，需要新建一条信息
				report_R_CreateTime = new Report_R_CreateTime();
				emc.beginTransaction( Report_R_CreateTime.class );
				report_R_CreateTime.setNextDayReportTime( date );
				emc.persist( report_R_CreateTime, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
