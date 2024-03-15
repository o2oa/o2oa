package com.x.program.center.jaxrs.code;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Code;
import com.x.program.center.core.entity.Code_;

class ActionValidate extends BaseAction {
	ActionResult<Wo> execute(String mobile, String answer) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo;
		String customSms = ThisApplication.context().applications().findApplicationName(CUSTOM_SMS_APPLICATION);
		if(StringUtils.isNotBlank(customSms) && Config.customConfig(CUSTOM_SMS_CONFIG_NAME) != null){
			ActionResponse resp = ThisApplication.context().applications()
					.getQuery(customSms, Applications.joinQueryUri("sms", "validate", "mobile", mobile, "answer", answer, "token", "(0)"));
			wo = resp.getData(Wo.class);
		}else {
			ActionResponse resp = ConnectionAction.get(Config.collect().url()
					+ "/o2_collect_assemble/jaxrs/code/validate/mobile/" + mobile + "/answer/" + answer, null);
			wo = resp.getData(Wo.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

	private Code get(EntityManagerContainer emc, String mobile) throws Exception {
		EntityManager em = emc.get(Code.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Code> cq = cb.createQuery(Code.class);
		Root<Code> root = cq.from(Code.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -5);
		Predicate p = cb.greaterThan(root.get(Code_.createTime), cal.getTime());
		p = cb.and(p, cb.equal(root.get(Code_.mobile), mobile));
		//p = cb.and(p, cb.equal(root.get(Code_.answer), answer));
		List<Code> list = em.createQuery(cq.where(p).orderBy(cb.desc(root.get(Code_.createTime)))).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
