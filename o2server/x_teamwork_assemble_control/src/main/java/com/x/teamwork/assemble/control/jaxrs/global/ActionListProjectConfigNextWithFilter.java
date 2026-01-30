package com.x.teamwork.assemble.control.jaxrs.global;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import com.x.teamwork.core.entity.ProjectConfig;

public class ActionListProjectConfigNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListProjectConfigNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = null;
		Boolean check = true;
		QueryFilter  queryFilter = null;
		
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectConfigQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( Boolean.TRUE.equals( check ) ){
			if( wrapIn == null ) {
				wrapIn = new Wi();
			}
			queryFilter = wrapIn.getQueryFilter();
		}
		
		if( Boolean.TRUE.equals( check ) ){
			/*采用缓存
			 * cacheKey = ApplicationCache.concreteCacheKey( "ActionListNextWithFilter", effectivePerson.getDistinguishedName(), flag, count, 
					wrapIn.getOrderField(), wrapIn.getOrderType(), 	queryFilter.getContentSHA1() );
			element = projectCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {*/
				try {
					//获取用户能查看的所有的项目信息ID列表，最多查询2000条数据
					List<String>  projectConfigIds = projectConfigQueryService.listAllProjectConfigIds( effectivePerson, 2000, queryFilter );
					if( ListTools.isNotEmpty( projectConfigIds )) {
						//直接根据可见项目ID列表进行分页查询
						Long total = Long.parseLong( projectConfigIds.size() + "" );										
						List<ProjectConfig>  projectConfigList = projectConfigQueryService.listWithProjectConfigIdFilter( count, flag, wrapIn.getOrderField(), wrapIn.getOrderType(), projectConfigIds );
						
						if( ListTools.isNotEmpty( projectConfigList )) {
							for( ProjectConfig projectConfig : projectConfigList ) {
								Wo wo = Wo.copier.copy(projectConfig);
								wos.add( wo );
							}
						}
						resultObject = new ResultObject( total, wos );
						//projectCache.put(new Element( cacheKey, resultObject ));
						
						result.setCount( resultObject.getTotal() );
						result.setData( resultObject.getWos() );
					}
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询项目配置信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			//}		
		}
		return result;
	}

	public static class Wi extends WrapInQueryProjectConfig{
		
	}
	
	public static class Wo extends ProjectConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectConfig, Wo> copier = WrapCopierFactory.wo( ProjectConfig.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class ResultObject {

		private Long total;
		
		private List<Wo> wos;

		public ResultObject() {}
		
		public ResultObject(Long count, List<Wo> data) {
			this.total = count;
			this.wos = data;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}
}