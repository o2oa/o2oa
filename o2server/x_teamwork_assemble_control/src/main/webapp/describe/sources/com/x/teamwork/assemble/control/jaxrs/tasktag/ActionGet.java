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
import com.x.teamwork.core.entity.TaskTag;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		TaskTag taskTag = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new TaskTagIdForQueryEmptyException();
			result.error( exception );
		}

		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = taskTagCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {
			if (check) {
				try {
					taskTag = taskTagQueryService.get(id);
					if ( taskTag == null) {
						check = false;
						Exception exception = new TaskTagNotExistsException(id);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskTagQueryException(e, "根据指定flag查询应用工作任务标签信息对象时发生异常。flag:" + id);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					wo = Wo.copier.copy( taskTag );
					taskTagCache.put(new Element(cacheKey, wo));
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new TaskTagQueryException(e, "将查询出来的应用工作任务标签信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
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
}