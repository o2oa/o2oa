package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;
import com.x.pan.core.entity.ZoneRoleEnum;

import java.util.List;
import java.util.stream.Collectors;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? folder.getId() : folder.getZoneId();
				boolean isZoneReader = business.folder3().isZoneViewer(zoneId, effectivePerson.getDistinguishedName());
				if(!isZoneReader) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			Wo wo = Wo.copier.copy(folder);
			setExtendInfo(business, wo, effectivePerson.getDistinguishedName(), business.controlAble(effectivePerson));
			boolean isZoneAdmin = business.folder3().isZoneAdmin(folder.getId(), effectivePerson.getDistinguishedName());
			if(isManager || isZoneAdmin) {
				List<WoZonePermission> zonePermissionList = emc.fetchEqual(ZonePermission.class, WoZonePermission.copier, ZonePermission.zoneId_FIELDNAME, folder.getId());
				if(!Business.TOP_FOLD.equals(folder.getSuperior()) && !business.getSystemConfig().getReadPermissionDown()) {
					zonePermissionList = zonePermissionList.stream().filter(z -> !z.getRole().equals(ZoneRoleEnum.READER.getValue())).collect(Collectors.toList());
				}
				wo.setZonePermissionList(zonePermissionList);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends AbstractWoFolder {

		private static final long serialVersionUID = -6018639030781453539L;

		protected static WrapCopier<Folder3, Wo> copier = WrapCopierFactory.wo(Folder3.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("权限列表")
		private List<WoZonePermission> zonePermissionList;

		public List<WoZonePermission> getZonePermissionList() {
			return zonePermissionList;
		}

		public void setZonePermissionList(List<WoZonePermission> zonePermissionList) {
			this.zonePermissionList = zonePermissionList;
		}
	}

	public static class WoZonePermission extends ZonePermission {

		protected static WrapCopier<ZonePermission, WoZonePermission> copier = WrapCopierFactory.wo(ZonePermission.class, WoZonePermission.class,
				JpaObject.singularAttributeField(ZonePermission.class, true, true), ListTools.toList(Folder3.capacity_FIELDNAME));
	}

}
