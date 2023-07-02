package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;

public class ActionListWhatICanViewData extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWhatICanViewData.class);

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
			isXAdmin = userManagerService.isManager( effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, isAnonymous, isXAdmin );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			if (check) {
				try {
					wos_out = listViewAbleAppInfoByPermission( personName, isAnonymous, null,  "all", "数据", isXAdmin, 1000 );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAppInfoProcess(e, "系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if( ListTools.isNotEmpty( wos_out )){
				for( Wo wo : wos_out ) {
					if( ListTools.isNotEmpty( wo.getWrapOutCategoryList() )) {

						try {
							wo.setConfig( appInfoServiceAdv.getConfigJson( wo.getId() ) );
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionAppInfoProcess(e, "系统根据ID查询栏目配置支持信息时发生异常。ID=" + wo.getId() );
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}

						wos.add( wo );
					}
				}
				//按appInfoSeq列的值， 排个序
				SortTools.asc( wos, "appInfoSeq");
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData( wos );
			}
		}
		return result;
	}
}