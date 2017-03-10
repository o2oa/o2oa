package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.service.OkrAttachmentFileInfoService;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.entity.OkrAttachmentFileInfo;

public class ExcuteBase {
	
	protected BeanCopyTools<OkrAttachmentFileInfo, WrapOutOkrAttachmentFileInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrAttachmentFileInfo.class, WrapOutOkrAttachmentFileInfo.class, null, WrapOutOkrAttachmentFileInfo.Excludes);
	protected OkrAttachmentFileInfoService okrAttachmentFileInfoService = new OkrAttachmentFileInfoService();
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
}
