package com.x.attendance.assemble.control.jaxrs.v2.file;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecordFile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionUpload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(AttendanceV2CheckInRecordFile.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMapping();
			}
			String fileName = this.fileName(disposition);
			fileName = FilenameUtils.getName(fileName);
			this.verifyConstraint(fileName);
			AttendanceV2CheckInRecordFile file = new AttendanceV2CheckInRecordFile(mapping.getName(), fileName,
					effectivePerson.getDistinguishedName());
			emc.check(file, CheckPersistType.all);
			file.saveContent(mapping, in, fileName);
			emc.beginTransaction(AttendanceV2CheckInRecordFile.class);
			emc.persist(file);
			emc.commit();
			wo.setId(file.getId());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		public Wo() {
		}

		public Wo(String id) throws Exception {
			super(id);
		}
	}
}
