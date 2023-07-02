package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sword
 */
public class ActionListAllAppType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllAppType.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		String personName = effectivePerson.getDistinguishedName();
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Business business = new Business(null);
		Boolean isManager = business.isManager( effectivePerson );

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, isAnonymous, isManager );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				List<String> unitNames = null;
				List<String> groupNames = null;
				if(!isManager && !isAnonymous) {
					unitNames = userManagerService.listUnitNamesWithPerson(personName);
					groupNames = userManagerService.listGroupNamesByPerson(personName);
				}
				List<String> appIds = business.getAppInfoFactory().listPeopleViewAppInfoIds(personName, unitNames, groupNames, "全部", isManager);
				if(ListTools.isNotEmpty(appIds)){
					final String noneType = "未分类";
					List<BaseAction.Wo> apps = emc.fetch(appIds, BaseAction.Wo.copier3);
					apps.stream().forEach(app -> {
						if(StringUtils.isBlank(app.getAppType())){
							app.setAppType(noneType);
						}
					});
					Map<String, List<BaseAction.Wo>> appMap = apps.stream().collect(Collectors.groupingBy(BaseAction.Wo::getAppType));
					List<String> appTypes = new ArrayList<>(appMap.keySet());
					SortTools.asc(appTypes);
					for(String appType : appTypes){
						if(!appType.equals(noneType)){
							wos.add( new Wo( appType, appMap.get(appType).size() ));
						}
					}
					if(appTypes.contains(noneType)){
						wos.add( new Wo( noneType, appMap.get(noneType).size() ));
					}
				}
				result.setData( wos );
				CacheManager.put(cacheCategory, cacheKey, wos);
			}
		}
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("栏目类别名称")
		private String appType;

		@FieldDescribe("栏目数量")
		private Integer count;

		public Wo( String _appType, Integer _count ) {
			this.appType = _appType;
			this.count = _count;
		}

		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
	}

}
