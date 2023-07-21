package com.x.file.assemble.control.jaxrs.folder2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;

class ActionDownload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} access folder{id:" + id
						+ "} denied.");
			}
			String zipName = folder.getName() + ".zip";
			List<Folder2> folderList = new ArrayList<>();
			folderList.add(folder);
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				this.fileCommonService.downToZip(emc, null, folderList, os);
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