package com.x.attendance.assemble.control.jaxrs.v2.file;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecordFile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;

class ActionDownload extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean stream) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			AttendanceV2CheckInRecordFile file = emc.find(id, AttendanceV2CheckInRecordFile.class);
			if (null == file) {
				file = emc.flag(id, AttendanceV2CheckInRecordFile.class);
				if (null == file) {
					throw new ExceptionEntityNotExist(id);
				}
			}
			if (effectivePerson.isNotManager() && !effectivePerson.getDistinguishedName().equals(file.getCreator())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings()
					.get(AttendanceV2CheckInRecordFile.class, file.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageMappingNotExisted(file.getStorage());
			}
			byte[] bs = file.readContent(mapping);
			String fastETag = file.getId() + file.getUpdateTime().getTime();
			Wo wo = new Wo(bs, this.contentType(stream, file.getName()), this.contentDisposition(stream, file.getName()),
					fastETag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
			super(bytes, contentType, contentDisposition, fastETag);
		}
	}
}
