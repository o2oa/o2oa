package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.queue.DataImportStatus;

public class ActionQueryImportStatusWithName extends BaseAction {
	protected ActionResult<DataImportStatus> execute(HttpServletRequest request, EffectivePerson effectivePerson, String batchName ) throws Exception {
		ActionResult<DataImportStatus> result = new ActionResult<>();
		DataImportStatus dataImportStatus = ThisApplication.getDataImportStatus( batchName );
		result.setData(dataImportStatus);
		return result;
	}
}