package com.x.cms.assemble.control.service;

import java.util.List;
import java.util.Map;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.view.WrapInView;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.View;

public class ViewService {

	public View save( EntityManagerContainer emc, WrapInView wrapIn ) throws Exception {
		View view = null;
		if( wrapIn.getId() == null ){
			wrapIn.setId( View.createId() );
		}
		view = emc.find( wrapIn.getId(), View.class );
		emc.beginTransaction( View.class );
		if( view == null ){
			view = new View();
			WrapTools.view_wrapin_copier.copy( wrapIn, view );
			emc.persist( view, CheckPersistType.all);
		}else{
			WrapTools.view_wrapin_copier.copy( wrapIn, view );
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

}
