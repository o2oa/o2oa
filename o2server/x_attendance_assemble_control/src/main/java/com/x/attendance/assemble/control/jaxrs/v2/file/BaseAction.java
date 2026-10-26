package com.x.attendance.assemble.control.jaxrs.v2.file;

import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

	private static final List<String> accessFileTypeList = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp",
			"heic", "heif");

	protected void verifyConstraint(String fileName) throws ExceptionFileNameInvalid {
		if (!StringTools.isFileName(fileName)) {
			throw new ExceptionFileNameInvalid(fileName);
		} else {
			String fileType = FilenameUtils.getExtension(fileName);
			if (StringUtils.isBlank(fileType)) {
				throw new ExceptionFileNameInvalid(fileName);
			} else {
				fileType = fileType.toLowerCase();
			}
			if (!ListTools.contains(accessFileTypeList, fileType)) {
				throw new ExceptionFileNameInvalid(fileName);
			}
		}
	}
}
