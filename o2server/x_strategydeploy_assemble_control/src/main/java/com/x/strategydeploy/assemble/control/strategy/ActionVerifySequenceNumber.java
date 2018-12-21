package com.x.strategydeploy.assemble.control.strategy;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyDeploy;
import com.x.strategydeploy.core.entity.StrategyDeploy_;

public class ActionVerifySequenceNumber extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionVerifySequenceNumber.class);

	public static class Wo extends WrapBoolean {
	}

	public static class SnWo extends WrapBoolean {
		String describe = "";

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	protected ActionResult<Wo> excute(Wi wi) throws Exception {
		Integer _sn = wi.getSequencenumber();
		String _year = wi.getStrategydeployyear();
		ActionResult<Wo> result = new ActionResult<Wo>();
		Wo wo = new Wo();
		boolean ischeck = true;
		if (null != _sn) {
			//_sn = StringUtils.trim(_sn);
		} else {
			Exception e = new Exception("sequencenumber can not be blank!");
			result.error(e);
			ischeck = false;
		}
		if (ischeck) {
			if (_sn <= 0) {
				Exception e = new Exception("sequencenumber must be positive integer! begin from 1");
				result.error(e);
				ischeck = false;
			}
		}
		if (ischeck) {
			Integer _it = new Integer(_sn);
			if (_it <= 0) {
				Exception e = new Exception("sequencenumber must be positive integer! begin from 1");
				result.error(e);
				ischeck = false;
			}
		}

		if (ischeck) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = business.entityManagerContainer().get(StrategyDeploy.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<StrategyDeploy> cq = cb.createQuery(StrategyDeploy.class);
				Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
				Predicate p = cb.equal(root.get(StrategyDeploy_.strategydeployyear), _year);
				p = cb.and(p, cb.equal(root.get(StrategyDeploy_.sequencenumber), _sn));
				cq.select(root).where(p);
				List<StrategyDeploy> objs = em.createQuery(cq).getResultList();
				if (null != objs) {
					if (objs.size() > 0) {
						Exception e = new Exception("sequencenumber :" + _sn + " has been used.");
						result.error(e);
					} else {
						wo.setValue(true);
						result.setData(wo);
					}
				}
			} catch (Exception e) {
				result.error(e);
			}
		}
		return result;
	}

}
