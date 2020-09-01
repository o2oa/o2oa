package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

public class ActionQueryPermissionReadDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryPermissionReadDocument.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String queryPerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		result.setData(wo);
		Document document = documentQueryService.view( id, effectivePerson );
		if(document == null){
			return result;
		}
		//匿名用户是否可读
		if(effectivePerson.isAnonymous()){
			AppInfo appInfo = appInfoServiceAdv.get( document.getAppId() );
			CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
			if(appInfo!=null && categoryInfo!=null){
				boolean flag = true;
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( ( ListTools.isNotEmpty( document.getReadPersonList() ) && !document.getReadPersonList().contains( "所有人" ) )
						|| ListTools.isNotEmpty( document.getReadUnitList() ) || ListTools.isNotEmpty( document.getReadGroupList() ) ) {
					flag = false;
				}
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( !appInfo.getAllPeopleView() ) {
					//栏目不可见
					flag = false;
				}
				//检查这个文档所在的栏目和分类是否都是全员可见
				if( !categoryInfo.getAllPeopleView() ) {
					//分类不可见
					flag = false;
				}
				wo.setValue(flag);
				return result;
			}
		}else{
			String personName = effectivePerson.getDistinguishedName();
			if(effectivePerson.isManager()){
				if(StringUtils.isNotEmpty(queryPerson)){
					Person person = userManagerService.getPerson(queryPerson);
					if(person!=null){
						personName = person.getDistinguishedName();
					}else{
						return result;
					}
				}else{
					wo.setValue(true);
					return result;
				}
			}
			List<String> unitNames = userManagerService.listUnitNamesWithPerson(personName);
			List<String> groupNames = userManagerService.listGroupNamesByPerson(personName);
			//是否是读者
			if(ListTools.contains(document.getReadPersonList(), getShortTargetFlag(personName)) ||
					ListTools.contains(document.getReadPersonList(), "所有人")){
				wo.setValue(true);
				return result;
			}
			for(String unitName : unitNames){
				if(ListTools.contains(document.getReadUnitList(), getShortTargetFlag(unitName))){
					wo.setValue(true);
					return result;
				}
			}
			for(String groupName : groupNames){
				if(ListTools.contains(document.getReadGroupList(), getShortTargetFlag(groupName))){
					wo.setValue(true);
					return result;
				}
			}
			//是否是作者
			if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
				if( document.getAuthorPersonList().contains( personName ) ) {
					wo.setValue(true);
					return result;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
				if( ListTools.containsAny( unitNames, document.getAuthorUnitList())) {
					wo.setValue(true);
					return result;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
				if( ListTools.containsAny( groupNames, document.getAuthorGroupList())) {
					wo.setValue(true);
					return result;
				}
			}
			//是否是分类的管理者
			CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
			if ( categoryInfoServiceAdv.isCategoryInfoManager( categoryInfo, personName, unitNames, groupNames )) {
				wo.setValue(true);
				return result;
			}
			//是否是栏目的管理者
			AppInfo appInfo = appInfoServiceAdv.get( document.getAppId() );
			if (appInfoServiceAdv.isAppInfoManager( appInfo, personName, unitNames, groupNames )) {
				wo.setValue(true);
				return result;
			}
		}
		return result;			
	}

	private String getShortTargetFlag(String distinguishedName) {
		String target = null;
		if( StringUtils.isNotEmpty( distinguishedName ) ){
			String[] array = distinguishedName.split("@");
			StringBuffer sb = new StringBuffer();
			if( array.length == 3 ){
				target = sb.append(array[1]).append("@").append(array[2]).toString();
			}else if( array.length == 2 ){
				//2段
				target = sb.append(array[0]).append("@").append(array[1]).toString();
			}else{
				target = array[0];
			}
		}
		return target;
	}

	public static class Wo extends WrapBoolean {
		
	}

}