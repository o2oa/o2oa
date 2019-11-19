package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.CategoryInfo;

public class ActionListCategoryInfoViewers extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListCategoryInfoViewers.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String categoryId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( categoryId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoQueryById( e, categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			Wo wo = new Wo();
			wo.setPersonList( categoryInfo.getViewablePersonList() );
			wo.setUnitList( categoryInfo.getViewableUnitList() );
			wo.setGroupList( categoryInfo.getViewableGroupList() );
			result.setData( wo );
		}
		return result;
	}

	public static class Wo{
		
		@FieldDescribe("人员列表")
		private List<String> personList;
		
		@FieldDescribe("组织列表")
		private List<String> unitList;
		
		@FieldDescribe("群组列表")
		private List<String> groupList;

		public List<String> getPersonList() {
			return personList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public List<String> getGroupList() {
			return groupList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}
	}
}