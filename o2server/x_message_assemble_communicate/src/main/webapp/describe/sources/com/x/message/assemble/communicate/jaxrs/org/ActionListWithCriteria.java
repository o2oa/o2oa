package com.x.message.assemble.communicate.jaxrs.org;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Org;
import com.x.message.core.entity.Org_;

public class ActionListWithCriteria extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListWithCriteria.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String consume, Integer count, String receiveSystem, String consumedModule)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/*
			String name = business.organization().person().get(person);
			if (StringUtils.isEmpty(name)) {
				throw new ExceptionPersonNotExist(name);
			}*/
			String operType = "";
			String orgType = "";
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = this.list(business, Boolean.valueOf(consume), NumberUtils.min(200, NumberUtils.max(1, count)), receiveSystem,consumedModule,operType,orgType);
			result.setData(wos);
			return result;
		}
	}

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			
			//Person person = new Person();
			//Wi.copier.copy(wi, person);
			String receiveSystem = wi.getReceiveSystem();
			String consumedModule = wi.getConsumedModule();
			String operType = wi.getOperType();
			String orgType = wi.getOrgType();
			int count = 20;
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = this.list(business, wi.getConsume(), NumberUtils.min(200, NumberUtils.max(1, count)), receiveSystem,consumedModule,operType,orgType);
			result.setData(wos);
			return result;
		}
	}
	
	private List<Wo> list(Business business, boolean consume, Integer count, String receiveSystem, String consumedModule,String operType,String orgType ) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Org.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Org> cq = cb.createQuery(Org.class);
		
		Root<Org> root = cq.from(Org.class);
		
		Predicate p = cb.equal(root.get(Org_.consumed), consume);
		
		if(!StringUtils.isEmpty(receiveSystem.trim())) {
			p = cb.and(p, cb.equal(root.get(Org_.receiveSystem), receiveSystem));
		}
		if(!StringUtils.isEmpty(consumedModule.trim())) {
	    	p = cb.and(p, cb.like(root.get(Org_.consumedModule),"%," + consumedModule+",%"));
		}
		
		if(!StringUtils.isEmpty(operType.trim())) {
			p= cb.and(p,cb.equal(root.get(Org_.operType),operType));
		}
		
		if(!StringUtils.isEmpty(orgType.trim())) {
	       p = cb.and(p, cb.equal(root.get(Org_.orgType), orgType));
		}
		
		//p = cb.and( p, cb.isFalse( root.get(Message_.consumed) ));
		List<Org> orgs = em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(Org_.createTime))))
				.setMaxResults(count).getResultList();
		return Wo.copier.copy(orgs);
	}
	
    
	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("操作类型(add|modify|delete)")
		private String operType;
		@FieldDescribe("类型(person(个人)|unit(部门)|group(群组)|identity(身份)|role(角色)|duty(职务))")
		private String orgType;
		@FieldDescribe("是否消费(true|false),必填项")
		private boolean consume;
		@FieldDescribe("接收系统")
		private String receiveSystem;
		@FieldDescribe("各应用模块名称")
		private String consumedModule;
		
		public String getOperType() {
			return operType;
		}
		public void setOperType(String operType) {
			this.operType = operType;
		}
		public String getOrgType() {
			return orgType;
		}
		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}
		public boolean getConsume() {
			return consume;
		}
		public void setConsume(boolean consume) {
			this.consume = consume;
		}
		public String getReceiveSystem() {
			return receiveSystem;
		}
		public void setReceiveSystem(String receiveSystem) {
			this.receiveSystem = receiveSystem;
		}
		public String getConsumedModule() {
			return consumedModule;
		}
		public void setConsumedModule(String consumedModule) {
			this.consumedModule = consumedModule;
		}
		
		
	}

	public static class Wo extends Org {

		private static final long serialVersionUID = 681982898431236763L;
		static WrapCopier<Org, Wo> copier = WrapCopierFactory.wo(Org.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
