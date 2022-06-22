package com.x.component.assemble.control.schedule;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Components;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

public class InitComponents extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitComponents.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			this.init();
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void init() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			List<Component> os = emc.listEqualOrIn(Component.class, Component.TYPE_FIELDNAME, Component.TYPE_SYSTEM,
					Component.NAME_FIELDNAME, Components.SYSTEM_NAME_NAMES);

			List<String> existNames = ListTools.extractProperty(os, Component.NAME_FIELDNAME, String.class, true, true);

			List<Component> removes = new ArrayList<>();
			for (Component o : os) {
				if (!Components.SYSTEM_NAME_NAMES.contains(o.getName())) {
					removes.add(o);
				}
			}
			if (!removes.isEmpty()) {
				emc.beginTransaction(Component.class);
				for (Component o : removes) {
					emc.remove(o, CheckRemoveType.all);
				}
				emc.commit();
			}
			List<Component> adds = new ArrayList<>();
			for (com.x.base.core.project.config.Components.Component o : Config.components().getSystems()) {
				if (!existNames.contains(o.getName())) {
					Component component = new Component();
					component.setName(o.getName());
					component.setPath(o.getPath());
					component.setTitle(o.getTitle());
					component.setIconPath(o.getIconPath());
					component.setOrderNumber(o.getOrderNumber());
					component.setVisible(true);
					component.setType(Component.TYPE_SYSTEM);
					adds.add(component);
				}
			}
			if (!adds.isEmpty()) {
				emc.beginTransaction(Component.class);
				for (Component o : adds) {
					emc.persist(o, CheckPersistType.all);
				}
				emc.commit();
			}
		}

	}

}
