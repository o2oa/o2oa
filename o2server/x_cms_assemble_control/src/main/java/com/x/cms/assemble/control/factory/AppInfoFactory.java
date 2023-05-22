package com.x.cms.assemble.control.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfo_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;


/**
 * 应用信息表基础功能服务类
 *
 * @author O2LEE
 */
public class AppInfoFactory extends AbstractFactory {

	public AppInfoFactory(Business business) throws Exception {
		super(business);
	}

	public AppInfo get(String id) throws Exception {
		return this.entityManagerContainer().find(id, AppInfo.class, ExceptionWhen.none);
	}

	public AppInfo flag(String flag) throws Exception {
		return this.entityManagerContainer().flag(flag, AppInfo.class );
	}

	public List<String> listAllIds(String documentType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			Predicate p = cb.equal(root.get(AppInfo_.documentType), documentType);
			return em.createQuery(cq.where(p)).getResultList();
		}
		return em.createQuery(cq).getResultList();
	}

	public List<String> listByAppType(String appType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		if (StringUtils.isNotEmpty(appType) && !"全部".equals(appType) && !"all".equalsIgnoreCase(appType)) {
			Predicate p = cb.equal(root.get(AppInfo_.appType), appType);
			return em.createQuery(cq.where(p)).getResultList();
		}
		return em.createQuery(cq).getResultList();
	}

	public List<AppInfo> listAll( String appType, String documentType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery(AppInfo.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = null;
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo_.documentType), documentType) );
		}
		if (StringUtils.isNotEmpty(appType) &&!StringUtils.equals( "未分类",appType )) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo_.appType), appType) );
		}
		if (StringUtils.isNotEmpty(appType) &&StringUtils.equals( "未分类",appType )) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					CriteriaBuilderTools.predicate_or(
							cb, cb.isNull(root.get(AppInfo_.appType)),
							CriteriaBuilderTools.predicate_or(
									cb, cb.equal(root.get(AppInfo_.appType), ""),
									cb.equal(root.get(AppInfo_.appType), "未分类")
							)
					)
			);
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listLike(String keyStr) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.like(root.get(AppInfo_.appName), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(AppInfo_.appAlias), str + "%", '\\'));
		cq.select(root.get(AppInfo_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	public List<String> listAllPeoplePublishAppInfoIds(String documentType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		Predicate p = cb.isTrue(root.get(AppInfo_.allPeoplePublish));
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listPeoplePublishAppInfoIds(String personName, List<String> unitNames, List<String> groupNames, Boolean isManager) throws Exception {
		if(BooleanUtils.isTrue(isManager)){
			return listAllIds("全部");
		}
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		Predicate p = cb.isTrue(root.get(AppInfo_.allPeoplePublish));
		if (StringUtils.isNotEmpty(personName)) {
			p = cb.or(p, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
			p = cb.or(p, cb.isMember(personName, root.get(AppInfo_.manageablePersonList)));
		}
		if(ListTools.isNotEmpty(unitNames)){
			p = cb.or(p, root.get(AppInfo_.publishableUnitList).in(unitNames));
			p = cb.or(p, root.get(AppInfo_.manageableUnitList).in(unitNames));
		}
		if(ListTools.isNotEmpty(groupNames)){
			p = cb.or(p, root.get(AppInfo_.publishableGroupList).in(groupNames));
			p = cb.or(p, root.get(AppInfo_.manageableGroupList).in(groupNames));
		}
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public List<String> listPeopleViewAppInfoIds(String personName, List<String> unitNames, List<String> groupNames, String appType, Boolean isManager) throws Exception {
		if(BooleanUtils.isTrue(isManager)){
			return listByAppType(appType);
		}
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		Predicate p = cb.disjunction();
		if (StringUtils.isNotEmpty(appType) && !"全部".equals(appType) && !"all".equalsIgnoreCase(appType)) {
			p = cb.or(p, cb.and(cb.isTrue(root.get(AppInfo_.allPeopleView)), cb.equal(root.get(AppInfo_.appType), appType)));
		}else{
			p = cb.or(p, cb.isTrue(root.get(AppInfo_.allPeopleView)));
		}
		if (StringUtils.isNotEmpty(personName)) {
			p = cb.or(p, cb.isMember(personName, root.get(AppInfo_.viewablePersonList)));
			p = cb.or(p, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
			p = cb.or(p, cb.isMember(personName, root.get(AppInfo_.manageablePersonList)));
		}
		if(ListTools.isNotEmpty(unitNames)){
			p = cb.or(p, root.get(AppInfo_.viewableUnitList).in(unitNames));
			p = cb.or(p, root.get(AppInfo_.publishableUnitList).in(unitNames));
			p = cb.or(p, root.get(AppInfo_.manageableUnitList).in(unitNames));
		}
		if(ListTools.isNotEmpty(groupNames)){
			p = cb.or(p, root.get(AppInfo_.viewableGroupList).in(groupNames));
			p = cb.or(p, root.get(AppInfo_.publishableGroupList).in(groupNames));
			p = cb.or(p, root.get(AppInfo_.manageableGroupList).in(groupNames));
		}
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 查询所有未设置可见权限的AppInfo的ID列表( with List copy ) 全员可发布的栏目也包含在内：判断allPeopleView or
	 * allPeoplePublish
	 *
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllPeopleViewAppInfoIds(String documentType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		Predicate p = cb.isTrue(root.get(AppInfo_.allPeopleView));
		p = cb.or(p, cb.isTrue(root.get(AppInfo_.allPeoplePublish)));
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByAppName(String appName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal(root.get(AppInfo_.appName), appName);
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByAppAlias(String appAlias) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal(root.get(AppInfo_.appAlias), appAlias);
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<AppInfo> listAppInfoByAppAlias(List<String> appAliases) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery(AppInfo.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = root.get(AppInfo_.appAlias).in(appAliases);
		p = cb.or(p, root.get(AppInfo_.appName).in(appAliases));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public <T extends AppInfo> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(AppInfo::getAppAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(AppInfo::getAppName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	/**
	 * 查询指定用户可以管理的所有栏目ID列表
	 *
	 * @param personName
	 * @param groupNames
	 * @param unitNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableAppIdsByPerson(String personName, List<String> unitNames, List<String> groupNames,
			String documentType, Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember(personName, root.get(AppInfo_.manageablePersonList));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.manageableUnitList).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.manageableGroupList).in(groupNames));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p_permission = cb.equal(p_permission, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p_permission)).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 根据权限查询用户可以发布文档的栏目ID列表（检测allPeoplePublish ）
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 *            - 栏目ID的最大范围
	 * @param excludAppInfoIds
	 *            - 需要排队的栏目ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableAppInfoIds(String personName, List<String> unitNames, List<String> groupNames,
			List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType, Integer maxCount)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p_filter = root.get(AppInfo_.id).in(inAppInfoIds);
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			if (p_filter == null) {
				p_filter = cb.not(root.get(AppInfo_.id).in(excludAppInfoIds));
			} else {
				p_filter = cb.and(p_filter, cb.not(root.get(AppInfo_.id).in(excludAppInfoIds)));
			}
		}

		Predicate p_permission = null;
		p_permission = cb.isTrue(root.get(AppInfo_.allPeoplePublish));

		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.manageablePersonList)));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableUnitList).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableGroupList).in(groupNames));
		}

		// 使用新的条件将两个条件组合起来
		if (p_filter != null) {
			p = p_filter;
		}
		if (p != null) {
			if (p_permission != null) {
				p = cb.and(p, p_permission);
			}
		} else {
			if (p_permission != null) {
				p = p_permission;
			}
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 根据权限查询用户可以发布文档的栏目ID列表（不检测allPeopleView 和 allPeoplePublish ）
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 *            - 栏目ID的最大范围
	 * @param excludAppInfoIds
	 *            - 需要排队的栏目ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableAppIdsInPermission(String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType,
			Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p_filter = root.get(AppInfo_.id).in(inAppInfoIds);
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			if (p_filter == null) {
				p_filter = cb.not(root.get(AppInfo_.id).in(excludAppInfoIds));
			} else {
				p_filter = cb.and(p_filter, cb.not(root.get(AppInfo_.id).in(excludAppInfoIds)));
			}
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember(personName, root.get(AppInfo_.manageablePersonList));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableUnitList).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableGroupList).in(groupNames));
		}

		// 使用新的条件将两个条件组合起来
		if (p_filter != null) {
			p = p_filter;
		}
		if (p != null) {
			if (p_permission != null) {
				p = cb.and(p, p_permission);
			}
		} else {
			if (p_permission != null) {
				p = p_permission;
			}
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 查询用户有权限访问的所有栏目ID列表（检测allPeopleView 和 allPeoplePublish ）
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableAppInfoIds(String personName, List<String> unitNames, List<String> groupNames,
			List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType, Integer maxCount)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p_filter = root.get(AppInfo_.id).in(inAppInfoIds);
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			if (p_filter == null) {
				p_filter = cb.not(root.get(AppInfo_.id).in(excludAppInfoIds));
			} else {
				p_filter = cb.and(p_filter, cb.not(root.get(AppInfo_.id).in(excludAppInfoIds)));
			}
		}

		Predicate p_permission = null;
		p_permission = cb.isTrue(root.get(AppInfo_.allPeopleView));
		p_permission = cb.or(p_permission, cb.isTrue(root.get(AppInfo_.allPeoplePublish)));
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.manageablePersonList)));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.viewablePersonList)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableUnitList).in(unitNames));
			p_permission = cb.or(p_permission, root.get(AppInfo_.viewableUnitList).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableGroupList).in(groupNames));
			p_permission = cb.or(p_permission, root.get(AppInfo_.viewableGroupList).in(groupNames));
		}

		// 使用新的条件将两个条件组合起来
		if (p_filter != null) {
			p = p_filter;
		}
		if (p != null) {
			if (p_permission != null) {
				p = cb.and(p, p_permission);
			}
		} else {
			if (p_permission != null) {
				p = p_permission;
			}
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 查询用户有权限访问的所有栏目ID列表（不检测allPeopleView）
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableAppIdsInPermission(String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType,
			Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p_filter = root.get(AppInfo_.id).in(inAppInfoIds);
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			if (p_filter == null) {
				p_filter = cb.not(root.get(AppInfo_.id).in(excludAppInfoIds));
			} else {
				p_filter = cb.and(p_filter, cb.not(root.get(AppInfo_.id).in(excludAppInfoIds)));
			}
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember(personName, root.get(AppInfo_.manageablePersonList));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.publishablePersonList)));
			p_permission = cb.or(p_permission, cb.isMember(personName, root.get(AppInfo_.viewablePersonList)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableUnitList).in(unitNames));
			p_permission = cb.or(p_permission, root.get(AppInfo_.viewableUnitList).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo_.publishableGroupList).in(groupNames));
			p_permission = cb.or(p_permission, root.get(AppInfo_.viewableGroupList).in(groupNames));
		}

		// 使用新的条件将两个条件组合起来
		if (p_filter != null) {
			p = p_filter;
		}
		if (p != null) {
			if (p_permission != null) {
				p = cb.and(p, p_permission);
			}
		} else {
			if (p_permission != null) {
				p = p_permission;
			}
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo_.documentType), documentType));
		}
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	public AppInfo pick(String flag) throws Exception {
		AppInfo o = this.business().entityManagerContainer().flag(flag, AppInfo.class);
		if (o != null) {
			this.entityManagerContainer().get(AppInfo.class).detach(o);
		}
		return o;
	}

	public List<String> listAllAppType() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.isNotNull( root.get(AppInfo_.appType));
		cq.select(root.get(AppInfo_.appType));
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

//	public Long countAppInfoWithAppType(String type) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<AppInfo> root = cq.from(AppInfo.class);
//		Predicate p = cb.equal( root.get(AppInfo_.appType), type );
//		cq.select(cb.count(root));
//		return em.createQuery(cq.where(p)).getSingleResult();
//	}
//
//	public Long countAppInfoWithOutAppType() throws Exception {
//		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<AppInfo> root = cq.from(AppInfo.class);
//		Predicate p = cb.isNull( root.get(AppInfo_.appType) );
//		p = cb.or( p, cb.equal( root.get(AppInfo_.appType), ""));
//		p = cb.or( p, cb.equal( root.get(AppInfo_.appType), "未分类"));
//		cq.select(cb.count(root));
//		return em.createQuery(cq.where(p)).getSingleResult();
//	}

	public List<String> listAppIdsWithAppType(String type) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal( root.get(AppInfo_.appType), type );
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAppIdsWithOutAppType() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.isNull( root.get(AppInfo_.appType) );
		p = cb.or( p, cb.equal( root.get(AppInfo_.appType), ""));
		p = cb.or( p, cb.equal( root.get(AppInfo_.appType), "未分类"));
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
