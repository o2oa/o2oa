package o2.collect.assemble.jaxrs.code;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.StringTools;

import net.sf.ehcache.Element;
import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.assemble.sms.SmsMessage;
import o2.collect.assemble.sms.SmsMessageType;
import o2.collect.assemble.sms.SmsSender;
import o2.collect.core.entity.Unit;
import o2.collect.core.entity.Unit_;

class ActionTransfer extends BaseAction {

//	private static final Integer TRANSFER_INTERVAL = 30;
//
//	private static final Integer MAX_INTERVAL_COUNT = 10;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getMobile())) {
				throw new ExceptionMobileEmpty();
			}
//			Integer count = this.intervalCount(business, wi.getMobile());
//			if (count > MAX_INTERVAL_COUNT) {
//				throw new ExceptionMaxInterval(wi.getMobile(), TRANSFER_INTERVAL, count);
//			}
			/** name是老名字 20180320 */
			String unit = wi.getUnit();
			if (StringUtils.isEmpty(unit)) {
				unit = wi.getName();
			}
			if (StringUtils.isEmpty(unit)) {
				throw new ExceptionUnitEmpty();
			}
			if (StringUtils.isEmpty(wi.getPassword())) {
				throw new ExceptionPasswordEmpty();
			}
			if (StringUtils.isEmpty(wi.getMobile())) {
				throw new ExceptionMobileEmpty();
			}
			if (StringUtils.isEmpty(wi.getAnswer())) {
				wi.setAnswer(StringTools.randomNumber4());
			}
			Unit o = findUnit(business, unit);
			if (null == o) {
				throw new ExceptionUnitNotExist(unit);
			}
			if (!StringUtils.equals(Crypto.encrypt(wi.getPassword(), Config.token().getKey()), o.getPassword())) {
				throw new ExceptionPasswordNotMatch(unit);
			}
			SmsMessage smsMessage = new SmsMessage();
			smsMessage.setSmsMessageType(SmsMessageType.code);
			smsMessage.setMobile(wi.getMobile());
			smsMessage.setMessage(wi.getAnswer());
			SmsSender.send(smsMessage);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
		}
		return result;
	}

	private Unit findUnit(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.name), name);
		cq.select(root).where(p);
		List<Unit> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("号码")
		private String mobile;
		@FieldDescribe("值")
		private String answer;
		@FieldDescribe("名称")
		private String unit;
		@FieldDescribe("旧名称")
		private String name;
		@FieldDescribe("密码")
		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Wo extends WrapBoolean {

	}

//	private Integer intervalCount(Business business, String mobile) {
//		String cacheKey = ApplicationCache.concreteCacheKey(mobile);
//		Element element = business.codeCache().get(cacheKey);
//		Integer count = 1;
//		if ((null != element) && (null != element.getObjectValue())) {
//			count = (Integer) element.getObjectValue();
//			count = count + 1;
//		}
//		business.codeCache().put(new Element(cacheKey, count));
//		return count;
//	}
}