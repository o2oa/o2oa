package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class OkrWorkBaseInfoQueryService {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkBaseInfoQueryService.class);
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 根据指定的ID从数据库查询OkrWorkBaseInfo对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkBaseInfo get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, OkrWorkBaseInfo.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据指定的ID列表查询具体工作信息列表
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return null;
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().list(ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 权限校验，判断用户是否有权限拆解工作
	 * 
	 * @param workId
	 *            -- 被拆解工作的ID
	 * @param userName
	 *            -- 操作的用户姓名
	 * @return
	 * @throws Exception
	 */
	public boolean canDismantlingWorkByIdentity(String workId, String userIdentity) throws Exception {
		/**
		 * 1、判断用户是否是该工作的责任者，责任者可以进行工作拆解
		 */
		// 先根据工作的ID，用户的姓名，身份（责任者），查询工作的干系人信息，如果有，则可以进行部署
		Business business = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add("正常");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkPersonFactory().listByWorkAndIdentity(null, workId, userIdentity, "责任者", statuses);
		} catch (Exception e) {
			throw e;
		}
		if (ids != null && ids.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listNextWithFilter(String id, Integer count, WrapInFilter wrapIn) throws Exception {
		Business business = null;
		Object sequence = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty(emc.find(id, OkrWorkBaseInfo.class),  JpaObject.sequence_FIELDNAME);
				}
			}
			return business.okrWorkBaseInfoFactory().listNextWithFilter(id, count, sequence, wrapIn);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listPrevWithFilter(String id, Integer count, WrapInFilter wrapIn) throws Exception {
		Business business = null;
		Object sequence = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty(emc.find(id, OkrWorkBaseInfo.class),  JpaObject.sequence_FIELDNAME);
				}
			}
			return business.okrWorkBaseInfoFactory().listPrevWithFilter(id, count, sequence, wrapIn);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询符合条件的数据总数
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getCountWithFilter(WrapInFilter wrapIn) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getCountWithFilter(wrapIn);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据部署日期，完成时限，汇报周期，汇报日期计算在工作执行期间所有的汇报日期列表
	 * 
	 * @param deployDateStr
	 * @param completeDateLimitStr
	 * @param reportCycle
	 * @param reportDayInCycle
	 * @return
	 * @throws Exception
	 */
	public String getReportTimeQue(Date deployDate, Date completeDateLimit, String reportCycle,
			Integer reportDayInCycle, String createTime) throws Exception {
		List<String> dateStringList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		Date _tmp_date = null;
		if (reportCycle != null && reportCycle.trim().equals("每月汇报")) {
			int reportDay = 0;
			int dayMaxNumber = 0;
			calendar.setTime(deployDate);
			_tmp_date = calendar.getTime();
			do {
				dayMaxNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (dayMaxNumber < reportDayInCycle) {
					reportDay = dayMaxNumber;
				} else {
					reportDay = reportDayInCycle;
				}
				calendar.set(Calendar.DAY_OF_MONTH, reportDay);
				// 判断是否周末, 汇报会跳过周末，顺延到下一个工作日
				while (dateOperation.isWeekend(calendar.getTime())) {
					calendar.add(Calendar.DATE, 1);
				}

				if (calendar.getTime().after(deployDate)) {
					dateStringList.add(
							dateOperation.getDateStringFromDate(calendar.getTime(), "yyyy-MM-dd") + " " + createTime);
				}

				// 判断是否节假日
				calendar.add(Calendar.MONTH, 1);
				_tmp_date = calendar.getTime();
			} while (_tmp_date.before(completeDateLimit));
		} else if (reportCycle != null && reportCycle.trim().equals("每周汇报")) {
			int reportDay = 0;
			int dayMaxNumber = 7; // 1-SUNDAY, 2-MONDAY, 3-TUESDAY, 4-WENDSDAY, 5-THURSDAY, 6-FRIDAY, 7-SATURDAY
			calendar.setTime(deployDate);
			_tmp_date = calendar.getTime();
			do {
				if (dayMaxNumber < reportDayInCycle) {
					reportDay = dayMaxNumber;
				} else {
					reportDay = reportDayInCycle;
				}
				calendar.set(Calendar.DAY_OF_WEEK, reportDay);

				// 判断是否周末, 汇报会跳过周末，顺延到下一个工作日
				while (dateOperation.isWeekend(calendar.getTime())) {
					calendar.add(Calendar.DATE, 1);
				}

				if (calendar.getTime().after(deployDate)) {
					dateStringList.add(
							dateOperation.getDateStringFromDate(calendar.getTime(), "yyyy-MM-dd") + " " + createTime);
				}

				// 判断是否节假日
				calendar.add(Calendar.WEEK_OF_YEAR, 1);
				_tmp_date = calendar.getTime();

			} while (_tmp_date.before(completeDateLimit));
		}
		
		if (dateStringList != null && dateStringList.size() > 0) {
			for (String dateString : dateStringList) {
				if (sb.toString().trim().length() > 0) {
					sb.append(";" + dateString);
				} else {
					sb.append(dateString);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 根据定期汇报时间序列和上一次汇报时间来获取下一次汇报时间
	 * 
	 * @param reportTimeQue
	 * @param lastReportTime
	 * @return
	 * @throws Exception
	 */
	public Date getNextReportTime(String reportTimeQue, Date lastReportTime) throws Exception {
		String[] timeArray = null;
		if (reportTimeQue != null && reportTimeQue.trim().length() > 0) {
			timeArray = reportTimeQue.split(";");
			if (timeArray != null && timeArray.length > 0) {
				for (String reportTime : timeArray) {
					// 在现在之后，并且在上一次汇报时间之后
					if (dateOperation.getDateFromString(reportTime).after(new Date())) {
						if (lastReportTime == null) {
							return dateOperation.getDateFromString(reportTime);
						} else {
							if (dateOperation.getDateFromString(reportTime).after(lastReportTime)) {
								return dateOperation.getDateFromString(reportTime);
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据用户名称和中心工作ID列示所有工作信息
	 * 
	 * @param centerId
	 * @param statuses
	 *            需要显示的信息状态：正常|已删除
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkInCenter(String centerId, List<String> statuses) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("centerId is null.");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listWorkByCenterId(centerId, null, statuses);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据用户名称和中心工作ID列示所有与用户有关的工作信息
	 * 
	 * @param userIdentity
	 * @param centerId
	 * @param statuses
	 *            需要显示的信息状态：正常|已删除
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkInCenterByIdentity(String userIdentity, String centerId, List<String> statuses)
			throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("centerId is null.");
		}
		Business business = null;
		List<String> ids = okrWorkPersonService.listDistinctWorkIdsByIdentity(userIdentity, centerId, statuses);
		if (ids != null && !ids.isEmpty()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				return business.okrWorkBaseInfoFactory().list(ids);
			} catch (Exception e) {
				throw e;
			}
		}
		return null;
	}

	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkNextWithFilter(String id, Integer count, WorkCommonSearchFilter wrapIn)
			throws Exception {
		Business business = null;
		Object sequence = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if (wrapIn == null) {
			throw new Exception("wrapIn is null!");
		}
		wrapIn.setInfoType("WORK");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				okrWorkPersonList = business.okrWorkPersonFactory().listDetailWorkPerson(id, wrapIn);
				if (okrWorkPersonList != null && !okrWorkPersonList.isEmpty()) {
					sequence = okrWorkPersonList.get(0).getSequence();
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listNextWithFilter(id, count, sequence, wrapIn);
			if (okrWorkPersonList != null && !okrWorkPersonList.isEmpty()) {
				for (OkrWorkPerson okrWorkPerson : okrWorkPersonList) {
					okrWorkBaseInfo = emc.find(okrWorkPerson.getWorkId(), OkrWorkBaseInfo.class);
					if (okrWorkBaseInfo != null && !okrWorkBaseInfoList.contains(okrWorkBaseInfo)) {
						okrWorkBaseInfoList.add(okrWorkBaseInfo);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return okrWorkBaseInfoList;
	}

	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkPrevWithFilter(String id, Integer count, WorkCommonSearchFilter wrapIn)
			throws Exception {
		Business business = null;
		Object sequence = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if (wrapIn == null) {
			throw new Exception("wrapIn is null!");
		}
		wrapIn.setInfoType("WORK");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				okrWorkPersonList = business.okrWorkPersonFactory().listDetailWorkPerson(id, wrapIn);
				if (okrWorkPersonList != null && !okrWorkPersonList.isEmpty()) {
					sequence = okrWorkPersonList.get(0).getSequence();
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listPrevWithFilter(id, count, sequence, wrapIn);
			if (okrWorkPersonList != null && !okrWorkPersonList.isEmpty()) {
				for (OkrWorkPerson okrWorkPerson : okrWorkPersonList) {
					okrWorkBaseInfo = emc.find(okrWorkPerson.getCenterId(), OkrWorkBaseInfo.class);
					if (okrWorkBaseInfo != null && !okrWorkBaseInfoList.contains(okrWorkBaseInfo)) {
						okrWorkBaseInfoList.add(okrWorkBaseInfo);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return okrWorkBaseInfoList;
	}

	/**
	 * 查询符合条件的数据总数
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getWorkCountWithFilter(WorkCommonSearchFilter wrapIn) throws Exception {
		Business business = null;
		if (wrapIn == null) {
			throw new Exception("wrapIn is null!");
		}
		wrapIn.setInfoType("WORK");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getCountWithFilter(wrapIn);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询需要立即进行汇报的工作ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedReportWorkIds() throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listNeedReportWorkIds();
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listByParentId(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("centerId is null.");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listByParentId(id);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据分析时间来查询需要进行进展分析的工作ID列表
	 * 
	 * @param report_progress
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsForNeedProgressAnalyse(String report_progress, int count) throws Exception {
		if (report_progress == null || report_progress.isEmpty()) {
			throw new Exception("report_progress is null.");
		}
		if (count == 0) {
			throw new Exception("count is 0.");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listIdsForNeedProgressAnalyse(report_progress, count);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据指定的工作ID，进度汇报设置和进度分析时间来进行工作分析，并且更新进度、完成情况以及分析时间信息
	 * 
	 * @param id
	 * @param okrWorkReportBaseInfo 
	 * @param report_progress
	 * @param analyse_time_flag
	 * @throws Exception
	 */
	public void analyseWorkProgress(String id, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String report_progress, String nowDateTime) throws Exception {
		if ("OPEN".equals(report_progress.toUpperCase())) {
			// 汇报时需要进行工作进度和是否已完成的汇报。
			analyseWorkProgressFromReports(id, okrWorkReportBaseInfo, nowDateTime);
		} else {
			// 根据工作的部署时间，完成时限和当前时间来进行工作完成度的计算
			analyseWorkProgressFromProcessTimeLimit(id, nowDateTime);
		}
	}

	/**
	 * 汇报时需要进行工作进度和是否已完成的汇报。
	 * 
	 * @param workId
	 * @param okrWorkReportBaseInfo 
	 * @param analyse_time_flag
	 * @throws Exception
	 */
	private void analyseWorkProgressFromReports(String workId, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String analyse_time_flag) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null.");
		}
		if (analyse_time_flag == null || analyse_time_flag.isEmpty()) {
			throw new Exception("analyse_time_flag is null.");
		}
		// 取到该工作最后一次，并且已经提交的汇报的内容
		// 根据汇报内容来确定该工作的进度情况。
		Business business = null;
		Date nextReportTime = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add("正常");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			emc.beginTransaction(OkrWorkBaseInfo.class);
			emc.beginTransaction(OkrWorkPerson.class);
			emc.beginTransaction(OkrWorkReportBaseInfo.class);

			// 查询工作对象
			okrWorkBaseInfo = emc.find(workId, OkrWorkBaseInfo.class);

			if (okrWorkReportBaseInfo == null && okrWorkBaseInfo != null) {
				okrWorkReportBaseInfo = business.okrWorkReportBaseInfoFactory().getLastCompletedReport(workId);
			}
			if (okrWorkReportBaseInfo != null) {
				okrWorkBaseInfo.setIsCompleted(okrWorkReportBaseInfo.getIsWorkCompleted());
				if (okrWorkReportBaseInfo.getProgressPercent()>=100 ||  okrWorkReportBaseInfo.getIsWorkCompleted()) {
					// 已经完成
					okrWorkBaseInfo.setCompleteTime(new Date());
					okrWorkBaseInfo.setIsCompleted(true);
					okrWorkBaseInfo.setOverallProgress(100);

					// 修改所有干系人信息状态，从执行中修改为已完成，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId(workId, statuses);
					if (ids != null && !ids.isEmpty()) {
						for (String id : ids) {
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);
							okrWorkPerson.setWorkProcessStatus("已完成");
							okrWorkPerson.setIsCompleted(true);
							emc.check(okrWorkPerson, CheckPersistType.all);
						}
					}

					// 不需要进行汇报了，把工作的下次汇报时间设置为空
					okrWorkBaseInfo.setNextReportTime(null);
				} else {
					okrWorkBaseInfo.setIsCompleted(false);
					okrWorkBaseInfo.setOverallProgress(okrWorkReportBaseInfo.getProgressPercent());

					// 判断是否已经超时
					if (okrWorkBaseInfo.getCompleteDateLimit().before(new Date())) {
						okrWorkBaseInfo.setIsOverTime(true);

						// 修改所有干系人信息状态，从执行中修改为已超时，已删除的不要修改
						ids = business.okrWorkPersonFactory().listByWorkId(workId, statuses);
						if (ids != null && !ids.isEmpty()) {
							for (String id : ids) {
								okrWorkPerson = emc.find(id, OkrWorkPerson.class);
								okrWorkPerson.setWorkProcessStatus("执行中");
								okrWorkPerson.setIsCompleted(false);
								okrWorkPerson.setIsOverTime(true);
								emc.check(okrWorkPerson, CheckPersistType.all);
							}
						}
						// TODO:如果下一次汇报时间为空，还得分析一下下一次的汇报时间
						if (okrWorkBaseInfo.getNextReportTime() == null) {
							nextReportTime = getNextReportTime(okrWorkBaseInfo);
							okrWorkBaseInfo.setNextReportTime(nextReportTime);
						}
					}
				}
			} else {
				// 还没有开始汇报
				okrWorkBaseInfo.setIsCompleted(false);
				okrWorkBaseInfo.setOverallProgress(0);

				// 判断是否已经超时
				if (okrWorkBaseInfo.getCompleteDateLimit().before(new Date())) {
					okrWorkBaseInfo.setIsOverTime(true);
					// 修改所有干系人信息状态，从执行中修改为已超时，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId(workId, statuses);
					if (ids != null && !ids.isEmpty()) {
						for (String id : ids) {
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);
							okrWorkPerson.setWorkProcessStatus("执行中");
							okrWorkPerson.setIsCompleted(false);
							okrWorkPerson.setIsOverTime(true);
							emc.check(okrWorkPerson, CheckPersistType.all);
						}
					}
				}

				// TODO:如果下一次汇报时间为空，还得分析一下下一次的汇报时间
				if (okrWorkBaseInfo.getNextReportTime() == null) {
					nextReportTime = getNextReportTime(okrWorkBaseInfo);
					okrWorkBaseInfo.setNextReportTime(nextReportTime);
				}
			}

			if (okrWorkBaseInfo != null) {
				okrWorkBaseInfo.setProgressAnalyseTime(analyse_time_flag);
				emc.check(okrWorkBaseInfo, CheckPersistType.all);
			}
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据工作信息分析工作的下次汇报时间是否正常
	 * 
	 * @param okrWorkBaseInfo
	 * @return
	 * @throws Exception
	 */
	public Date getNextReportTime(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (!okrWorkBaseInfo.getIsCompleted()) {
			String reportStartTime = "10:00:00";
			Calendar calendar = Calendar.getInstance();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				reportStartTime = business.okrConfigSystemFactory().getValueWithConfigCode("REPORT_CREATETIME");
			} catch (Exception e) {
				reportStartTime = "10:00:00";
			}
			if (reportStartTime == null || reportStartTime.isEmpty()) {
				reportStartTime = "10:00:00";
			}
			// 如果下次汇报的时间不为空，并且下一次汇报时间有效，那么直接返回
			if (okrWorkBaseInfo.getNextReportTime() != null) {
				calendar.setTime(dateOperation.getDateFromString(
						dateOperation.getDateStringFromDate(okrWorkBaseInfo.getNextReportTime(), "yyyy-MM-dd") + " "
								+ reportStartTime));
				okrWorkBaseInfo.setNextReportTime(calendar.getTime());
				if (okrWorkBaseInfo.getLastReportTime() == null) {
					if (okrWorkBaseInfo.getNextReportTime().after(new Date())) {
						return okrWorkBaseInfo.getNextReportTime();
					}
				} else {
					if (okrWorkBaseInfo.getNextReportTime().after(okrWorkBaseInfo.getLastReportTime())) {
						return okrWorkBaseInfo.getNextReportTime();
					}
				}
			}
			if (okrWorkBaseInfo.getLastReportTime() == null) {
				// 如果上次汇报时间为空，也就是说没有进行过任何汇报
				// 根据工作的汇报周期配置来计算离今天最近的后一次汇报时间
				return calculateNextCycleTime(reportStartTime, okrWorkBaseInfo.getReportCycle(),
						okrWorkBaseInfo.getReportDayInCycle(), new Date());
			} else {
				// 根据上一次汇报时间来计算下一次汇报时间
				return calculateNextCycleTime(reportStartTime, okrWorkBaseInfo.getReportCycle(),
						okrWorkBaseInfo.getReportDayInCycle(), okrWorkBaseInfo.getLastReportTime());
			}
		}
		return null;
	}

	/**
	 * TODO:根据周期方式，以后周期时间点，和开始时间来计算下一个周期时间点
	 * 
	 * @param reportCycle
	 * @param reportDayInCycle
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private Date calculateNextCycleTime(String reportStartTime, String reportCycle, Integer reportDayInCycle,
			Date lastReportDate) throws Exception {
		int reportDay = 0;
		int dayMaxNumber = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateOperation.getDateFromString(
				dateOperation.getDateStringFromDate(lastReportDate, "yyyy-MM-dd") + " " + reportStartTime));
		if (reportCycle != null && reportCycle.trim().equals("每月汇报")) {
			dayMaxNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (dayMaxNumber < reportDayInCycle) {
				reportDay = dayMaxNumber;
			} else {
				reportDay = reportDayInCycle;
			}
			calendar.set(Calendar.DAY_OF_MONTH, reportDay);
			// 如果本月汇报时间已经过了，那么下月再汇报
			System.out.println(calendar.getTime() + ".before(" + lastReportDate + "):"
					+ calendar.getTime().before(lastReportDate));
			while (calendar.getTime().before(lastReportDate)) {
				calendar.set(Calendar.DAY_OF_MONTH, reportDay);
				calendar.add(Calendar.MONTH, 1);
			}
		} else if (reportCycle != null && reportCycle.trim().equals("每周汇报")) {
			dayMaxNumber = 7;
			if (dayMaxNumber < reportDayInCycle) {
				reportDay = dayMaxNumber;
			} else {
				reportDay = reportDayInCycle;
			}
			calendar.set(Calendar.DAY_OF_WEEK, reportDay);
			// 如果本周汇报时间已经过了，那么下周再汇报
			System.out.println(calendar.getTime() + ".before(" + lastReportDate + "):"
					+ calendar.getTime().before(lastReportDate));
			while (calendar.getTime().before(lastReportDate)) {
				calendar.set(Calendar.DAY_OF_WEEK, reportDay);
				calendar.add(Calendar.WEEK_OF_YEAR, 1);
			}
		}
		// 判断是否周末
		while (dateOperation.isWeekend(calendar.getTime())) {
			calendar.add(Calendar.DATE, 1);
		}
		return dateOperation.getDateFromString(
				dateOperation.getDateStringFromDate(calendar.getTime(), "yyyy-MM-dd") + " " + reportStartTime);
	}

	/**
	 * 根据工作的部署时间，完成时限和当前时间来进行工作完成度的计算
	 * 
	 * @param workId
	 * @param analyse_time_flag
	 * @throws Exception
	 */
	private void analyseWorkProgressFromProcessTimeLimit(String workId, String analyse_time_flag) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null.");
		}
		if (analyse_time_flag == null || analyse_time_flag.isEmpty()) {
			throw new Exception("analyse_time_flag is null.");
		}
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		Business business = null;
		Integer completePercent = 0;
		String deployDateString = null;
		Date startDateTime = null, processDateLimit = null, nowDate = new Date();
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add("正常");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 查询工作对象
			okrWorkBaseInfo = emc.find(workId, OkrWorkBaseInfo.class);
			if (okrWorkBaseInfo != null) {
				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrWorkPerson.class);

				processDateLimit = okrWorkBaseInfo.getCompleteDateLimit();
				if (processDateLimit == null) {
					throw new Exception(
							"work process date limit is null, system can not analyse work progress from process time limit.");
				}

				deployDateString = okrWorkBaseInfo.getDeployDateStr();
				if (deployDateString == null || deployDateString.isEmpty()) {
					throw new Exception(
							"work deploy date string is null, system can not analyse work progress from process time limit.");
				}

				try {
					startDateTime = dateOperation.getDateFromString(deployDateString);
				} catch (Exception e) {
					logger.warn("work deploy date string is not date style[deployDateString=" + deployDateString
							+ "], system can not analyse work progress from process time limit.");
					throw e;
				}

				// 根据部署时间，当前时间和结束时间进行进度计算
				if (processDateLimit.before(nowDate)) {
					// 处理时间已经耗尽，工作已经完成
					okrWorkBaseInfo.setCompleteTime(new Date());
					okrWorkBaseInfo.setIsCompleted(true);
					okrWorkBaseInfo.setOverallProgress(100);
					okrWorkBaseInfo.setNextReportTime(null);
					// 修改所有干系人信息状态，从执行中修改为已完成，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId(workId, statuses);
					if (ids != null && !ids.isEmpty()) {
						for (String id : ids) {
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);
							okrWorkPerson.setWorkProcessStatus("已完成");
							okrWorkPerson.setIsCompleted(true);
							emc.check(okrWorkPerson, CheckPersistType.all);
						}
					}
				} else {
					// 计算完成百分比
					long usedTime = nowDate.getTime() - startDateTime.getTime();
					long fullTime = processDateLimit.getTime() - startDateTime.getTime();
					if (fullTime > usedTime) {
						completePercent = Integer.parseInt(((usedTime * 100) / fullTime) + "");
					} else {
						completePercent = 0;
					}
					okrWorkBaseInfo.setIsCompleted(false);
					okrWorkBaseInfo.setOverallProgress(completePercent);
				}

				if (okrWorkBaseInfo != null) {
					okrWorkBaseInfo.setProgressAnalyseTime(analyse_time_flag);
					emc.check(okrWorkBaseInfo, CheckPersistType.all);
				}
			}
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getWorkTotalByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getWorkTotalByCenterId(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getProcessingWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getProcessingWorkCountByCenterId(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getCompletedWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getCompletedWorkCountByCenterId(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getOvertimeWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getOvertimeWorkCountByCenterId(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getDraftWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getDraftWorkCountByCenterId(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 判断用户是否存在未提交的汇报数据
	 * 
	 * @param workId
	 * @param activityName
	 * @param processStatus
	 * @param processIdentity
	 * @return
	 * @throws Exception
	 */
	public Boolean hasNoneSubmitReport(String workId, String activityName, String processStatus, String processIdentity)
			throws Exception {
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkReportBaseInfoFactory().listByWorkId(workId, activityName, processStatus,
					processIdentity);
			if (ids != null && !ids.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询所有未完成工作列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listAllProcessingWorks() throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listAllProcessingWorks();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询所有未完成工作列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllProcessingWorkIds() throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listAllProcessingWorkIds();
		} catch (Exception e) {
			throw e;
		}
	}

	public List<OkrWorkBaseInfo> listAllDeployedWorks(String centerId, String status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listAllDeployedWorks(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listAllDeployedWorkIds(String centerId, String status) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listAllDeployedWorkIds(centerId, status);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据工作ID，获取指定工作的所有下级工作ID列表
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<String> getSubNormalWorkBaseInfoIds(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, return null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getSubNormalWorkBaseInfoIds(workId);
		} catch (Exception e) {
			throw e;
		}
	}

	public Boolean hasSubWork(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, return null!");
		}
		List<String> ids = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkBaseInfoFactory().getSubNormalWorkBaseInfoIds(workId);
			if (ids != null && !ids.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 为工作绑定考核流程ID
	 * @param workId
	 * @param wf_workId
	 * @return
	 * @throws Exception
	 */
	public Boolean bindAppraiseWfId(String workId, String wf_workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, return null!");
		}
		if (wf_workId == null || wf_workId.isEmpty()) {
			throw new Exception("wf_workId is null, return null!");
		}
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo != null ) {
				emc.beginTransaction( OkrWorkBaseInfo.class );
				okrWorkBaseInfo.setCurrentAppraiseWorkId(wf_workId);
				okrWorkBaseInfo.setCurrentAppraiseInfoId("");
				okrWorkBaseInfo.setCurrentActivityName("拟稿");
				okrWorkBaseInfo.setCurrentAppraiseStatus("审核中");
				if(okrWorkBaseInfo.getAppraiseTimes() == null) {
					okrWorkBaseInfo.setAppraiseTimes(1);
				}else {
					okrWorkBaseInfo.setAppraiseTimes( okrWorkBaseInfo.getAppraiseTimes() + 1 );
				}
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public OkrAttachmentFileInfo saveAttachment(String workId, OkrAttachmentFileInfo attachment) throws Exception {
		if( workId == null ){
			throw new Exception( "workId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkBaseInfo work = emc.find( workId, OkrWorkBaseInfo.class );
			if( work != null ){
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				emc.beginTransaction( OkrWorkBaseInfo.class );
				emc.persist(attachment, CheckPersistType.all );
				if( work.getAttachmentList()== null  ) {
					work.setAttachmentList( new ArrayList<>());
				}
				if( !work.getAttachmentList().contains( attachment.getId() )) {
					work.getAttachmentList().add( attachment.getId() );
					emc.check( work, CheckPersistType.all );
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return attachment;
	}
}