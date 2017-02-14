package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.core.entity.Attachment;

class ActionBase extends StandardJaxrsAction {
	protected BeanCopyTools<Attachment, WrapOutAttachment> copier = BeanCopyToolsBuilder.create(Attachment.class,
			WrapOutAttachment.class, null, WrapOutAttachment.Excludes);
	protected static String[] ImageExtensions = new String[] { "jpg", "png", "bmp", "gif" };
}
