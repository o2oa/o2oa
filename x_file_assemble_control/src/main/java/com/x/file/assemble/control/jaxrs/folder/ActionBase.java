package com.x.file.assemble.control.jaxrs.folder;

import java.util.List;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutFolder;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

public class ActionBase {

	protected static BeanCopyTools<Folder, WrapOutFolder> copier = BeanCopyToolsBuilder.create(Folder.class,
			WrapOutFolder.class);

	protected void setCount(Business business, WrapOutFolder wrap) throws Exception {
		List<String> ids = business.attachment().listWithFolder(wrap.getId());
		long count = 0;
		long size = 0;
		for (Attachment o : business.entityManagerContainer().fetchAttribute(ids, Attachment.class, "length")) {
			count++;
			size += o.getLength();
		}
		wrap.setAttachmentCount(count);
		wrap.setSize(size);
		wrap.setFolderCount(business.folder().countSubDirect(wrap.getId()));
	}

}
