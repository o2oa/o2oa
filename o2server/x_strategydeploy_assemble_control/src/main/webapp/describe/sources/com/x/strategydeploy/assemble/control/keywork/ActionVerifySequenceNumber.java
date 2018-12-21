package com.x.strategydeploy.assemble.control.keywork;

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
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.KeyworkInfo_;

public class ActionVerifySequenceNumber extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionVerifySequenceNumber.class);

	public static class Wo extends WrapBoolean {
	}

	protected ActionResult<Wo> excute(Wi wi) throws Exception {
		Integer _sn = wi.getSequencenumber();
		String _year = wi.getKeyworkyear();
		String _unit = wi.getKeyworkunit();
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
			} else {
				wo.setValue(true);
				result.setData(wo);
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
				EntityManager em = business.entityManagerContainer().get(KeyworkInfo.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
				Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
				Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), _year);
				p = cb.and(p, cb.equal(root.get(KeyworkInfo_.keyworkunit), _unit));
				p = cb.and(p, cb.equal(root.get(KeyworkInfo_.sequencenumber), _sn));
				cq.select(root).where(p);
				List<KeyworkInfo> objs = em.createQuery(cq).getResultList();

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
