package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.element.QueryView_;

public class ActionListAll extends BaseAction {

	public ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<String> appInfoIds = null;
		List<String> queryViewIds = null;
		List<QueryView> queryViews = null;
		List<AppInfo> appInfos = null;
		List<WoQueryView> woQueryViews = null;
		List<Wo> wraps = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			appInfoIds = this.distinctAppInfoIdsWithQueryView( business );
			if( appInfoIds != null && !appInfoIds.isEmpty() ) {
				appInfos = appInfoServiceAdv.list( appInfoIds );
				if( appInfos != null && !appInfos.isEmpty() ) {
					wraps = Wo.copier.copy( appInfos );
					for( Wo wrap : wraps ) {
						queryViewIds = this.listQueryViewIdsWithAppInfoId( business, wrap.getId() );
						if( queryViewIds != null && !queryViewIds.isEmpty() ) {
							queryViews = business.entityManagerContainer().list( QueryView.class, queryViewIds );
							if( queryViews != null && !queryViews.isEmpty() ) {
								woQueryViews = WoQueryView.copier.copy( queryViews );
								SortTools.asc( woQueryViews, true, "name" );
								wrap.setQueryViews( woQueryViews );
							}
						}
					}
				}
				SortTools.asc( wraps, true, "appName" );
			}			
			result.setData(wraps);
			return result;
		}
	}

	/**
	 * 获取所有视图涉及的栏目信息ID列表
	 * @param business
	 * @return
	 * @throws Exception
	 */
	private List<String> distinctAppInfoIdsWithQueryView(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from( QueryView.class );
		cq.select(root.get( QueryView_.appId )).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private List<String> listQueryViewIdsWithAppInfoId( Business business, String appId ) throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.equal( root.get(QueryView_.appId), appId );
		cq.select(root.get(QueryView_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	public static class Wo extends AppInfo {

		private static final long serialVersionUID = 2886873983211744188L;
		
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		public static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo( AppInfo.class, Wo.class, null, JpaObject.FieldsInvisible );
		
		private List<WoQueryView> queryViews = null;

		public List<WoQueryView> getQueryViews() {
			return queryViews;
		}
		
		public void setQueryViews(List<WoQueryView> queryViews) {
			this.queryViews = queryViews;
		}		
	}
	
	public static class WoQueryView extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;
		
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		public static WrapCopier<QueryView, WoQueryView> copier = WrapCopierFactory.wo( QueryView.class, WoQueryView.class, null, JpaObject.FieldsInvisible);
	}
}