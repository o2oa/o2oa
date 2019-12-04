package com.x.program.center.jaxrs.center;

import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class ActionMetricsReport extends BaseAction {

	ActionResult<WrapBoolean> execute( String serverName, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapBoolean> result = new ActionResult<>();
		WrapBoolean wrapBoolean = new WrapBoolean();

		try {
			if( jsonElement != null ) {
				Map<String , LinkedTreeMap> reportMap = this.convertToWrapIn( jsonElement, Map.class );
				//直接放到内存中
				Map<String , LinkedTreeMap> serverMap = ThisApplication.metricsReportMap.get(serverName );

				if( serverMap == null ){
					serverMap = new HashMap<String,LinkedTreeMap >();
				}

				//遍历Server上所有应用的监控数据，检查本次汇报的所有数据是否全部存在
				Set set = reportMap.keySet();
				Iterator it = set.iterator();
				String key = null;
				while( it.hasNext() ){
					key = (String) it.next();
					//看看是否已经存在
					serverMap.put( key, reportMap.get(key));
				}

				ThisApplication.metricsReportMap.put( serverName, serverMap );
				wrapBoolean.setValue( true );
			}
		} catch (Exception e ) {
			result.error(e);
			e.printStackTrace();
		}
		result.setData(wrapBoolean);
		return result;
	}
}