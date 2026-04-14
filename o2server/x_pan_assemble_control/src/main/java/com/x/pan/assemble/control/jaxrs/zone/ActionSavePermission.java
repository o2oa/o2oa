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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 修改共享区目录权限
 * @author sword
 */
class ActionSavePermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionSavePermission.class );
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
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
			logger.info("{}操作共享区目录【{}|{}】的权限变更:{}", effectivePerson.getDistinguishedName(), folder.getName(), id, gson.toJson(wi.getZonePermissionList()));
			List<ZonePermission> zonePermissionList = wi.getZonePermissionList().stream()
					.filter(zp -> StringUtils.isNotBlank(zp.getName()) && ZoneRoleEnum.getByValue(zp.getRole()) != null)
					.collect(Collectors.toList());
			if(ListTools.isNotEmpty(zonePermissionList)) {
				List<ZonePermission> addZonePermissionList = new ArrayList<>();
				List<ZonePermission> updateZonePermissionList = new ArrayList<>();

				this.joinPermission(folder, zonePermissionList, business, effectivePerson.getDistinguishedName(), addZonePermissionList, updateZonePermissionList);
				emc.beginTransaction(Folder3.class);
				emc.beginTransaction(ZonePermission.class);
				for (ZonePermission zonePermission : addZonePermissionList) {
					emc.persist(zonePermission, CheckPersistType.all);
				}
				for (ZonePermission zonePermission : updateZonePermissionList) {
					emc.check(zonePermission, CheckPersistType.all);
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
	 * 权限变更同时需要递归变更子类未曾修改过的权限，忽略已修改过权限的子类
	 * @param folder
	 * @param zonePermissionList
	 * @param business
	 * @param operator
	 * @param addZonePermissionList
	 * @param updateZonePermissionList
	 * @throws Exception
	 */
	private void joinPermission(Folder3 folder, List<ZonePermission> zonePermissionList, Business business, String operator,
					List<ZonePermission> addZonePermissionList, List<ZonePermission> updateZonePermissionList) throws Exception{
		for (ZonePermission zp : zonePermissionList){
			ZonePermission zonePermission = business.folder3().getZonePermission(zp.getName(), folder.getId());
			if(zonePermission!=null){
				zonePermission.setRole(zp.getRole());
				zonePermission.setLastUpdatePerson(operator);
				updateZonePermissionList.add(zonePermission);
			}else{
				zonePermission = new ZonePermission(zp.getName(), zp.getRole(),
						folder.getId(), operator);
				addZonePermissionList.add(zonePermission);
			}
		}

		List<Folder3> list = business.folder3().listNotSetPermissionSubDirect(folder.getId());
		for(Folder3 subFolder : list){
			this.joinPermission(subFolder, zonePermissionList, business, operator, addZonePermissionList, updateZonePermissionList);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("权限列表：[{'name':'人员、组织或群组名称','role':'角色：admin|editor|reader|viewer'}]")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ZonePermission",
				fieldValue = "{name='',role=''}", fieldSample = "[{'name':'人员、组织或群组名称','role':'角色：admin|editor|reader|viewer'}]")
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
