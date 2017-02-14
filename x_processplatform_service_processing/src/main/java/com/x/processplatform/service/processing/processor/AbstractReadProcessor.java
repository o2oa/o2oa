package com.x.processplatform.service.processing.processor;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.utils.DateTools;
import com.x.collaboration.core.message.Collaboration;
import com.x.collaboration.core.message.notification.ReadMessage;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;

public abstract class AbstractReadProcessor extends AbstractReviewProcessor {

	protected AbstractReadProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	protected void createRead(String identity, Work work) throws Exception {
		String person = this.business().organization().person().getWithIdentity(identity).getName();
		if (StringUtils.isNotEmpty(person)) {
			Read read = this.create(this.business(), work, identity);
			this.entityManagerContainer().beginTransaction(Read.class);
			this.entityManagerContainer().persist(read, CheckPersistType.all);
			sendReadMessage(read);
		}
	}

	private void sendReadMessage(Read read) {
		try {
			ReadMessage message = new ReadMessage(read.getPerson(), read.getWork(), read.getId());
			Collaboration.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Read create(Business business, Work work, String identity) throws Exception {
		Read read = new Read();
		read.setViewed(false);
		read.setTitle(work.getTitle());
		read.setActivity(work.getActivity());
		read.setActivityName(work.getActivityName());
		read.setActivityType(work.getActivityType());
		read.setActivityToken(work.getActivityToken());
		read.setApplication(work.getApplication());
		read.setApplicationName(work.getApplicationName());
		read.setProcess(work.getProcess());
		read.setProcessName(work.getProcessName());
		read.setJob(work.getJob());
		read.setStartTime(new Date());
		read.setStartTimeMonth(DateTools.format(read.getStartTime(), DateTools.format_yyyyMM));
		read.setWork(work.getId());
		read.setCompleted(false);
		read.setIdentity(identity);
		read.setPerson(business.organization().person().getWithIdentity(identity).getName());
		read.setDepartment(business.organization().department().getWithIdentity(identity).getName());
		read.setCompany(business.organization().company().getWithIdentity(identity).getName());
		return read;
	}
}