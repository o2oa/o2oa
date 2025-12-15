package com.x.pan.assemble.control.jaxrs.zone;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;
import com.x.pan.core.entity.ZoneRoleEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 删除共享区目录权限
 * @author sword
 */
class ActionDeletePermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionDeletePermission.class );
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final Business business = new Business(emc);
			if (ListTools.isEmpty(wi.getZonePermissionList())) {
				throw new ExceptionZonePermissionEmpty();
			}
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				boolean isZoneAdmin = business.folder3().isZoneAdmin(folder.getId(), effectivePerson.getDistinguishedName());
				if(!isZoneAdmin){
					throw new ExceptionAccessDenied(effectivePerson.getName());
				}
			}
			logger.info("{}操作共享区目录【{}|{}】的权限删除:{}", effectivePerson.getDistinguishedName(), folder.getName(), id, gson.toJson(wi.getZonePermissionList()));
			final Map<String, List<ZonePermission>> map = wi.getZonePermissionList().stream()
					.filter(zp -> StringUtils.isNotBlank(zp.getName()) && ZoneRoleEnum.getByValue(zp.getRole()) != null)
					.collect(Collectors.groupingBy(ZonePermission::getRole));
			if(!map.isEmpty()) {
				List<ZonePermission> deleteList = new ArrayList<>();
				List<ZonePermission> addList = new ArrayList<>();
				for (String role : map.keySet()){
					List<String> nameList = ListTools.extractField(map.get(role), ZonePermission.name_FIELDNAME, String.class, true, true);
					this.joinPermission(role, nameList, business, effectivePerson.getDistinguishedName(), folder, deleteList, addList);
				}
				emc.beginTransaction(ZonePermission.class);
				emc.beginTransaction(Folder3.class);
				for (ZonePermission zonePermission : deleteList) {
					emc.remove(zonePermission);
				}
				folder.setHasSetPermission(true);
				emc.commit();

				emc.beginTransaction(ZonePermission.class);
				for (ZonePermission zonePermission : addList) {
					emc.persist(zonePermission, CheckPersistType.all);
				}
				emc.commit();
				CacheManager.notify(ZonePermission.class);
			}
			wo.setValue(true);
			result.setData(wo);
			return result;

		}
	}

	/**
	 * 如果管理权限全部被删除则重新继承父类权限，读者和编辑者权限允许被清空
	 * 权限变更同时需要递归变更子类未曾修改过的权限，忽略已修改过权限的子类
	 * @param role
	 * @param nameList
	 * @param business
	 * @param operator
	 * @param folder
	 * @param deleteList
	 * @param addList
	 * @throws Exception
	 */
	private void joinPermission(String role, List<String> nameList, Business business, String operator,
						Folder3 folder, List<ZonePermission> deleteList, List<ZonePermission> addList) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		List<ZonePermission> zonePermissionList = emc.listEqualAndIn(ZonePermission.class,
				ZonePermission.zoneId_FIELDNAME, folder.getId(), ZonePermission.name_FIELDNAME, nameList);
		deleteList.addAll(zonePermissionList);
		long count = emc.countEqualAndEqual(ZonePermission.class,
				ZonePermission.zoneId_FIELDNAME, folder.getId(), ZonePermission.role_FIELDNAME, role);
		List<ZonePermission> parentPermissionList = new ArrayList<>();

		boolean deleteAllFlag = false;
		if(count == zonePermissionList.size()){
			deleteAllFlag = true;
			if(role.equals(ZoneRoleEnum.ADMIN.getValue()) && !folder.getSuperior().equals(Business.TOP_FOLD)){
				parentPermissionList = business.folder3().listZonePermission(folder.getSuperior(), role);;
				for (ZonePermission pp : parentPermissionList){
					ZonePermission zonePermission = new ZonePermission(pp.getName(), pp.getRole(),
							folder.getId(), operator);
					addList.add(zonePermission);
				}
			}
		}
		List<Folder3> subList = business.folder3().listNotSetPermissionSubDirect(folder.getId());
		for(Folder3 subFolder : subList){
			this.joinSubPermission(role, nameList, business, operator, subFolder, deleteList, addList, deleteAllFlag, parentPermissionList);
		}
	}

	private void joinSubPermission(String role, List<String> nameList, Business business, String operator,
						Folder3 folder, List<ZonePermission> deleteList, List<ZonePermission> addList,
						boolean deleteAllFlag, List<ZonePermission> parentPermissionList) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		if(deleteAllFlag){
			List<ZonePermission> zonePermissionList = business.folder3().listZonePermission(folder.getId(), role);
			deleteList.addAll(zonePermissionList);
			for (ZonePermission pp : parentPermissionList){
				ZonePermission zonePermission = new ZonePermission(pp.getName(), pp.getRole(),
						folder.getId(), operator);
				addList.add(zonePermission);
			}
		}else{
			List<ZonePermission> zonePermissionList = emc.listEqualAndIn(ZonePermission.class,
					ZonePermission.zoneId_FIELDNAME, folder.getId(), ZonePermission.name_FIELDNAME, nameList);
			deleteList.addAll(zonePermissionList);
		}
		List<Folder3> subList = business.folder3().listNotSetPermissionSubDirect(folder.getId());
		for(Folder3 subFolder : subList){
			this.joinSubPermission(role, nameList, business, operator, subFolder, deleteList, addList, deleteAllFlag, parentPermissionList);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("权限列表：[{'name':'人员、组织或群组名称','role':'角色：admin|editor|reader'}]")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.pan.core.entity.ZonePermission",
				fieldValue = "[{name='',role='']", fieldSample = "[{'name':'人员、组织或群组名称','role':'角色：admin|editor|reader'}]")
		private List<ZonePermission> zonePermissionList;

		public List<ZonePermission> getZonePermissionList() {
			return zonePermissionList;
		}

		public void setZonePermissionList(List<ZonePermission> zonePermissionList) {
			this.zonePermissionList = zonePermissionList;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
