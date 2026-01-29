package com.x.pan.assemble.control.jaxrs.folder2;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionSaveToZone extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSaveToZone.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String zoneFolder, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson.getDistinguishedName());
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Folder3 folder3 = emc.find(zoneFolder, Folder3.class);
			if (null == folder3) {
				throw new ExceptionEntityNotExist(zoneFolder, Folder3.class);
			}
			if(!business.zoneEditableToCreate(effectivePerson, folder3.getId())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Attachment2> attList = new ArrayList<>();
			if(ListTools.isNotEmpty(wi.getAttIdList())){
				List<Attachment2> attachments = emc.list(Attachment2.class, wi.getAttIdList());
				for(Attachment2 att : attachments){
					if (StringUtils.equals(att.getPerson(), effectivePerson.getDistinguishedName())) {
						attList.add(att);
					}
				}
			}
			List<Folder2> folderList = new ArrayList<>();
			if(ListTools.isNotEmpty(wi.getFolderIdList())){
				List<Folder2> folders = emc.list(Folder2.class, wi.getFolderIdList());
				for(Folder2 folder : folders){
					if (StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
						folderList.add(folder);
					}
				}
			}
			if (attList.isEmpty() && folderList.isEmpty()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			this.saveToZone(business, folder3, attList, folderList, effectivePerson.getDistinguishedName());

			Wo wo = new Wo();
			wo.setValue(true);

			return result;
		}
	}

	private void saveToZone(Business business, Folder3 superior,
							List<Attachment2> attList, List<Folder2> folderList, String person) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Attachment3> attachment3List = new ArrayList<>();
		for (Attachment2 att: attList){
			String fileName = business.attachment3().adjustFileName(superior.getId(), att.getName());
			Attachment3 attachment3 = new Attachment3(fileName, person,
					superior.getId(), att.getOriginFile(), att.getLength(), superior.getZoneId());
			attachment3List.add(attachment3);
		}

		List<Folder3> folder3List = new ArrayList<>();
		for (Folder2 folder : folderList){
			addSubFile(business, folder, superior, attachment3List, folder3List, person, true);
		}

		List<ZonePermission> permissionList = business.folder3().listZonePermission(superior.getId(), null);
		emc.beginTransaction(Folder3.class);
		emc.beginTransaction(ZonePermission.class);
		for (Folder3 folder3 : folder3List){
			emc.persist(folder3, CheckPersistType.all);
			for (ZonePermission pp : permissionList){
				ZonePermission zonePermission = new ZonePermission(pp.getName(), pp.getRole(),
						folder3.getId(), person);
				emc.persist(zonePermission, CheckPersistType.all);
			}
		}

		emc.beginTransaction(Attachment3.class);
		for (Attachment3 attachment3 : attachment3List){
			emc.persist(attachment3, CheckPersistType.all);
		}
		emc.commit();
	}

	private void addSubFile(Business business, Folder2 folder, Folder3 superior,
							List<Attachment3> attachment3List, List<Folder3> folder3List, String person, boolean flag) throws Exception{
		String fileName = folder.getName();
		if(flag){
			fileName = business.folder3().adjustFileName(superior.getId(), folder.getName());
		}
		Folder3 folder3 = new Folder3(fileName, person, superior.getId(), superior.getZoneId());
		folder3List.add(folder3);

		List<Attachment2> attList = business.attachment2().listWithFolder2(folder.getId(), FileStatusEnum.VALID.getName());
		for (Attachment2 att: attList){
			Attachment3 attachment3 = new Attachment3(att.getName(), person,
					folder3.getId(), att.getOriginFile(), att.getLength(), superior.getZoneId());
			attachment3List.add(attachment3);
		}

		List<Folder2> folderList = business.folder2().listSubDirect1(folder.getId(), FileStatusEnum.VALID.getName());
		for (Folder2 subFolder : folderList){
			addSubFile(business, subFolder, folder3, attachment3List, folder3List, person, false);
		}
	}

	public static class Wi extends GsonPropertyObject{

		@FieldDescribe("附件ID列表.")
		private List<String> attIdList;

		@FieldDescribe("目录ID列表.")
		private List<String> folderIdList;

		public List<String> getAttIdList() {
			return attIdList;
		}

		public void setAttIdList(List<String> attIdList) {
			this.attIdList = attIdList;
		}

		public List<String> getFolderIdList() {
			return folderIdList;
		}

		public void setFolderIdList(List<String> folderIdList) {
			this.folderIdList = folderIdList;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
