package com.x.meeting.assemble.control.jaxrs.attachment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionUpdateCallback extends BaseAction {

	public ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String id, String callback, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExistCallback(callback, id);
			}
			Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExistCallback(callback, attachment.getMeeting());
			}
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			try (InputStream input = new ByteArrayInputStream(bytes)) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				attachment.updateContent(mapping, input);
				attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				attachment.setLastUpdateTime(new Date());
				emc.beginTransaction(Attachment.class);
				emc.persist(attachment);
				emc.commit();
			}
			WoObject woObject = new WoObject();
			woObject.setId(attachment.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
	}

}