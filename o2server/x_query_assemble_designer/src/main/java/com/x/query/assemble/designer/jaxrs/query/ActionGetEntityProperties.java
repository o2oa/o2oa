package com.x.query.assemble.designer.jaxrs.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Lob;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionGetEntityProperties extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetEntityProperties.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String entity, String entityCategory)
			throws Exception {

		LOGGER.debug("execute:{}, entity:{}, entityCategory:{}.", effectivePerson::getDistinguishedName, () -> entity,
				() -> entityCategory);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Class<? extends JpaObject> cls = this.clazz(business, classLoader, entity, entityCategory);
			result.setData(this.getEntityDes(cls, true, false));
			return result;
		}
	}

	private <T extends JpaObject> List<Wo> getEntityDes(Class<T> clz, Boolean excludeInvisible, Boolean excludeLob) {
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;
		for (Field field : FieldUtils.getFieldsListWithAnnotation(clz, Column.class)) {
			if (BooleanUtils.isTrue(excludeInvisible) && JpaObject.FieldsInvisible.contains(field.getName())) {
				continue;
			}
			if (BooleanUtils.isTrue(excludeLob)) {
				if (null != field.getAnnotation(Lob.class)) {
					continue;
				} else {
					Strategy strategy = field.getAnnotation(Strategy.class);
					if ((null != strategy)
							&& StringUtils.equals(JpaObject.JsonPropertiesValueHandler, strategy.value())) {
						continue;
					}
				}
			}
			wo = new Wo();
			wo.setName(field.getName());
			wo.setType(field.getType().getSimpleName());
			FieldDescribe fd = field.getAnnotation(FieldDescribe.class);
			if (fd != null) {
				wo.setDescription(fd.value());
			}
			wos.add(wo);
		}
		return wos;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends JpaObject> clazz(Business business, ClassLoader classLoader, String entity,
			String entityCategory) throws Exception {
		Class<? extends JpaObject> cls = null;
		if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, entityCategory)
				|| StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, entityCategory)) {
			try {
				cls = (Class<? extends JpaObject>) classLoader.loadClass(entity);
			} catch (Exception e) {
				throw new ExceptionEntityNotExist(entity, entityCategory);
			}
		} else {
			Table table = business.entityManagerContainer().flag(entity, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(entity, Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			try {
				cls = (Class<? extends JpaObject>) classLoader.loadClass(dynamicEntity.className());
			} catch (Exception e) {
				throw new ExceptionEntityNotExist(entity, entityCategory);
			}
		}
		return cls;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("属性名称.")
		private String name;
		@FieldDescribe("属性类型.")
		private String type;
		@FieldDescribe("属性描述.")
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
