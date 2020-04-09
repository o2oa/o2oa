package com.x.organization.assemble.control.jaxrs.personcard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;




import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.staticconfig.FollowConfig;
import com.x.organization.core.entity.PersonCard;



class ActionExportPersonalVcf extends BaseAction {


	//导出通讯录vcf
	ActionResult<String> exportPersonalVcf(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<String> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> personIdList = wi.getPersonList();
			if(!personIdList.isEmpty()){
				//String path = "D:\\tmp\\export_contacts.vcf";
				String path = FollowConfig.VCFPATH+"export_personal_contacts"+System.currentTimeMillis()+".vcf";
				File file = new File(path);
				if (file.exists()) {
					file.createNewFile();
				}
				BufferedWriter reader = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				for (String flag : personIdList) {
					PersonCard personCard = emc.find(flag, PersonCard.class);
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
					if(personCard.getAddress()!=null && !personCard.getAddress().equals("")){
						reader.write("ADR;HOME;POSTAL:;;"+personCard.getAddress());
						reader.write("\r\n");
					}
					if(personCard.getGroupType()!=null && !personCard.getGroupType().equals("")){
						reader.write("ORG:"+personCard.getGroupType());
						reader.write("\r\n");
					}
					if(personCard.getDescription()!=null && !personCard.getDescription().equals("")){
						reader.write("NOTE:"+personCard.getDescription());
						reader.write("\r\n");
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

		@FieldDescribe("个人通讯录人员标识")
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

}
