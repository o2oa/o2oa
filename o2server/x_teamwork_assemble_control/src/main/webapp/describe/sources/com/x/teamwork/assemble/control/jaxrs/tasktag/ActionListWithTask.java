package com.x.teamwork.assemble.control.jaxrs.tasktag;

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
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;

import net.sf.ehcache.Element;

public class ActionListWithTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithTask.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String taskId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = null;;
		Boolean check = true;
		Task task = null;
		String cacheKey = null;
		Element element = null;
		
		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskIdEmptyException();
			result.error( exception );
		}
		
		if (check) {
			try {
				task = taskQueryService.get( taskId );
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException( taskId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagPersistException(e, "根据ID查询任务信息对象时发生异常。ID:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListWithTask", effectivePerson.getDistinguishedName(), taskId );
			element = taskTagCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {			
					List<TaskTag>  taskTagList = taskTagQueryService.listWithTaskAndPerson( effectivePerson, task );
					if( ListTools.isNotEmpty( taskTagList )) {
						wos = Wo.copier.copy( taskTagList );
					}
					if( wos == null ) {
						wos = new ArrayList<>();
					}else {
						SortTools.desc(  wos, "createTime" );
					}
					
					resultObject = new ResultObject( Long.parseLong( wos.size()+"" ), wos );
					taskTagCache.put(new Element( cacheKey, resultObject ));					
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询工作任务标签信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}
	
	public static class Wo extends TaskTag {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTag, Wo> copier = WrapCopierFactory.wo( TaskTag.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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