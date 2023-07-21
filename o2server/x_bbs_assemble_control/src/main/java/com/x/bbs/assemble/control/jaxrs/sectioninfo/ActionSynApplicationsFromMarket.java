package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionSynApplicationsFromMarket extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSynApplicationsFromMarket.class );

	//从应用市场同步所有的应用名称，存入论坛板块"应用市场"字段subjectTypeList
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		try {
			List<String> applicationList = this.getAllApplicationList();
			logger.info("ActionSynApplicationsFromMarket applicationList:"+applicationList);
			if(ListTools.isNotEmpty(applicationList)){
				BBSSectionInfo bBSSectionInfo = sectionInfoServiceAdv.getMainSectionBySectionName("应用市场");
				bBSSectionInfo.setSubjectTypeList(applicationList);
				sectionInfoServiceAdv.save(bBSSectionInfo);
				CacheManager.notify(BBSSectionInfo.class);
				wrap = Wo.copier.copy( bBSSectionInfo );
				result.setData(wrap);
			}

		} catch (Exception e) {
			logger.warn("getAllApplicationList got an exception.");
			logger.error(e);
		}



		return result;
	}
	private List<String> getAllApplicationList() throws Exception {
		List<String> applicationList = new ArrayList<>();
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_program_center.class, "market/list/paging/1/size/1000","{}");
		JsonObject resJson =  gson.fromJson(resp.toJson(),JsonObject.class);
		if((!resJson.isJsonNull()) && resJson.has("type") && StringUtils.equals("success",resJson.get("type").getAsString())){
			JsonArray appArry = resJson.getAsJsonArray("data");
			if(appArry.isJsonArray() && appArry.size()>0){
				logger.info("ActionSynApplicationsFromMarket appArry:"+appArry.size());
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
	public static class Wo extends BBSSectionInfo{
		public static WrapCopier< BBSSectionInfo, Wo> copier = WrapCopierFactory.wo( BBSSectionInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}