package com.x.organization.assemble.control.jaxrs.personcard;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.staticconfig.FollowConfig;
import com.x.organization.core.entity.PersonCard;

import net.sf.ehcache.Element;



class ActionExportPersonalVcf2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExportPersonalVcf2.class);
	//导出通讯录vcf
	ActionResult<Wo> exportPersonalVcf(EffectivePerson effectivePerson, String idList) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/*Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> personIdList = wi.getPersonList();*/
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
				String fileName = "export_personal_contacts"+System.currentTimeMillis()+".txt";
				/*VcfResultObject obj = new VcfResultObject();
				obj.setBytes(os.toByteArray());
				obj.setName(name);
				String flagid = StringTools.uniqueToken();
				business.cache().put(new Element(flagid, obj));
				
				//result = execute(effectivePerson,flagid);
				String cacheKey = ApplicationCache.concreteCacheKey(flagid);
				Element element = business.cache().get(cacheKey);
				if (null != element && null != element.getObjectValue()) {
					result.setData((Wo) element.getObjectValue());				
				} else {
					Wo wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
							this.contentDisposition(true, obj.getName()));
					business.cache().put(new Element(cacheKey, wo));
					result.setData(wo); 
				}*/
				try {
					wo = new Wo(os.toByteArray(), 
							this.contentType(true, fileName), 
							this.contentDisposition(true, fileName));
				} finally {
					os.close();
				}
				result.setData(wo);
			}

			return result;
		}
	}
	
	/*public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人通讯录人员标识")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}*/
	public static class VcfResultObject extends GsonPropertyObject {

		private byte[] bytes;
		private String name;
		
		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
	/*public static class Wo extends WoId {
		
	}*/

}
