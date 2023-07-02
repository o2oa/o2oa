package com.x.meeting.assemble.control.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionDownload extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Boolean stream) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class, ExceptionWhen.not_found);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(attachment.getMeeting());
			}
			if (BooleanUtils.isNotTrue(Config.meeting().getAnonymousAccessAttachment())) {
				if (!business.meetingReadAvailable(effectivePerson, meeting)) {
					throw new ExceptionMeetingAccessDenied(effectivePerson, meeting.getSubject());
				}
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			Wo wo = new Wo(attachment.readContent(mapping), this.contentType(stream, attachment.getName()),
					this.contentDisposition(stream, attachment.getName()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
