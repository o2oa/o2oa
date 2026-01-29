package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

class ActionListWithFolder extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionListWithFolder.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, String orderBy, Boolean desc) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			final Business business = new Business(emc);
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}

			final boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? folder.getId() : folder.getZoneId();
				boolean isZoneReader = business.folder3().isZoneViewer(zoneId, effectivePerson.getDistinguishedName());
				if(!isZoneReader) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}

			List<Wo> wos = Wo.copier.copy(business.folder3().listSubDirectObjectPermission(folder.getId(), FileStatus.VALID.getName(), effectivePerson));
			wos.forEach(wo -> {
				try {
					setExtendInfo(business, wo, effectivePerson.getDistinguishedName(), isManager);
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
			});
			if(Wo.copier.getCopyFields().contains(orderBy)) {
				if (BooleanUtils.isTrue(desc)) {
					SortTools.desc(wos, false, orderBy);
				} else {
					SortTools.asc(wos, false, orderBy);
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends AbstractWoFolder {

		private static final long serialVersionUID = -3084093468867905220L;

		protected static WrapCopier<Folder3, Wo> copier = WrapCopierFactory.wo(Folder3.class, Wo.class,
				JpaObject.singularAttributeField(Folder3.class, true, true), ListTools.toList(Folder3.capacity_FIELDNAME));

	}

}
