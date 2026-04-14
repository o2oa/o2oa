package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.pan.assemble.control.Business;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.pan.core.entity.Folder3;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

class ActionDownload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			String zoneId = business.getSystemConfig().getReadPermissionDown() ? folder.getId() : folder.getZoneId();
			if (!business.zoneReadable(effectivePerson, zoneId)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			String zipName = folder.getName() + ".zip";
			List<Folder3> folderList = new ArrayList<>();
			folderList.add(folder);
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				this.fileCommonService.downToZip2(emc, effectivePerson,null, folderList, os);
				byte[] bs = os.toByteArray();
				Wo wo = new Wo(bs, this.contentType(false,zipName),
						this.contentDisposition(false, zipName));
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
