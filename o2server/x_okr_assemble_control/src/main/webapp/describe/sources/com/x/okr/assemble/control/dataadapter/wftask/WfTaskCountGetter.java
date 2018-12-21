package com.x.okr.assemble.control.dataadapter.wftask;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrConfigSystemService;

/**
 * 获取指定用户所有的待办数量信息
 * 
 * @author O2LEE
 */
public class WfTaskCountGetter {
	
	public static OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	
	public Long countWithProcess(String person) throws Exception {
		String APPRAISE_WORKFLOW_ID = null;
		
		try {
			APPRAISE_WORKFLOW_ID = okrConfigSystemService.getValueWithConfigCode("APPRAISE_WORKFLOW_ID");
		} catch (Exception e) {
			System.out.println("获取流程ID参数发生异常");
			e.printStackTrace();
		}
		
		if( StringUtils.isNotEmpty(APPRAISE_WORKFLOW_ID)) {
			JsonParser jsonParser = new JsonParser();
			String json = "{";
			json  +=            "\"credentialList\":[";
			json  +=              "'"+person+"'";
			json +=              "],";
			json  +=            "\"appliationList\":[],";
			json  +=            "\"processList\":[";
			json  +=              "'"+APPRAISE_WORKFLOW_ID+"'";
			json +=              "]";
			json +=         "}";
			
			String serviceUri = "task/count/filter";
			JsonElement data = jsonParser.parse( json ); // 将json字符串转换成JsonElement
			ActionResponse resp = ThisApplication.context().applications().postQuery(
					x_processplatform_assemble_surface.class, serviceUri, data
			);
			
			Wo wo = resp.getData( Wo.class );
//			if( wo != null ){
//				System.out.println(">>>>>>>>>>>>>>获取到用户的待办（TASK）数量：" + wo.count + ",  指定流程ID：" + APPRAISE_WORKFLOW_ID );
//			}
			return wo.count;
		}
		return 0L;
	}
	
	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("待办数量")
		private Long count = 0L;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}
}
