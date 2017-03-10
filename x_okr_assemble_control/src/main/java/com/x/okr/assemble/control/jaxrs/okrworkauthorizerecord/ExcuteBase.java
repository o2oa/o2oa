package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.entity.OkrWorkAuthorizeRecord;

public class ExcuteBase {

	protected BeanCopyTools<OkrWorkAuthorizeRecord, WrapOutOkrWorkAuthorizeRecord> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkAuthorizeRecord.class, WrapOutOkrWorkAuthorizeRecord.class, null, WrapOutOkrWorkAuthorizeRecord.Excludes);
	protected OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	
}
