package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import com.x.organization.core.entity.Unit_;

/**
 * 清空人员组织数据
 */
public class ActionWipeAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionWipeAll.class);
	List<Unit> allUnitList = new ArrayList<>();
	List<UnitAttribute> allUnitAttributeList = new ArrayList<>();
	List<Person> allPersonList = new ArrayList<>();
	List<PersonAttribute> allPersonAttributeList = new ArrayList<>();
	List<Identity> allIdentityList = new ArrayList<>();
	List<UnitDuty> allDutyList = new ArrayList<>();
	List<Group> allGroupList = new ArrayList<>();
	
	protected ActionResult<Wo> execute(  EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Business business = null;
		
		// 先获取需要删除的数据
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.info("开始删除人员组织所有数据--------start");
			 business = new Business(emc);
			this.listUnit(business);
			this.listPerson(business);
			this.listUnitAttribute(business);
			this.listPersonAttribute(business);
			this.listIdentity(business);
			this.listDuty(business);
			this.listGroup(business);
			
			emc.beginTransaction( UnitDuty.class );
			emc.beginTransaction( Group.class );
			emc.beginTransaction( Identity.class );
			emc.beginTransaction( Person.class );
			//emc.beginTransaction( Unit.class );
			logger.info("开始删除职务--------");
			this.deleteDutys(emc,business);
			logger.info("开始删除群组--------");
			this.deleteGroups(emc,business);
			logger.info("开始删除身份--------");
			this.deleteIdentitys(emc,business);

			logger.info("开始删除人员属性--------");
			this.deletePersonAttributes(emc,business);
			logger.info("开始删除组织属性--------");
			this.deleteUnitAttributes(emc,business);

			logger.info("开始删除人员--------");
			this.deletePersons(emc,business);
			logger.info("开始删除组织--------");
			this.deleteUnits(emc,business);
			emc.commit();

			CacheManager.notify(UnitDuty.class);
			CacheManager.notify(Group.class);
			CacheManager.notify(Identity.class);
			CacheManager.notify(PersonAttribute.class);
			CacheManager.notify(UnitAttribute.class);
			CacheManager.notify(Person.class);
			CacheManager.notify(Unit.class);
			
			wo.setFlag("清空人员数据成功");
			logger.info("开始删除人员组织所有数据--------end");
		} catch (Exception e) {
			logger.info("系统在查询所有组织人员信息时发生异常。" );
			wo.setFlag("清空人员数据失败");
			e.printStackTrace();
		}
		
			
		result.setData(wo);
		return result;
	}
	
	private void listUnit(Business business) throws Exception {
		List<Unit> topUnitList = new ArrayList<>();
		topUnitList = this.listTopUnit(business);
		if(ListTools.isNotEmpty(topUnitList)){
			allUnitList.addAll(topUnitList);
			for (Unit unitItem : topUnitList) {
				List<Unit> ulist= this.listSubNested(business, unitItem);
				if(ListTools.isNotEmpty(ulist)){
					allUnitList.addAll(ulist);
				}
			}
		}
		
		if(ListTools.isNotEmpty(allUnitList)){
			//allUnitList = business.unit().sort(allUnitList);
			allUnitList = allUnitList.stream().sorted(Comparator.comparing(Unit::getLevel).reversed()).collect(Collectors.toList());
		}
	}
	private void listUnitAttribute(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		allUnitAttributeList = em.createQuery(cq.select(root)).getResultList();
	}
	
	private void listPerson(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		allPersonList = em.createQuery(cq.select(root)).getResultList();
	}
	private void listPersonAttribute(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		allPersonAttributeList = em.createQuery(cq.select(root)).getResultList();
	}
	
	private void listIdentity(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		allIdentityList = em.createQuery(cq.select(root)).getResultList();
	}
	
	private void listDuty(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		allDutyList = em.createQuery(cq.select(root)).getResultList();
	}
	
	private void listGroup(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		allGroupList = em.createQuery(cq.select(root)).getResultList();
	}

	private List<Unit> listTopUnit(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.level), 1);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}
	
	private List<Unit> listSubNested(Business business,Unit unit) throws Exception {
		List<Unit> os = business.unit().listSubNestedObject(unit);
		return os;
	}
	private void deletePersonAttributes(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allPersonAttributeList)){
			for(PersonAttribute pa : allPersonAttributeList){
				business.entityManagerContainer().beginTransaction(PersonAttribute.class);
				business.entityManagerContainer().remove(pa, CheckRemoveType.all);
			}
			business.entityManagerContainer().commit();
		}

	}
	private void deleteUnitAttributes(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allUnitAttributeList)){
			for(UnitAttribute ua : allUnitAttributeList){
				business.entityManagerContainer().beginTransaction(UnitAttribute.class);
				business.entityManagerContainer().remove(ua, CheckRemoveType.all);
			}
			business.entityManagerContainer().commit();
		}

	}
	
	private void deleteUnits(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allUnitList)){
			/*for(Unit unit : allUnitList){
				//this.deleteEntity("unit/"+unit.getId());
				//emc.remove( unit , CheckRemoveType.all );
			}*/
			for(int i=0; i<allUnitList.size();i++){
				Unit unit = allUnitList.get(i);
				business.entityManagerContainer().beginTransaction(Unit.class);
				business.entityManagerContainer().remove(unit, CheckRemoveType.all);
				business.entityManagerContainer().commit();
			}
		}
		
	}
	
	private void deletePersons(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allPersonList)){
			for(Person person : allPersonList){
				business.entityManagerContainer().beginTransaction(Person.class);
				business.entityManagerContainer().remove(person, CheckRemoveType.all);
				//emc.remove( person , CheckRemoveType.all );
			}
			business.entityManagerContainer().commit();
		}
		
	}
	
	private void deleteIdentitys(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allIdentityList)){
			for(Identity identity : allIdentityList){
				//this.deleteEntity("identity/"+identity.getId());
				//emc.remove( identity , CheckRemoveType.all );
				business.entityManagerContainer().beginTransaction(Identity.class);
				business.entityManagerContainer().remove(identity, CheckRemoveType.all);
			}
			business.entityManagerContainer().commit();
		}
		
	}
	
	private void deleteDutys(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allDutyList)){
			for(UnitDuty unitDuty : allDutyList){
				//this.deleteEntity("unitduty/"+unitDuty.getId());
				//emc.remove( unitDuty , CheckRemoveType.all );
				business.entityManagerContainer().beginTransaction(UnitDuty.class);
				business.entityManagerContainer().remove(unitDuty, CheckRemoveType.all);
				business.entityManagerContainer().commit();
			}
		}
		
	}
	
	private void deleteGroups(EntityManagerContainer emc,Business business) throws Exception{
		if(ListTools.isNotEmpty(allGroupList)){
			for(Group group : allGroupList){
				if(group != null){
					//this.deleteEntity("group/"+group.getId());
					//emc.remove( group , CheckRemoveType.all );
					this.removeGroupMember(business,group);
					
					business.entityManagerContainer().beginTransaction(Group.class);
					business.entityManagerContainer().remove(group, CheckRemoveType.all);
					business.entityManagerContainer().commit();
				}
				
			}
		}
		
	}
	
	private void removeGroupMember(Business business, Group group) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(group.getId(), root.get(Group_.groupList));
		List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
		
		for (Group o : os) {
			o.getGroupList().remove(group.getId());
			business.entityManagerContainer().beginTransaction(Group.class);
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
		business.entityManagerContainer().commit();
		
	}
	
	private String deleteEntity(String path) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.deleteQuery(x_organization_assemble_control.class, path);
		return resp.getMessage();
	}
	
	private List<PersonAttribute> listAttributeWithPerson(Business business,String personId) throws Exception{
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), personId);
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}
	
	private List<UnitDuty> listDutyWithIdentity(Business business,String identityId) throws Exception{
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identityId, root.get(UnitDuty_.identityList));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("返回的结果标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

}
