package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;

public class ActionProcessing extends ActionBase {

	protected ActionResult<WrapOutId> execute(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			Read read = emc.find(id, Read.class, ExceptionWhen.not_found);
			ReadCompleted readCompleted = new ReadCompleted();
			read.copyTo(readCompleted);
			readCompleted.setCompletedTime(new Date());
			readCompleted
					.setCompletedTimeMonth(DateTools.format(readCompleted.getCompletedTime(), DateTools.format_yyyyMM));
			readCompleted.setRead(read.getId());
			/* 设置duration工作时长 */
			long duration = Config.workTime().betweenMinutes(readCompleted.getStartTime(),
					readCompleted.getCompletedTime());
			readCompleted.setDuration(duration);
			emc.persist(readCompleted, CheckPersistType.all);
			emc.remove(read, CheckRemoveType.all);
			/* 删除这个人当前的其他待阅 */
			this.removeOtherRead(business, read);
			emc.commit();
			result.setData(new WrapOutId(read.getId()));
			return result;
		}
	}

	private void removeOtherRead(Business business, Read read) throws Exception {
		List<String> ids = new ArrayList<>();
		EntityManagerContainer emc = business.entityManagerContainer();
		if (read.getCompleted()) {
			ids = business.read().listWithPersonWithWorkCompleted(read.getPerson(), read.getWorkCompleted());
		} else {
			ids = business.read().listWithPersonWithWork(read.getPerson(), read.getWork());
		}
		for (String str : ids) {
			if (!StringUtils.equals(read.getId(), str)) {
				Read o = emc.find(str, Read.class);
				if (null != o) {
					emc.remove(o);
				}
			}
		}
	}

}
