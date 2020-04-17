package com.x.component.assemble.control.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class InitComponents extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(InitComponents.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Component.class);
			this.init(emc);
			emc.commit();
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void init(EntityManagerContainer emc) throws Exception {
		List<String> names = ListTools.extractProperty(Config.components().getSystems(), "name", String.class, true,
				true);
		List<Component> os = emc.listEqual(Component.class, Component.type_FIELDNAME, Component.TYPE_SYSTEM);
		for (Component o : os) {
			names.remove(o.getName());
		}
		for (com.x.base.core.project.config.Components.Component o : Config.components().getSystems()) {
			if (names.contains(o.getName())) {
				Component component = new Component();
				component.setName(o.getName());
				component.setPath(o.getPath());
				component.setTitle(o.getTitle());
				component.setIconPath(o.getIconPath());
				component.setOrder(o.getOrder());
				component.setVisible(true);
				emc.persist(component, CheckPersistType.all);
			}
		}
		Iterator<Component> iterator = os.iterator();
		while (iterator.hasNext()) {
			Component o = iterator.next();
			if (!names.contains(o.getName())) {
				iterator.remove();
			}
		}
	}

}
