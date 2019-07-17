package com.x.teamwork.assemble.control.jaxrs.dynamic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;

import net.sf.ehcache.Element;

public class ActionListNextWithTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithTask.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, String taskId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter  queryFilter = null;
		
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}
		
		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskIdForQueryEmptyException();
			result.error( exception );
		}
		
		if( check ) {
			queryFilter = new QueryFilter();
			queryFilter.addEqualsTerm( new EqualsTerm("taskId", taskId ));
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListNextWithTask", taskId  );
			element = dynamicCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {
					List<Dynamic>  dynamicList = null;
					long total = dynamicQueryService.countWithFilter( queryFilter );
					
					if( total > 0 ) {
						dynamicList = dynamicQueryService.listWithFilter( effectivePerson, count, flag, "createTime", "desc", queryFilter );			
					}else {
						total = 0;
					}
					
					if( ListTools.isNotEmpty( dynamicList )) {
						wos = Wo.copier.copy(dynamicList);
					}else {
						wos = new ArrayList<>();
					}					
					resultObject = new ResultObject( total, wos );
					dynamicCache.put(new Element( cacheKey, resultObject ));
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统根据工作任务查询工作动态信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}
	
	public static class Wo extends Dynamic {

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Dynamic, Wo> copier = WrapCopierFactory.wo( Dynamic.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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