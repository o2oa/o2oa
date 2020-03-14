package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.message.OrgBodyMessage;
import com.x.organization.assemble.control.message.OrgMessage;
import com.x.organization.assemble.control.message.OrgMessageFactory;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import com.x.organization.core.entity.Unit_;

class ActionDelete extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (Config.token().isInitialManager(flag)) {
				throw new ExceptionDenyDeleteInitialManager();
			} else {
				Business business = new Business(emc);
				Person person = business.person().pick(flag);
				if (null == person) {
					throw new ExceptionEntityNotExist(flag, Person.class);
				}
				/** 从内存中取到人 */
				person = emc.find(person.getId(), Person.class);
				if (!this.editable(business, effectivePerson, person)) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				List<Identity> identities = this.listIdentity(business, person);
				/** 删除身份组织职务成员,提交后才可以删除身份 */
				emc.beginTransaction(UnitDuty.class);
				this.removeMemberOfUnitDuty(business, identities);
				emc.commit();
				/** 删除身份 */
				emc.beginTransaction(Identity.class);
				for (Identity o : identities) {
					emc.remove(o, CheckRemoveType.all);
				}
				emc.commit();
				/** 删除个人属性 */
				emc.beginTransaction(PersonAttribute.class);
				this.removePersonAttribute(business, person);
				/** 删除群组成员 */
				emc.beginTransaction(Group.class);
				this.removeMemberOfGroup(business, person);
				/** 删除角色成员 */
				emc.beginTransaction(Role.class);
				this.removeMemberOfRole(business, person);
				/** 删除组织的管理个人以及继承的管理个人 */
				emc.beginTransaction(Unit.class);
				this.removeMemberOfUnitController(business, person);
				/** 删除个人管理者成员 */
				this.removeMemberOfPersonController(business, person);
				/** 删除汇报人员为将要删除的人 */
				this.removeMemberOfPersonSuperior(business, person);
				emc.beginTransaction(Person.class);
				/** 先进行一次提交,通过check */
				emc.commit();
				emc.beginTransaction(Person.class);
				emc.remove(person, CheckRemoveType.all);
				emc.commit();
				ApplicationCache.notify(Person.class);
				/** 通知x_collect_service_transmit同步数据到collect */
				business.instrument().collect().person();
				
				/**创建 组织变更org消息通信 */
				//createMessageCommunicate(person,  effectivePerson);
				OrgMessageFactory  orgMessageFactory = new OrgMessageFactory();
				orgMessageFactory.createMessageCommunicate("delete", "person", person, effectivePerson);
				
				Wo wo = new Wo();
				wo.setId(person.getId());
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private List<Identity> listIdentity(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private void removeMemberOfUnitDuty(Business business, List<Identity> identities) throws Exception {
		List<String> ids = ListTools.extractProperty(identities, JpaObject.id_FIELDNAME, String.class, true, true);
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = root.get(UnitDuty_.identityList).in(ids);
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		for (UnitDuty o : os) {
			o.getIdentityList().removeAll(ids);
		}
	}

	private void removeMemberOfUnitController(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isMember(person.getId(), root.get(Unit_.controllerList));
		p = cb.or(cb.isMember(person.getId(), root.get(Unit_.inheritedControllerList)));
		List<Unit> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		for (Unit o : os) {
			o.getControllerList().remove(person.getId());
			o.getInheritedControllerList().remove(person.getId());
		}
	}

	private void removeMemberOfPersonController(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.isMember(person.getId(), root.get(Person_.controllerList));
		List<Person> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		for (Person o : os) {
			o.getControllerList().remove(person.getId());
		}
	}

	private void removeMemberOfPersonSuperior(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.superior), person.getId());
		List<Person> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		for (Person o : os) {
			o.setSuperior("");
		}
	}

	private void removePersonAttribute(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (PersonAttribute o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void removeMemberOfGroup(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(person.getId(), root.get(Group_.personList));
		List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Group o : os) {
			o.getPersonList().remove(person.getId());
		}
	}

	private void removeMemberOfRole(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(person.getId(), root.get(Role_.personList));
		List<Role> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Role o : os) {
			o.getPersonList().remove(person.getId());
		}
	}
	
	/**创建 组织变更org消息通信 */
	private boolean createMessageCommunicate(Person person, EffectivePerson effectivePerson) {
		try{
			Gson gson = new Gson();
			String strPerson = gson.toJson(person);
			OrgMessage orgMessage = new OrgMessage();
			
			orgMessage.setOperType("delete");
			orgMessage.setOrgType("person");
			orgMessage.setOperUerId(effectivePerson.getDistinguishedName());
			orgMessage.setOperDataId(person.getId());
			orgMessage.setReceiveSystem("");
			orgMessage.setConsumed(false);
			orgMessage.setConsumedModule("");
			
			OrgBodyMessage orgBodyMessage = new OrgBodyMessage();
			orgBodyMessage.setOriginalData(strPerson);
			orgMessage.setBody( gson.toJson(orgBodyMessage));
			
			Applications applications = new Applications();
			String path ="org/create";
		     //String address = "http://127.0.0.1:20020/x_message_assemble_communicate/jaxrs/org/create";
		     //ActionResponse resp = CipherConnectionAction.post(false, address, body);
		     
			ActionResponse resp =  ThisApplication.context().applications()
						.postQuery(x_message_assemble_communicate.class, path, orgMessage);
		
			String mess = resp.getMessage();
			String data = resp.getData().toString();
			return true;
			}catch(Exception e) {
				logger.print(e.toString());
				return false;
			}	
	}

}