package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CategoryInfo_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * 获取可管理的分类
 * @author sword
 */
public class ActionListManage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListManage.class );

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String appId) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		logger.debug(effectivePerson.getDistinguishedName());
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), appId, effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				AppInfo appInfo = emc.find(appId, AppInfo.class);
				if(appInfo == null){
					throw new ExceptionEntityNotExist(appId);
				}
				List<CategoryInfo> categoryInfoList;
				if(business.isManager(effectivePerson)){
					categoryInfoList = emc.listEqual(CategoryInfo.class, CategoryInfo.appId_FIELDNAME, appId);
				}else{
					List<String> unitList = business.organization().unit().listWithPersonSupNested(effectivePerson.getDistinguishedName());
					List<String> groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
					if(appInfoServiceAdv.isAppInfoManager(appInfo, effectivePerson.getDistinguishedName(), unitList, groupList)){
						categoryInfoList = emc.listEqual(CategoryInfo.class, CategoryInfo.appId_FIELDNAME, appId);
					}else{
						EntityManager em = emc.get(CategoryInfo.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<CategoryInfo> cq = cb.createQuery(CategoryInfo.class);
						Root<CategoryInfo> root = cq.from(CategoryInfo.class);
						Predicate qp = cb.equal(root.get(CategoryInfo_.appId), appId);
						Predicate p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(CategoryInfo_.manageablePersonList));
						if(ListTools.isNotEmpty(unitList)){
							p = cb.or(p, root.get(CategoryInfo_.manageableUnitList).in(unitList));
						}
						if(ListTools.isNotEmpty(groupList)){
							p = cb.or(p, root.get(CategoryInfo_.manageableGroupList).in(groupList));
						}
						qp = cb.and(qp, p);
						categoryInfoList = em.createQuery(cq.where(qp)).getResultList();
					}
				}
				List<Wo> wos = Wo.copier.copy( categoryInfoList );
				SortTools.asc( wos, "categoryName");
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData( wos );
			}
		}
		return result;
	}

	public static class Wo extends CategoryInfo {

		private static final long serialVersionUID = -1137384484063244073L;
		static final WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo( CategoryInfo.class, Wo.class,
				JpaObject.singularAttributeField(CategoryInfo.class, true, true), null);

	}
}
