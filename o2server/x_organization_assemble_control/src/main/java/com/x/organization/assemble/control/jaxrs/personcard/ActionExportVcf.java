package com.x.organization.assemble.control.jaxrs.personcard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.staticconfig.FollowConfig;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;




class ActionExportVcf extends BaseAction {


	//导出通讯录vcf
	ActionResult<String> exportVcf(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<String> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> personIdList = wi.getPersonList();
			if(!personIdList.isEmpty()){
				//String path = "D:\\tmp\\export_contacts.vcf";
				String path = FollowConfig.VCFPATH+"export_contacts"+System.currentTimeMillis()+".vcf";
				File file = new File(path);
				if (file.exists()) {
					file.createNewFile();
				}
				BufferedWriter reader = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				for (String flag : personIdList) {
					Person personCard = emc.find(flag, Person.class);
					if (null == personCard) {
						throw new ExceptionPersonNotExist(flag);
					}
					reader.write("BEGIN:VCARD");
					reader.write("\r\n");
					reader.write("VERSION:3.0");
					reader.write("\r\n");
					reader.write("N:"+personCard.getName()+";;;;");
					reader.write("\r\n");
					reader.write("FN:"+personCard.getName());
					reader.write("\r\n");	    
					if(personCard.getOfficePhone()!=null && !personCard.getOfficePhone().equals("")){
						reader.write("TEL:"+personCard.getOfficePhone());
						reader.write("\r\n");
					}
					if(personCard.getMobile()!=null && !personCard.getMobile().equals("")){
						reader.write("TEL;CELL:"+personCard.getMobile());
						reader.write("\r\n");
					}
					/*if(!personCard.getAddress().equals("")){
						content = content+"ADR;HOME;POSTAL:;;"+personCard.getAddress()+";;;;\n";
					}*/
					if(personCard.getMail()!=null && !personCard.getMail().equals("")){
						reader.write("EMAIL:"+personCard.getMail());
						reader.write("\r\n");
					}
					if(personCard.getQq()!=null && !personCard.getQq().equals("")){
						reader.write("X-QQ:"+personCard.getQq());
						reader.write("\r\n");
					}
					if(personCard.getWeixin()!=null && !personCard.getWeixin().equals("")){
						reader.write("NOTE:微信:"+personCard.getWeixin());
						reader.write("\r\n");
					}
					if(personCard.getBirthday()!=null && !personCard.getBirthday().equals("")){
						reader.write("BDAY:"+DateTools.formatDate(personCard.getBirthday()));
						reader.write("\r\n");
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
								reader.write("ORG:"+unit.getName());
								reader.write("\r\n");
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
								reader.write("ORG:"+orgString);
								reader.write("\r\n");
							}
							if(!titleString.equals("")){
								reader.write("TITLE:"+titleString);
								reader.write("\r\n");
							}
						}	
					}
					
					reader.write("END:VCARD");
					reader.write("\r\n");
					
				}
				reader.flush();
				reader.close();
				result.setData(path);
			}

			return result;
		}
	}
	
	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("通讯录人员标识")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}
	public static class Wo extends WoId {
		
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
