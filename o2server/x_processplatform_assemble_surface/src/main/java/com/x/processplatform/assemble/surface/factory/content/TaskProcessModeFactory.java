package com.x.processplatform.assemble.surface.factory.content;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import com.x.processplatform.core.entity.content.TaskProcessMode_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TaskProcessModeFactory extends AbstractFactory {

	public TaskProcessModeFactory(Business business) throws Exception {
		super(business);
	}

	public TaskProcessMode getMode(String person, TaskProcessMode wi) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskProcessMode.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskProcessMode> cq = cb.createQuery(TaskProcessMode.class);
		Root<TaskProcessMode> root = cq.from(TaskProcessMode.class);
		Predicate p = cb.equal(root.get(TaskProcessMode_.person), person);
		p = cb.and(p, cb.equal(root.get(TaskProcessMode_.process), wi.getProcess()));
		p = cb.and(p, cb.equal(root.get(TaskProcessMode_.activity), wi.getActivity()));
		p = cb.and(p, cb.equal(root.get(TaskProcessMode_.routeId), wi.getRouteId()));
		cq.select(root).where(p);
		List<TaskProcessMode> os = em.createQuery(cq).setMaxResults(1).getResultList();
		if(os.size() > 0){
			return os.get(0);
		}
		//兼容流程版本升级出现的节点id和路由id不同的影响
		if(StringUtils.isNotBlank(wi.getActivityAlias()) || StringUtils.isNotBlank(wi.getActivityName()) || StringUtils.isNotBlank(wi.getRouteName())){
			p = cb.equal(root.get(TaskProcessMode_.person), person);
			p = cb.and(p, cb.equal(root.get(TaskProcessMode_.process), wi.getProcess()));
			Predicate activityP = cb.equal(root.get(TaskProcessMode_.activity), wi.getActivity());
			if(StringUtils.isNotBlank(wi.getActivityAlias())){
				activityP = cb.or(activityP, cb.equal(root.get(TaskProcessMode_.activityAlias), wi.getActivityAlias()));
			}
			if(StringUtils.isNotBlank(wi.getActivityName())){
				activityP = cb.or(activityP, cb.equal(root.get(TaskProcessMode_.activityName), wi.getActivityName()));
			}
			p = cb.and(p, activityP);
			Predicate routeP = cb.equal(root.get(TaskProcessMode_.routeId), wi.getRouteId());
			if(StringUtils.isNotBlank(wi.getRouteName())){
				routeP = cb.or(routeP, cb.equal(root.get(TaskProcessMode_.routeName), wi.getRouteName()));
			}
			p = cb.and(p, routeP);
			cq.select(root).where(p);
			os = em.createQuery(cq).getResultList();
			if(os.size() > 0){
				if(os.size() == 1){
					return os.get(0);
				}
				if(StringUtils.isNotBlank(wi.getRouteName())) {
					Optional<TaskProcessMode> optional = os.stream().filter(o -> o.getActivity().equals(wi.getActivity()) && wi.getRouteName().equals(o.getRouteName())).findFirst();
					if (optional.isPresent()) {
						return optional.get();
					}
				}
				if(StringUtils.isNotBlank(wi.getActivityAlias())){
					Optional<TaskProcessMode> optional = os.stream().filter(o -> {
						return wi.getActivityAlias().equals(o.getActivityAlias()) && (wi.getRouteId().equals(o.getRouteId()) || (StringUtils.isNotBlank(o.getRouteName()) && o.getRouteName().equals(wi.getRouteName())));
					}).findFirst();
					if (optional.isPresent()) {
						return optional.get();
					}
				}
				if(StringUtils.isNotBlank(wi.getActivityName())){
					Optional<TaskProcessMode> optional = os.stream().filter(o -> {
						return wi.getActivityName().equals(o.getActivityName()) && (wi.getRouteId().equals(o.getRouteId()) || (StringUtils.isNotBlank(o.getRouteName()) && o.getRouteName().equals(wi.getRouteName())));
					}).findFirst();
					if (optional.isPresent()) {
						return optional.get();
					}
				}
			}
		}
		return null;
	}

	public List<TaskProcessMode> listMode(String person, TaskProcessMode wi) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskProcessMode.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskProcessMode> cq = cb.createQuery(TaskProcessMode.class);
		Root<TaskProcessMode> root = cq.from(TaskProcessMode.class);
		Predicate p = cb.equal(root.get(TaskProcessMode_.person), person);
		p = cb.and(p, cb.equal(root.get(TaskProcessMode_.process), wi.getProcess()));
		p = cb.and(p, cb.equal(root.get(TaskProcessMode_.activity), wi.getActivity()));
		cq.select(root).where(p);
		List<TaskProcessMode> os = em.createQuery(cq).getResultList();
		if(os.size() > 0){
			return os;
		}
		if(StringUtils.isNotBlank(wi.getActivityAlias()) || StringUtils.isNotBlank(wi.getActivityName())){
			p = cb.equal(root.get(TaskProcessMode_.person), person);
			p = cb.and(p, cb.equal(root.get(TaskProcessMode_.process), wi.getProcess()));
			Predicate activityP = cb.equal(root.get(TaskProcessMode_.activity), wi.getActivity());
			if(StringUtils.isNotBlank(wi.getActivityAlias())){
				activityP = cb.or(activityP, cb.equal(root.get(TaskProcessMode_.activityAlias), wi.getActivityAlias()));
			}
			if(StringUtils.isNotBlank(wi.getActivityName())){
				activityP = cb.or(activityP, cb.equal(root.get(TaskProcessMode_.activityName), wi.getActivityName()));
			}
			p = cb.and(p, activityP);
			cq.select(root).where(p);
			return em.createQuery(cq).getResultList();
		}
		return Collections.emptyList();
	}

}
