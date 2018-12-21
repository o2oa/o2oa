package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSSectionInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;

public class BaseAction extends StandardJaxrsAction {
	protected BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	protected BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
}
