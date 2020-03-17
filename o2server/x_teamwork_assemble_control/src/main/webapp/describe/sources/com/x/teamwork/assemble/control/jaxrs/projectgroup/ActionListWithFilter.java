package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.ProjectGroup;

import net.sf.ehcache.Element;

public class ActionListWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWithFilter.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<ProjectGroup> projectGroups = null;
		List<Wo> wos = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectGroupQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( getStringListHeyx( wi.getIds()) );
		Element element = projectGroupCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData( wos );
		} else {
			if (check) {
				try {
					projectGroups = projectGroupQueryService.list( wi.getIds() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectGroupQueryException(e, "系统通过指定的ID列表查询项目组信息列表时发生异常。JSON:" + jsonElement.toString() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				
			}
			
			if (check) {
				if( ListTools.isNotEmpty( projectGroups )) {
					wos = Wo.copier.copy( projectGroups );					
					SortTools.asc( wos, "createTime");					
					projectGroupCache.put(new Element(cacheKey, wos));
					result.setData(wos);
				}	
			}
		}
		return result;
	}	

	public static class Wi {

		@FieldDescribe("项目组ID列表")
		private List<String> ids;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
	}

	public static class Wo extends ProjectGroup {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectGroup, Wo> copier = WrapCopierFactory.wo( ProjectGroup.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
	
	private String getStringListHeyx( List<String> list ) {
		StringBuffer content = new StringBuffer();
		if( ListTools.isNotEmpty( list )) {
			SortTools.asc( list );
			for( String str : list ) {
				content.append( str );
			}
			return DigestUtils.sha1Hex(content.toString() );
		}
		return "null";
	}
	
}