package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.core.entity.CategoryInfo;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<CategoryInfo> categoryInfoList = null;
		Boolean check = true;		

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass() );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try {
				categoryInfoList = categoryInfoServiceAdv.listAll();
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "查询所有分类信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				if( categoryInfoList != null && !categoryInfoList.isEmpty() ){
					try {
						wos = Wo.copier.copy( categoryInfoList );
						for(Wo wo : wos) {
							wo.setExtContent( categoryInfoServiceAdv.getExtContentWithId( wo.getId() ));
						}
						SortTools.desc( wos, "categorySeq");
						CacheManager.put(cacheCategory, cacheKey, wos);
						result.setData(wos);
					} catch ( Exception e ) {
						check = false;
						Exception exception = new ExceptionCategoryInfoProcess( e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		return result;
	}
	
	public static class Wo extends CategoryInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo( CategoryInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}
	}
}