package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.file.assemble.control.wrapout.WrapOutFile;
import com.x.file.core.entity.open.File;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<File, WrapOutFile> copier = BeanCopyToolsBuilder.create(File.class, WrapOutFile.class, null,
			WrapOutFile.Excludes);

}
