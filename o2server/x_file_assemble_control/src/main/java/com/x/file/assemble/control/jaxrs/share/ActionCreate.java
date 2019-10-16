package com.x.file.assemble.control.jaxrs.share;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DateTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getFileId())) {
				throw new ExceptionShareNameEmpty();
			}
			if (StringUtils.isEmpty(wi.getShareType())) {
				throw new Exception("shareType can not be empty.");
			}
			Share share = business.share().getShareByFileId(wi.getFileId(),effectivePerson.getDistinguishedName());
			boolean isExist = true;
			if(share == null) {
				share = Wi.copier.copy(wi);
				isExist = false;
			}else{
				share.setPassword(wi.getPassword());
				share.setShareUserList(wi.getShareUserList());
				share.setShareOrgList(wi.getShareOrgList());
				share.setShareType(wi.getShareType());
			}
			if("password".equals(wi.getShareType())){
				if(StringUtils.isBlank(share.getPassword())){
					throw new Exception("password can not be empty.");
				}
			}else{
				if((wi.getShareUserList()==null || wi.getShareUserList().isEmpty()) &&
						(wi.getShareOrgList()==null || wi.getShareOrgList().isEmpty())){
					throw new Exception("shareUserList or shareOrgList can not be empty.");
				}
			}
			Attachment2 attachment = emc.find(wi.getFileId(), Attachment2.class);
			if(attachment == null) {
				Folder2 folder = emc.find(wi.getFileId(), Folder2.class);
				if(folder==null){
					throw new ExceptionShareNotExist(wi.getFileId());
				}else{
					if (!effectivePerson.isManager() && !StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
						throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} access folder{id:" + wi.getFileId()
								+ "} denied.");
					}
					share.setFileType("folder");
					share.setName(folder.getName());
					share.setPerson(folder.getPerson());
				}
			}else{
				if (!effectivePerson.isManager() && !StringUtils.equals(attachment.getPerson(), effectivePerson.getDistinguishedName())) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} access att{id:" + wi.getFileId()
							+ "} denied.");
				}
				share.setFileType("attachment");
				share.setName(attachment.getName());
				share.setLength(attachment.getLength());
				share.setExtension(attachment.getExtension());
				share.setPerson(attachment.getPerson());
			}
			share.setLastUpdateTime(new Date());
			if(share.getValidTime()==null){
				share.setValidTime(DateTools.getDateAfterYearAdjust(new Date(),100,null,null));
			}

			emc.beginTransaction(Share.class);
			if(isExist){
				emc.check(share, CheckPersistType.all);
			}else {
				emc.persist(share, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(share.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Share {

		private static final long serialVersionUID = 3965042303681243568L;

		static WrapCopier<Wi, Share> copier = WrapCopierFactory.wi(Wi.class, Share.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
