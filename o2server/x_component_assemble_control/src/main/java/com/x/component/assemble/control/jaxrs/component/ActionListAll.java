package com.x.component.assemble.control.jaxrs.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

import net.sf.ehcache.Element;

class ActionListAll extends ActionBase {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				wos = (List<Wo>) element.getObjectValue();
			} else {
				List<Component> os = emc.listAll(Component.class);
				if (os.isEmpty()) {
					/* 一个模块都没有新建默认 */
					synchronized (ActionListAll.class) {
						if (emc.listAll(Component.class).isEmpty()) {
							emc.beginTransaction(Component.class);
							for (String name : DEFAULT_COMPONENT_LIST) {
								Component o = this.createComponent(name);
								emc.persist(o, CheckPersistType.all);
								os.add(o);
							}
							emc.commit();
							ApplicationCache.notify(Component.class);
						}
					}
				}
				wos = Wo.copier.copy(os);
				wos = wos.stream().sorted(
						Comparator.comparing(Component::getOrderNumber, Comparator.nullsLast(Integer::compareTo)))
						.collect(Collectors.toList());
				cache.put(new Element(cacheKey, wos));
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Component {

		private static final long serialVersionUID = -340611438251489741L;

		static WrapCopier<Component, Wo> copier = WrapCopierFactory.wo(Component.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private Component createComponent(String name) {
		Component o = new Component();
		switch (name) {
		case COMPONENT_FILE:
			o.setName(COMPONENT_FILE);
			o.setPath(COMPONENT_FILE);
			o.setTitle("云文件");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_NOTE:
			o.setName(COMPONENT_NOTE);
			o.setPath(COMPONENT_NOTE);
			o.setTitle("便签");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_MEETING:
			o.setName(COMPONENT_MEETING);
			o.setPath(COMPONENT_MEETING);
			o.setTitle("会议管理");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_EXECUTION:
			o.setName(COMPONENT_EXECUTION);
			o.setPath(COMPONENT_EXECUTION);
			o.setTitle("执行力管理");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_ATTENDANCE:
			o.setName(COMPONENT_ATTENDANCE);
			o.setPath(COMPONENT_ATTENDANCE);
			o.setTitle("考勤管理");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_FORUM:
			o.setName(COMPONENT_FORUM);
			o.setPath(COMPONENT_FORUM);
			o.setTitle("论坛");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_HOTARTICLE:
			o.setName(COMPONENT_HOTARTICLE);
			o.setPath(COMPONENT_HOTARTICLE);
			o.setTitle("热点");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_EXEMANAGER:
			o.setName(COMPONENT_EXEMANAGER);
			o.setPath(COMPONENT_EXEMANAGER);
			o.setTitle("执行力配置");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_ONLINEMEETING:
			o.setName(COMPONENT_ONLINEMEETING);
			o.setPath(COMPONENT_ONLINEMEETING);
			o.setTitle("网络会议");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
//		case COMPONENT_STRATEGY:
//			o.setName(COMPONENT_STRATEGY);
//			o.setPath(COMPONENT_STRATEGY);
//			o.setTitle("战略管理");
//			o.setIconPath("appicon.png");
//			o.setVisible(true);
//			break;
//		case COMPONENT_REPORT:
//			o.setName(COMPONENT_REPORT);
//			o.setPath(COMPONENT_REPORT);
//			o.setTitle("工作报告");
//			o.setIconPath("appicon.png");
//			o.setVisible(true);
//			break;
		case COMPONENT_MINDER:
			o.setName(COMPONENT_MINDER);
			o.setPath(COMPONENT_MINDER);
			o.setTitle("脑图编辑器");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_CALENDAR:
			o.setName(COMPONENT_CALENDAR);
			o.setPath(COMPONENT_CALENDAR);
			o.setTitle("日程安排");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_ANN:
			o.setName(COMPONENT_ANN);
			o.setPath(COMPONENT_ANN);
			o.setTitle("神经网络");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		case COMPONENT_SEARCH:
			o.setName(COMPONENT_SEARCH);
			o.setPath(COMPONENT_SEARCH);
			o.setTitle("搜索");
			o.setIconPath("appicon.png");
			o.setVisible(true);
			break;
		default:
			break;
		}
		return o;
	}

}
