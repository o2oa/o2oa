package com.x.teamwork.assemble.control.jaxrs.extfield;

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
import com.x.teamwork.core.entity.ProjectExtFieldRele;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		ProjectExtFieldRele projectExtFieldRele = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new ProjectExtFieldReleFlagForQueryEmptyException();
			result.error( exception );
		}

		String cacheKey = ApplicationCache.concreteCacheKey( "ProjectExtFieldRele.Get." + id );
		Element element = projectExtFieldReleCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			
		} else {
			if (check) {
				try {
					projectExtFieldRele = projectExtFieldReleQueryService.get(id);
					if ( projectExtFieldRele == null) {
						check = false;
						Exception exception = new ProjectExtFieldReleNotExistsException(id);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectExtFieldReleQueryException( e, "根据指定id查询扩展属性关联信息对象时发生异常。flag:" + id );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					wo = Wo.copier.copy( projectExtFieldRele );
				} catch (Exception e) {
					Exception exception = new ProjectExtFieldReleQueryException(e, "将查询出来的扩展属性关联信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends ProjectExtFieldRele {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectExtFieldRele, Wo> copier = WrapCopierFactory.wo( ProjectExtFieldRele.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}