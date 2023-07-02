package com.x.organization.assemble.control.jaxrs.personcard;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;




class ActionExportVcf extends BaseAction {


	//导出通讯录vcf
	ActionResult<Wo> exportVcf(EffectivePerson effectivePerson, String idList) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> personIdList = Arrays.asList(idList.split(","));
			Wo wo = null;
			if(!personIdList.isEmpty()){
				ByteArrayOutputStream reader = new ByteArrayOutputStream();
				
				for (String flag : personIdList) {
					Person personCard = emc.find(flag, Person.class);
					if (null == personCard) {
						throw new ExceptionPersonNotExist(flag);
					}
					reader.write("BEGIN:VCARD".getBytes());
					reader.write("\r\n".getBytes());
					reader.write("VERSION:3.0".getBytes());
					reader.write("\r\n".getBytes());
					reader.write(("N:"+personCard.getName()+";;;;").getBytes());
					reader.write("\r\n".getBytes());
					reader.write(("FN:"+personCard.getName()).getBytes());
					reader.write("\r\n".getBytes());	    
					if(personCard.getOfficePhone()!=null && !personCard.getOfficePhone().equals("")){
						reader.write(("TEL:"+personCard.getOfficePhone()).getBytes());
						reader.write("\r\n".getBytes());
					}
					if(personCard.getMobile()!=null && !personCard.getMobile().equals("")){
						reader.write(("TEL;CELL:"+personCard.getMobile()).getBytes());
						reader.write("\r\n".getBytes());
					}
					/*if(!personCard.getAddress().equals("")){
						content = content+"ADR;HOME;POSTAL:;;"+personCard.getAddress()+";;;;\n";
					}*/
					if(personCard.getMail()!=null && !personCard.getMail().equals("")){
						reader.write(("EMAIL:"+personCard.getMail()).getBytes());
						reader.write("\r\n".getBytes());
					}
					if(personCard.getQq()!=null && !personCard.getQq().equals("")){
						reader.write(("X-QQ:"+personCard.getQq()).getBytes());
						reader.write("\r\n".getBytes());
					}
					if(personCard.getWeixin()!=null && !personCard.getWeixin().equals("")){
						reader.write(("NOTE:微信:"+personCard.getWeixin()).getBytes());
						reader.write("\r\n".getBytes());
					}
					if(personCard.getBirthday()!=null && !personCard.getBirthday().equals("")){
						reader.write(("BDAY:"+DateTools.formatDate(personCard.getBirthday())).getBytes());
						reader.write("\r\n".getBytes());
					}
					if(personCard.getId()!=null && !personCard.getId().equals("")){
						List<Identity> identityList = referenceIdentity(business,flag);
						if(identityList.isEmpty()){
							if(!personCard.getTopUnitList().isEmpty()){
								String TopUnitId = personCard.getTopUnitList().get(0);
								Unit unit = emc.find(flag, Unit.class);
								if (null == unit) {
									throw new ExceptionEntityNotExist(flag);
								}
								reader.write(("ORG:"+unit.getName()).getBytes());
								reader.write("\r\n".getBytes());
							}
						}else{
							String orgString = "";
							String titleString = "";
							for (Identity identity : identityList) {
								orgString = orgString.equals("")?identity.getUnitLevelName():orgString +";"+identity.getUnitLevelName();
								List<UnitDuty> unitdutyList = referenceUnitduty(business,identity.getId());
								if(!unitdutyList.isEmpty()){
									for(UnitDuty unitduty : unitdutyList){
										if(null != unitduty){
											titleString = titleString.equals("")?unitduty.getName():titleString +";"+unitduty.getName();
										}
									}
								}
							}
							if(!orgString.equals("")){
								reader.write(("ORG:"+orgString).getBytes());
								reader.write("\r\n".getBytes());
							}
							if(!titleString.equals("")){
								reader.write(("TITLE:"+titleString).getBytes());
								reader.write("\r\n".getBytes());
							}
						}	
					}
					
					reader.write("END:VCARD".getBytes());
					reader.write("\r\n".getBytes());
					
				}
				String fileName = "export_person_contacts"+System.currentTimeMillis()+".vcf";
				try {
					wo = new Wo(reader.toByteArray(), 
							this.contentType(false, fileName), 
							this.contentDisposition(false, fileName));
				} finally {
					reader.close();
				}
				result.setData(wo);
			}

			return result;
		}
	}
	
	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
	
	public List<Identity> referenceIdentity(Business business,String flag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), flag);
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}
	public List<UnitDuty> referenceUnitduty(Business business,String flag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(flag, root.get(UnitDuty_.identityList));
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

}
