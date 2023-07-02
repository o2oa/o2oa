package com.x.organization.assemble.control.jaxrs.personcard;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.PersonCard;



class ActionExportPersonalVcf extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExportPersonalVcf.class);
	//导出通讯录vcf
	ActionResult<Wo> exportPersonalVcf(EffectivePerson effectivePerson, String idList) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			List<String> personIdList = Arrays.asList(idList.split(","));
			Wo wo = null;
			if(!personIdList.isEmpty()){
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				
				for (String flag : personIdList) {
					PersonCard personCard = emc.find(flag, PersonCard.class);
					if (null == personCard) {
						throw new ExceptionPersonNotExist(flag);
					}
					os.write("BEGIN:VCARD".getBytes());
					os.write("\r\n".getBytes());
					os.write("VERSION:3.0".getBytes());
					os.write("\r\n".getBytes());
					os.write(("N:"+personCard.getName()+";;;;").getBytes());
					os.write("\r\n".getBytes());
					os.write(("FN:"+personCard.getName()).getBytes());
					os.write("\r\n".getBytes());	    
					if(personCard.getOfficePhone()!=null && !personCard.getOfficePhone().equals("")){
						os.write(("TEL:"+personCard.getOfficePhone()).getBytes());
						os.write(("\r\n").getBytes());
					}
					if(personCard.getMobile()!=null && !personCard.getMobile().equals("")){
						os.write(("TEL;CELL:"+personCard.getMobile()).getBytes());
						os.write("\r\n".getBytes());
					}
					if(personCard.getAddress()!=null && !personCard.getAddress().equals("")){
						os.write(("ADR;HOME;POSTAL:;;"+personCard.getAddress()).getBytes());
						os.write("\r\n".getBytes());
					}
					if(personCard.getGroupType()!=null && !personCard.getGroupType().equals("")){
						os.write(("ORG:"+personCard.getGroupType()).getBytes());
						os.write("\r\n".getBytes());
					}
					if(personCard.getDescription()!=null && !personCard.getDescription().equals("")){
						os.write(("NOTE:"+personCard.getDescription()).getBytes());
						os.write("\r\n".getBytes());
					}									
					os.write("END:VCARD".getBytes());
					os.write("\r\n".getBytes());
					
				}
				String fileName = "export_personal_contacts"+System.currentTimeMillis()+".vcf";
				try {
					wo = new Wo(os.toByteArray(), 
							this.contentType(false, fileName), 
							this.contentDisposition(false, fileName));
				} finally {
					os.close();
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

}
