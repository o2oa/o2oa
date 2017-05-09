package com.x.cms.assemble.control.service;

import java.util.List;
import java.util.Map;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.jaxrs.view.WrapInView;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ViewServiceAdv {
	
	private ViewService viewService = new ViewService();
	
	public View save( WrapInView wrapIn,  EffectivePerson currentPerson ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn for save is null!");
		}
		View view = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			view = viewService.save( emc, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return view;
	}
	
	public View get( String id ) throws Exception {
		if( id == null ){
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return viewService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> nextPageDocuemntView(String id, Integer count, List<String> viewAbleDocIds, Map<String, Object> condition) throws Exception {
		if( viewAbleDocIds == null ){
			throw new Exception("viewAbleDocIds is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return viewService.nextPageDocuemntView( emc, id, count, viewAbleDocIds, condition );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listFieldConfigByView(String viewId ) throws Exception {
		if( viewId == null ){
			throw new Exception("viewId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return viewService.listFieldConfigByView( emc, viewId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void deleteFieldConfig(String fieldId) throws Exception {
		if( fieldId == null ){
			throw new Exception("fieldId is null!");
		}
		ViewFieldConfig fieldConfig = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			fieldConfig = viewService.getFieldConfig( emc, fieldId );
			if( fieldConfig != null ){
				emc.beginTransaction( ViewFieldConfig.class );
				emc.remove( fieldConfig, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
}
