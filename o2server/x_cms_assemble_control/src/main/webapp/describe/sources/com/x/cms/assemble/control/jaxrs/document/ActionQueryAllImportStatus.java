package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.queue.DataImportStatus;

public class ActionQueryAllImportStatus extends BaseAction {
	protected ActionResult<List<DataImportStatus>> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<DataImportStatus>> result = new ActionResult<>();
		Map<String, DataImportStatus>  status = ThisApplication.listImportStatus();
		List<DataImportStatus> statusList = new ArrayList<>();
		
		Set<String> set = status.keySet();
		DataImportStatus dataImportStatus = null;
		Iterator<String> iterator = set.iterator();
		String key = null;
		while( iterator.hasNext() ) {
			key = (String) iterator.next();
			dataImportStatus = status.get( key );
			
			statusList.add( new DataImportStatus(
					dataImportStatus.getBatchName(), 
					dataImportStatus.getDataTotal(), 
					dataImportStatus.getProcessTotal(), 
					dataImportStatus.getSuccessTotal(), 
					dataImportStatus.getErrorTotal(), 
					null, 
					null
				));
		}		
		result.setData(statusList);
		return result;
	}
}