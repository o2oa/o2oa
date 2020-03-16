package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

/**
 * 对列表视图信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class ViewServiceAdv {
	
	private ViewService viewService = new ViewService();
	
	public View save( View wrapIn,  EffectivePerson effectivePerson, List<ViewFieldConfig> fields ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn for save is null!");
		}
		View view = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			view = viewService.save( emc, effectivePerson, wrapIn, fields );
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

//	public List<Document> nextPageDocuemntView(String id, Integer count, List<String> viewAbleDocIds, Map<String, Object> condition) throws Exception {
//		if( viewAbleDocIds == null ){
//			throw new Exception("viewAbleDocIds is null!");
//		}
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			return viewService.nextPageDocuemntView( emc, id, count, viewAbleDocIds, condition );
//		} catch ( Exception e ) {
//			throw e;
//		}
//	}

	public List<String> listFieldConfigIdsByView(String viewId ) throws Exception {
		if( viewId == null ){
			throw new Exception("viewId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return viewService.listFieldConfigByView( emc, viewId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<ViewFieldConfig> listFieldConfigByView(String viewId ) throws Exception {
		if( viewId == null ){
			throw new Exception("viewId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> ids = viewService.listFieldConfigByView( emc, viewId );
			if(ListTools.isNotEmpty( ids )) {
				return emc.list(ViewFieldConfig.class, ids);
//				return viewService.listFieldConfig(emc, ids );
			}else {
				return null;
			}			
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

	public List<String> listViewIdsWithCategoryId(String categoryId) throws Exception {
		if( categoryId == null ){
			throw new Exception("categoryId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return viewService.listViewIdsWithCategoryId( emc, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
