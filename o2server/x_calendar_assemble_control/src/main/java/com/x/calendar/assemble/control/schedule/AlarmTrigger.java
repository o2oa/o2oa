package com.x.calendar.assemble.control.schedule;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.assemble.control.factory.MessageFactory;
import com.x.calendar.assemble.control.service.Calendar_EventServiceAdv;
import com.x.calendar.core.entity.Calendar_Event;

/**
 * 查询需要提醒的日程或者事件，并且按要求发送提醒消息
 * 
 * @author O2LEE
 *
 */
public class AlarmTrigger extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(AlarmTrigger.class);
	protected Calendar_EventServiceAdv calendar_EventServiceAdv = new Calendar_EventServiceAdv();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			alarm(business);
			logger.info("The trigger for calendar alarm execute completed." + new Date());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	/**
	 * 执行日程事件提醒 1、查询需要提醒的日程事件ID列表 2、发送消息
	 * 
	 * @param business
	 * @return
	 * @throws Exception
	 */
	private boolean alarm(Business business) throws Exception {
		// 1、查询需要提醒的日程事件ID列表 当前时间已经到达或者超过提醒时间，并且提醒标识为未提醒（false）
		List<String> ids = null;
		Calendar_Event calendar_Event = null;
		try {
			ids = calendar_EventServiceAdv.listNeedAlarmEventIds(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ListTools.isNotEmpty(ids)) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				for (String id : ids) {
					calendar_Event = calendar_EventServiceAdv.get(id);
					if (calendar_Event != null) {
						MessageFactory.send_alarm(emc, calendar_Event);
						logger.info("send message:{}.", calendar_Event.getTitle());
					}
				}
			} catch (Exception e) {
				throw e;
			}
			logger.info("The trigger sent " + ids.size() + " calendar alarms.");
		}
		return false;
	}

}
