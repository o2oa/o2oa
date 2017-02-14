package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.core.entity.content.Attachment;

abstract class ActionBase {
	protected static BeanCopyTools<Attachment, WrapOutAttachment> attachmentOutCopier = BeanCopyToolsBuilder
			.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);
}
