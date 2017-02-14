package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;

public class ActionBase {

	protected WrapOutId processing(Business business, String id, ProcessingType type) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Read read = emc.find(id, Read.class, ExceptionWhen.not_found);
		List<String> ids = new ArrayList<>();
		if (read.getCompleted()) {
			ids = business.read().listWithPersonWithWorkCompleted(read.getPerson(), read.getWorkCompleted());
		} else {
			ids = business.read().listWithPersonWithWork(read.getPerson(), read.getWork());
		}
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		ReadCompleted readCompleted = this.createReadCompleted(business, read, type);
		emc.persist(readCompleted, CheckPersistType.all);
		emc.delete(Read.class, ids);
		emc.commit();
		WrapOutId wrap = new WrapOutId(id);
		return wrap;
	}

	protected ReadCompleted createReadCompleted(Business business, Read read, ProcessingType type) throws Exception {
		ReadCompleted readCompleted = new ReadCompleted();
		read.copyTo(readCompleted);
		readCompleted.setProcessingType(type);
		readCompleted.setProcessingType(ProcessingType.processing);
		Date date = new Date();
		readCompleted.setCompletedTime(date);
		readCompleted.setCompletedTimeMonth(DateTools.format(date, DateTools.format_yyyyMM));
		readCompleted.setRead(read.getId());
		readCompleted.setDuration(
				business.workTime().betweenMinutes(readCompleted.getStartTime(), readCompleted.getCompletedTime()));
		return readCompleted;
	}

}