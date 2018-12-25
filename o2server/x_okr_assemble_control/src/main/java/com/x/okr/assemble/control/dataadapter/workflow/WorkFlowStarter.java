package com.x.okr.assemble.control.dataadapter.workflow;

import java.io.UnsupportedEncodingException;

import com.google.gson.JsonElement;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.WrapOutId;
import com.x.okr.assemble.control.ThisApplication;

/**
 * 启动工作流的服务
 * 
 * @author O2LEE
 */
public class WorkFlowStarter {
	
	/**
	 * 启动一个工作流程，并且返回工作流实例work的ID
	 * @param data
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * 
	 * http://dev.o2oa.io:20020/x_processplatform_service_processing/jaxrs/work
	 * POST
	 */
	public String start( JsonElement data ) throws UnsupportedEncodingException, Exception {
		ActionResponse resp = ThisApplication.context().applications().postQuery(
				x_processplatform_service_processing.class, "work",  data
		);
		WrapOutId wrapOutId = resp.getData( WrapOutId.class );
		return wrapOutId.getId();
	}
}
