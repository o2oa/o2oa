package com.x.pan.assemble.control.jaxrs.folder3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ActionSaveToPerson extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSaveToPerson.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String personFolder, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson.getDistinguishedName());
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Folder2 folder2;
			if(EMPTY_SYMBOL.equals(personFolder)){
				folder2 = new Folder2();
				folder2.setId(Business.TOP_FOLD);
				folder2.setPerson(effectivePerson.getDistinguishedName());
			}else{
				folder2 = emc.fetch(personFolder, Folder2.class);
			}
			if (null == folder2) {
				throw new ExceptionEntityNotExist(personFolder, Folder3.class);
			}
			if(!business.controlAble(effectivePerson) && !effectivePerson.getDistinguishedName().equals(folder2.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Set<String> zoneIdSet = new HashSet<>();
			List<Attachment3> attList = new ArrayList<>();
			if(ListTools.isNotEmpty(wi.getAttIdList())){
				List<Attachment3> attachments = emc.list(Attachment3.class, wi.getAttIdList());
				for(Attachment3 att : attachments){
					String zoneId = business.getSystemConfig().getReadPermissionDown() ? att.getFolder() : att.getZoneId();
					if(zoneIdSet.contains(zoneId)){
						attList.add(att);
					}else if (business.zoneReadable(effectivePerson, zoneId)) {
						attList.add(att);
						zoneIdSet.add(zoneId);
					}
				}
			}
			List<Folder3> folderList = new ArrayList<>();
			if(ListTools.isNotEmpty(wi.getFolderIdList())){
				List<Folder3> folders = emc.list(Folder3.class, wi.getFolderIdList());
				for(Folder3 folder : folders){
					String zoneId = business.getSystemConfig().getReadPermissionDown() ? folder.getId() : folder.getZoneId();
					if(zoneIdSet.contains(zoneId)){
						folderList.add(folder);
					}else if (business.zoneReadable(effectivePerson, zoneId)) {
						folderList.add(folder);
						zoneIdSet.add(zoneId);
					}
				}
			}
			if (attList.isEmpty() && folderList.isEmpty()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			this.saveToPerson(business, folder2, attList, folderList, effectivePerson);

			Wo wo = new Wo();
			wo.setValue(true);

			return result;
		}
	}

	private void saveToPerson(Business business, Folder2 superior,
							List<Attachment3> attList, List<Folder3> folderList, EffectivePerson effectivePerson) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		String person = effectivePerson.getDistinguishedName();
		long usedSize = business.attachment2().getUseCapacity(person);
		List<Attachment2> attachment2List = new ArrayList<>();
		for (Attachment3 att: attList){
			usedSize = usedSize + att.getLength();
			int vResult = business.verifyConstraint(person, usedSize);
			if (vResult > 0) {
				long usedCapacity = usedSize / (1024 * 1024);
				throw new ExceptionCapacityOut(usedCapacity, vResult);
			}
			String fileName = business.attachment2().adjustFileName(superior.getId(), att.getName());
			Attachment2 attachment2 = new Attachment2(fileName, person,
					superior.getId(), att.getOriginFile(), att.getLength(), att.getType());
			attachment2List.add(attachment2);
		}

		List<Folder2> folder2List = new ArrayList<>();
		for (Folder3 folder : folderList){
			addSubFile(business, folder, superior, attachment2List, folder2List, effectivePerson, usedSize, true);
		}

		emc.beginTransaction(Folder2.class);
		for (Folder2 folder2 : folder2List){
			emc.persist(folder2, CheckPersistType.all);
		}

		emc.beginTransaction(Attachment2.class);
		for (Attachment2 attachment2 : attachment2List){
			emc.persist(attachment2, CheckPersistType.all);
		}
		emc.commit();
	}

	private void addSubFile(Business business, Folder3 folder, Folder2 superior,
							List<Attachment2> attachment2List, List<Folder2> folder2List,
							EffectivePerson effectivePerson, long usedSize, boolean flag) throws Exception{
		String fileName = folder.getName();
		if(flag){
			fileName = business.folder2().adjustFileName(superior.getId(), folder.getName());
		}
		String person = effectivePerson.getDistinguishedName();
		Folder2 folder2 = new Folder2(fileName, person, superior.getId(), FileStatusEnum.VALID.getName());
		folder2List.add(folder2);

		List<Attachment3> attList = business.attachment3().listWithFolder2(folder.getId(), FileStatusEnum.VALID.getName());
		for (Attachment3 att: attList){
			usedSize = usedSize + att.getLength();
			int vResult = business.verifyConstraint(person, usedSize);
			if (vResult > 0) {
				long usedCapacity = usedSize / (1024 * 1024);
				throw new ExceptionCapacityOut(usedCapacity, vResult);
			}
			Attachment2 attachment2 = new Attachment2(att.getName(), person,
					folder2.getId(), att.getOriginFile(), att.getLength(), att.getType());
			attachment2List.add(attachment2);
		}

		List<Folder3> folderList = business.folder3().listSubDirectObjectPermission(folder.getId(), FileStatusEnum.VALID.getName(), effectivePerson);
		for (Folder3 subFolder : folderList){
			addSubFile(business, subFolder, folder2, attachment2List, folder2List, effectivePerson, usedSize, false);
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
