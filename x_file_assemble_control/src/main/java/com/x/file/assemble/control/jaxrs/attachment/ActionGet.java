package com.x.file.assemble.control.jaxrs.attachment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.core.entity.Attachment;

public class ActionGet {

	private BeanCopyTools<Attachment, WrapOutAttachment> copier = BeanCopyToolsBuilder.create(Attachment.class,
			WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

	public ActionResult<WrapOutAttachment> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAttachment> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getName(), attachment.getPerson())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} access attachment{id:" + id + "} denied.");
			}
			WrapOutAttachment wrap = copier.copy(attachment);
			wrap.setContentType(Config.mimeTypes().getContentType(wrap.getName()));
			result.setData(wrap);
			return result;
		}
	}
}
