package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;

import net.sf.ehcache.Element;

public class ActionListWhatICanViewAllDocType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWhatICanViewAllDocType.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		List<Wo> wos_out = new ArrayList<>();
		Boolean isXAdmin = false;
		Boolean check = true;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		
		try {
			isXAdmin = userManagerService.isManager(request, effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey(personName, "AllType", isXAdmin);
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData(wos);
		} else {
			if (check) {
				if (isXAdmin) {
					try {
						wos = listViewAbleAppInfoByPermission( personName, isAnonymous, null,  "all", "全部", isXAdmin, 1000 );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e, "系统查询所有可见的分类信息时发生异常[管理员]。Name:" + personName);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					try {
						wos_out = listViewAbleAppInfoByPermission( personName, isAnonymous, null,  "all", "全部", isXAdmin, 1000 );
						for( Wo wo : wos_out ) {
							if( ListTools.isNotEmpty( wo.getWrapOutCategoryList() )) {
								wos.add( wo );
							}
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e, "系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + personName);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		if(ListTools.isNotEmpty( wos)) {
			SortTools.asc( wos, "appInfoSeq");
		}
		cache.put(new Element( cacheKey, wos ));
		result.setData(wos);
		return result;
	}
}