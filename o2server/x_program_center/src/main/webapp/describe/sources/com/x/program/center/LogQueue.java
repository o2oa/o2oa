package com.x.program.center;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class LogQueue extends AbstractQueue<NameValuePair> {

	protected void execute(NameValuePair pair) {
		try {
			if (StringUtils.equals(pair.getName(), PromptErrorLog.class.getName())) {
				this.concretePromptErrorLog(pair.getValue(PromptErrorLog.class));
			} else if (StringUtils.equals(pair.getName(), UnexpectedErrorLog.class.getName())) {
				this.concreteUnexpectedErrorLog(pair.getValue(UnexpectedErrorLog.class));
			} else if (StringUtils.equals(pair.getName(), WarnLog.class.getName())) {
				this.concreteWarnLog(pair.getValue(WarnLog.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void concretePromptErrorLog(PromptErrorLog o) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(PromptErrorLog.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
		}
	}

	private void concreteUnexpectedErrorLog(UnexpectedErrorLog o) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(UnexpectedErrorLog.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
		}
	}

	private void concreteWarnLog(WarnLog o) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(WarnLog.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
		}
	}

}
