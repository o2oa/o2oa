package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;

class ActionManagerClear extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerClear.class);

	/** JPQL 命名参数, 用户标识只允许以参数绑定方式进入语句. */
	private static final String PERSON_PARAMETER = "person";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String person) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.info("{}操作清除【{}】的待办处理方式记录.", effectivePerson.getDistinguishedName(), person);
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			EntityManager em = emc.get(TaskProcessMode.class);
			StringBuilder sql = new StringBuilder("DELETE FROM ");
			sql.append(TaskProcessMode.class.getName()).append(" o");
			String personValue = null;
			if (!EMPTY_SYMBOL.equals(person)) {
				personValue = this.resolvePerson(business, person);
				sql.append(" WHERE o.person = :").append(PERSON_PARAMETER);
			}
			emc.beginTransaction(TaskProcessMode.class);
			Query query = em.createQuery(sql.toString());
			if (null != personValue) {
				query.setParameter(PERSON_PARAMETER, personValue);
			}
			query.executeUpdate();
			emc.commit();

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	/**
	 * 将传入的用户标识解析为组织架构中的唯一标识(unique).
	 * <p>
	 * 解析结果只通过 JPQL 命名参数绑定, 不参与语句拼接, 因此无论是否能在组织架构中命中,
	 * 原始输入都不具备改变语句结构的能力; 未命中时按原始标识作为查询条件,
	 * 以兼容清理已离职/已删除用户遗留记录的场景.
	 */
	private String resolvePerson(Business business, String person) throws Exception {
		if (StringUtils.isBlank(person)) {
			return person;
		}
		Person personObj = business.organization().person().getObject(person);
		if ((null != personObj) && StringUtils.isNotBlank(personObj.getUnique())) {
			return personObj.getUnique();
		}
		LOGGER.warn("清除待办处理方式记录时未能从组织架构解析用户标识【{}】, 按原始标识作为查询条件.", person);
		return person;
	}

	public static class Wo extends WrapBoolean {

	}
}
