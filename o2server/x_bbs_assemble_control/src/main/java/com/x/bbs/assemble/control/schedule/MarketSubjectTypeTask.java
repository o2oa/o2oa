package com.x.bbs.assemble.control.schedule;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_program_center;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.entity.BBSSectionInfo;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;


/**
 * 定时代理，从应用市场同步所有的应用名称，存入论坛板块"应用市场"字段subjectType
 * 
 * @author LJ
 *
 */
public class MarketSubjectTypeTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(MarketSubjectTypeTask.class);
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private static final Gson gson = new Gson();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			List<String> applicationList = this.getAllApplicationList();
			logger.info("MarketSubjectTypeTask completed and applicationList:"+applicationList);
			if(ListTools.isNotEmpty(applicationList)){
				BBSSectionInfo bBSSectionInfo = sectionInfoServiceAdv.getMainSectionBySectionName("应用市场");
				bBSSectionInfo.setSubjectTypeList(applicationList);
				sectionInfoServiceAdv.save(bBSSectionInfo);
			}

		} catch (Exception e) {
			logger.warn("MarketSubjectTypeTask got an exception.");
			logger.error(e);
		}
	}

	private List<String> getAllApplicationList() throws Exception {
		List<String> applicationList = new ArrayList<>();
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_program_center.class, "market/list/paging/1/size/1000","{}");
		JsonObject resJson =  gson.fromJson(resp.toJson(),JsonObject.class);
		if((!resJson.isJsonNull()) && resJson.has("type") && StringUtils.equals("success",resJson.get("type").getAsString())){
			JsonArray appArry = resJson.getAsJsonArray("data");
			if(appArry.isJsonArray() && appArry.size()>0){
				for(int i=0;i<appArry.size();i++){
					JsonObject applictionObj = appArry.get(i).getAsJsonObject();
					if((!applictionObj.isJsonNull()) && applictionObj.has("name")){
						if(StringUtils.isNotEmpty(applictionObj.get("name").getAsString())){
							applicationList.add(applictionObj.get("name").getAsString());
						}
					}
				}
			}
		}
		return  applicationList;
	}
}