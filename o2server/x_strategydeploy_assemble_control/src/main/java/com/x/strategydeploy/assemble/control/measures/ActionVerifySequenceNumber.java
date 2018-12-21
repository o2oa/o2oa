package com.x.strategydeploy.assemble.control.measures;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.measures.tools.VerifySequenceNumberTools;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo_;

public class ActionVerifySequenceNumber extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionVerifySequenceNumber.class);

	public static class Wo extends WrapBoolean {
	}

	protected ActionResult<Wo> excute(Wi wi) throws Exception {
		//Integer _sn = wi.getSequencenumber();
		String _sn = wi.getSequencenumber();
		String _year = wi.getMeasuresinfoyear();
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

		//校验测试
		ischeck = VerifySequenceNumberTools.VerifySequenceNumber(_sn);
		//SequenceNumber must be like 1.13,1.20;not like 000.10，000.000
		if (!ischeck) {
			Exception e = new Exception("SequenceNumber must be like 1.13,1.20;not like 000.10，000.000!");
			result.error(e);
			ischeck = false;
		}

		/*				
		if (ischeck) {
			//if (_sn <= 0) {
			if (StringUtils.equalsIgnoreCase(_sn, "0")) {
				Exception e = new Exception("sequencenumber must be number! begin from like 1.1");
				result.error(e);
				ischeck = false;
			} else {
				wo.setValue(true);
				result.setData(wo);
			}
		}
		if (ischeck) {
			Double _it = new Double(_sn);
			if (_it <= 0) {
				Exception e = new Exception("sequencenumber must be number! begin from like 1.1");
				result.error(e);
				ischeck = false;
			}
		}
		*/
		if (ischeck) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = business.entityManagerContainer().get(MeasuresInfo.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
				Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
				Predicate p = cb.equal(root.get(MeasuresInfo_.measuresinfoyear), _year);
				p = cb.and(p, cb.equal(root.get(MeasuresInfo_.sequencenumber), _sn));
				cq.select(root).where(p);
				List<MeasuresInfo> objs = em.createQuery(cq).getResultList();

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

	public static boolean VerifySequenceNumber(String _sn) {
		//StringUtils.countMatches(arg0, arg1)
		return false;
	}

}
