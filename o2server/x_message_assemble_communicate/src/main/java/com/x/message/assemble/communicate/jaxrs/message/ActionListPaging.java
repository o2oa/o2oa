package com.x.message.assemble.communicate.jaxrs.message;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

class ActionListPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCustomCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(Message.class, Wo.copier, p, page, size, Message.sequence_FIELDNAME);

			result.setData(wos);
			result.setCount(emc.count(Message.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.conjunction();

		if (StringUtils.isNotEmpty(wi.getPerson())) {
			p = cb.and(p, cb.equal(root.get(Message_.person), wi.getPerson()));
		}

		if (StringUtils.isNotEmpty(wi.getType())) {
			p = cb.and(p, cb.equal(root.get(Message_.type), wi.getType()));
		}

		if (StringUtils.isNotEmpty(wi.getConsume())) {
			p = cb.and(p, cb.equal(root.get(Message_.consumer), wi.getConsume()));
		}

		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getStartTime()))) {
			p = cb.and(p, cb.greaterThan(root.get(JpaObject_.createTime), DateTools.parse(wi.getStartTime())));
		}
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getEndTime()))) {
			p = cb.and(p, cb.lessThan(root.get(JpaObject_.createTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -8335537395971819377L;

		@FieldDescribe("用户")
		private String person;

		@FieldDescribe("消息类型.")
		private String type;

		@FieldDescribe("消费者")
		private String consume;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getConsume() {
			return consume;
		}

		public void setConsume(String consume) {
			this.consume = consume;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
	}

	public static class Wo extends Message {

		private static final long serialVersionUID = 681982898431236763L;
		static WrapCopier<Message, Wo> copier = WrapCopierFactory.wo(Message.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
