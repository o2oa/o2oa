package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.tools.filter.term.MemberTerm;

import net.sf.ehcache.Element;

public class ActionListStarNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListStarNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = null;
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter  queryFilter = null;
		List<String> queryProjectIds = new ArrayList<>();
		
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ) {
			if( wrapIn == null ) {
				wrapIn = new Wi();
			}
			queryFilter = wrapIn.getQueryFilter();
			if( StringUtils.isNotEmpty( wrapIn.getGroup() )) {
				//如果项目分组不为空，那么需要先根据项目分组来查询这个分组下的所有项目ID集合
				List<String> projectIds = projectGroupQueryService.listProjectIdByGroup( wrapIn.getGroup() );
				if( ListTools.isEmpty( projectIds )) {
					queryProjectIds.add("0000-0000-0000");
				}else {
					for( String projectId : projectIds ) {
						queryProjectIds.add( projectId );
					}
				}
				queryFilter.addInTerm( new InTerm("id", new ArrayList<Object>(queryProjectIds) ));
			}
			//标星人员里有该用户
			queryFilter.addMemberTerm( new MemberTerm("starPersonList", effectivePerson.getDistinguishedName() ));
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListStarNextWithFilter", effectivePerson.getDistinguishedName(), flag, count, 
					wrapIn.getOrderField(), wrapIn.getOrderType(), 	queryFilter.getContentSHA1() );
			element = projectCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {	
					//获取用户能查看的所有的项目信息ID列表，最多查询2000条数据
					List<String>  projectIds = projectQueryService.listAllViewableProjectIds( effectivePerson, 2000, queryFilter );
					if( ListTools.isNotEmpty( projectIds )) {
						//直接根据可见项目ID列表进行分页查询
						Long total = Long.parseLong( projectIds.size() + "" );										
						List<Project>  projectList = projectQueryService.listWithProjectIdFilter( count, flag, wrapIn.getOrderField(), wrapIn.getOrderType(), projectIds );
						
						if( ListTools.isNotEmpty( projectList )) {
							WrapOutControl control = null;
							for( Project project : projectList ) {
								Wo wo = Wo.copier.copy(project);
								wo.setDescription( projectQueryService.getDescription( project.getId()));
								if( wo.getStarPersonList().contains( effectivePerson.getDistinguishedName() )) {
									wo.setStar( true );
								}
								control = new WrapOutControl();
								if( effectivePerson.isManager() 
										|| effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson() )
										|| project.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )) {
									control.setManageAble( true );
									control.setEditAble( true );
								}
								wo.setControl(control);
								wos.add( wo );
							}
						}
						resultObject = new ResultObject( total, wos );
						projectCache.put(new Element( cacheKey, resultObject ));
						
						result.setCount( resultObject.getTotal() );
						result.setData( resultObject.getWos() );
					}
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询项目信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}

	public static class Wi extends WrapInQueryProject{
	}
	
	public static class Wo extends WrapOutProject {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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