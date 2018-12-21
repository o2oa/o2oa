package com.x.okr.assemble.control.dataadapter.workflow;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.ThisApplication;

/**
 * 为工作流添加阅读权限
 * 
 * @author O2LEE
 */
public class WorkFlowReaderAdder {
	
	/**
	 * 为工作流添加阅读权限
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public void add( String wf_workId, List<String> identities ) throws UnsupportedEncodingException, Exception {
		if( ListTools.isEmpty(identities )) {
			return;
		}
		JsonParser jsonParser = new JsonParser();
		String json = "{";
		json  +=            "\"notify\": false,";
		json  +=            "\"identityList\":[";
		for(int i=0; i< identities.size() ; i++ ) {
			if(i == 0 ) {
				json  +=           "'"+ identities.get(i) +"'";
			}else {
				json  +=           ", '"+ identities.get(i) +"'";
			}
		}
		json +=              "]";
		json +=         "}";
		/**
		 * {
		 * 		"notify": false,
		 *  		"identityList":[
		 *  					'叶洪@84f71856-6447-4714-b616-64949e6e21c0@I', 
		 *  					'金飞@50ab9f6f-6690-4972-ae50-c5c105316481@I', 
		 *  					'金飞@50ab9f6f-6690-4972-ae50-c5c105316452@I', 
		 *  					'昌威@0c5e3d66-3271-4815-b7b4-9be2370ce35ef@I', 
		 *  					'张剑@c59fc8f0-e04c-4c92-92a9-42321c57883f@I'
		 * 		]
		 *  }
		 */
		JsonElement data = jsonParser.parse( json ); // 将json字符串转换成JsonElement
		ActionResponse resp = ThisApplication.context().applications().postQuery(
				x_processplatform_assemble_surface.class, "read/work/" + wf_workId ,  data
		);
		return ;
	}
}
