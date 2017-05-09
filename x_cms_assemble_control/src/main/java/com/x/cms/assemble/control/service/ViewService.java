package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.view.WrapInView;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.WrapInViewFieldConfig;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ViewService {

	public View save( EntityManagerContainer emc, WrapInView wrapIn ) throws Exception {
		View view = null;
		ViewFieldConfig fieldConfig = null;
		List<String> ids = null;
		List<String> fieldIds = new ArrayList<>();
		Business business = new Business( emc );
		if( wrapIn.getId() == null ){
			wrapIn.setId( View.createId() );
		}
		
		emc.beginTransaction( ViewFieldConfig.class );
		emc.beginTransaction( View.class);
		//先保存所有的列信息
		ids = business.getViewFieldConfigFactory().listByViewId( wrapIn.getId() );
		if( ids != null && !ids.isEmpty() ){
			//说明原来有列配置，先删除掉
			for( String id : ids ){
				fieldConfig = business.getViewFieldConfigFactory().get(id);
				if( fieldConfig != null ){
					emc.remove( fieldConfig, CheckRemoveType.all );
				}
			}
		}
		if( wrapIn.getFields() != null && !wrapIn.getFields().isEmpty() ){
			for( WrapInViewFieldConfig wrapInField : wrapIn.getFields() ){
				wrapInField.setViewId( wrapIn.getId() );
				//查询是否已经存在
				fieldConfig = business.getViewFieldConfigFactory().get( wrapInField.getId() );
				if( fieldConfig == null ){//新增
					fieldConfig = new ViewFieldConfig();					
					WrapTools.viewFieldConfig_wrapin_copier.copy( wrapInField, fieldConfig );
					if( wrapInField.getId() != null && !wrapInField.getId().isEmpty() ){
						fieldConfig.setId( wrapInField.getId() );
					}
					emc.persist( fieldConfig, CheckPersistType.all );
				}else{//更新
					WrapTools.viewFieldConfig_wrapin_copier.copy( wrapInField, fieldConfig );
					emc.check( fieldConfig, CheckPersistType.all );
				}
				fieldIds.add( fieldConfig.getId() );
			}
		}
		//再保存视图信息
		view = emc.find( wrapIn.getId(), View.class );
		if( view == null ){
			view = new View();
			view.setId( wrapIn.getId() );
			view.setFieldConfigList( fieldIds );
			WrapTools.view_wrapin_copier.copy( wrapIn, view );
			emc.persist( view, CheckPersistType.all);
		}else{
			WrapTools.view_wrapin_copier.copy( wrapIn, view );
			view.setFieldConfigList( fieldIds );
			emc.check( view, CheckPersistType.all );	
		}
		emc.commit();
		return view;
	}
	
	public View get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		return emc.find(id, View.class );
	}

	public List<Document> nextPageDocuemntView(EntityManagerContainer emc, String id, Integer count, List<String> viewAbleDocIds, Map<String, Object> condition) throws Exception {
		Business business = new Business(emc);
		return business.getViewFactory().nextPageDocuemntView( id, count, viewAbleDocIds, condition );
	}

	public List<String> listFieldConfigByView(EntityManagerContainer emc, String viewId) throws Exception {
		Business business = new Business(emc);
		return business.getViewFieldConfigFactory().listByViewId( viewId );
	}

	public ViewFieldConfig getFieldConfig(EntityManagerContainer emc, String fieldId) throws Exception {
		Business business = new Business(emc);
		return business.getViewFieldConfigFactory().get( fieldId );
	}

}
