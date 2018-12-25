package com.x.report.assemble.control.dataadapter.workflow;

import java.io.UnsupportedEncodingException;

import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.WrapOutId;
import com.x.report.assemble.control.ThisApplication;

/**
 * 启动工作流的服务
 * 
 * @author O2LEE
 */
public class WorkFlowDelete {
	
	/**
	 * 删除一个流程实例，并且返回工作流实例work的ID
	 * @param workId	 
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * 
	 * http://dev.o2oa.io:20020/x_processplatform_assemble_surface/jaxrs/work/{workId}
	 * DELETE
	 */
	public String deleteProcessInstance( String workId ) throws UnsupportedEncodingException, Exception {
		ActionResponse resp = null;
		WrapOutId wrapOutId = null;
		try {
			resp = ThisApplication.context().applications().deleteQuery(
					x_processplatform_assemble_surface.class, "work/" + workId
			);
			wrapOutId = resp.getData( WrapOutId.class );
		}catch( Exception e ) {
			try {
				resp = ThisApplication.context().applications().deleteQuery(
						x_processplatform_assemble_surface.class, "workcompleted/" + workId + "/delete/manage"
				);
				wrapOutId = resp.getData( WrapOutId.class );
			}catch( Exception e1 ) {
				e.printStackTrace();
			}
		}
		if( wrapOutId != null ) {
			return wrapOutId.getId();
		}else {
			return "";
		}
	}
}
