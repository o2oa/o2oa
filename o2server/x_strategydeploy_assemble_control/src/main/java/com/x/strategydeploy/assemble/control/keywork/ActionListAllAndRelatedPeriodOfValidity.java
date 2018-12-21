package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.KeyworkInfo_;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionListAllAndRelatedPeriodOfValidity extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListAllAndRelatedPeriodOfValidity.class);

	public static class WoKeyworkWithMeasures extends Wo {
		private static final long serialVersionUID = 5975138780235049687L;
		public List<WoMeasuresWithStrategy> measuresList = new ArrayList<WoMeasuresWithStrategy>();

		public List<WoMeasuresWithStrategy> getMeasuresList() {
			return measuresList;
		}

		public void setMeasuresList(List<WoMeasuresWithStrategy> measuresList) {
			this.measuresList = measuresList;
		}
	}

	public static class WoMeasuresWithStrategy extends MeasuresInfo {
		private static final long serialVersionUID = -1344569053695400928L;
		public StrategyDeploy strategy;

		public StrategyDeploy getStrategy() {
			return strategy;
		}

		public void setStrategy(StrategyDeploy strategy) {
			this.strategy = strategy;
		}
	}

	//public List<WoKeyworkWithMeasures> execute(HttpServletRequest request, EffectivePerson effectivePerson, Date date) throws Exception {
	public List<WoKeyworkWithMeasures> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year, Integer month) throws Exception {
		boolean ispass = true;
		List<WoKeyworkWithMeasures> result = new ArrayList<WoKeyworkWithMeasures>();
		if (null == year || null == month) {
			Exception e = new Exception("year or month can not be blank!");
			ispass = false;
			return result;
		}
		if (ispass) {
			List<KeyworkInfo> keyworkList = new ArrayList<KeyworkInfo>();
			List<MeasuresInfo> measuresList = new ArrayList<MeasuresInfo>();

			List<WoKeyworkWithMeasures> keyworkwithmeasuresList = new ArrayList<WoKeyworkWithMeasures>();
			List<WoMeasuresWithStrategy> measureswithstrategyList = new ArrayList<WoMeasuresWithStrategy>();

			WrapCopier<KeyworkInfo, WoKeyworkWithMeasures> KeyworkInfo_wrapout_copier = WrapCopierFactory.wo(KeyworkInfo.class, WoKeyworkWithMeasures.class, null, null);
			WrapCopier<MeasuresInfo, WoMeasuresWithStrategy> MeasuresInfo_wrapout_copier = WrapCopierFactory.wo(MeasuresInfo.class, WoMeasuresWithStrategy.class, null, null);

			StrategyDeploy strategy = new StrategyDeploy();
			keyworkList = ActionListAllAndRelatedPeriodOfValidity.listKeyworkListByDate(year, month);
			for (KeyworkInfo keyworkInfo : keyworkList) {
				measuresList = ActionListAllAndRelatedPeriodOfValidity.listMeasuresListByIds(keyworkInfo.getMeasureslist());
				for (MeasuresInfo measuresInfo : measuresList) {
					strategy = ActionListAllAndRelatedPeriodOfValidity.getStrategyDeployByparentid(measuresInfo.getMeasuresinfoparentid());
					WoMeasuresWithStrategy m_o = MeasuresInfo_wrapout_copier.copy(measuresInfo);
					m_o.setStrategy(strategy);
					measureswithstrategyList.add(m_o);
				}
				WoKeyworkWithMeasures k_o = KeyworkInfo_wrapout_copier.copy(keyworkInfo);
				k_o.setMeasuresList(measureswithstrategyList);
				keyworkwithmeasuresList.add(k_o);
			}
			return keyworkwithmeasuresList;
		} else {
			return result;
		}
	}

	public static List<KeyworkInfo> listKeyworkListByDate(String year, Integer month) throws Exception {
		Logger logger1 = LoggerFactory.getLogger(ActionListAllAndRelatedPeriodOfValidity.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(KeyworkInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
			Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
			Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), year);
			p = cb.and(p, cb.le(root.get(KeyworkInfo_.keyworkbegindate), month));
			p = cb.and(p, cb.ge(root.get(KeyworkInfo_.keyworkenddate), month));
			cq.select(root).where(p).orderBy(cb.asc(root.get(KeyworkInfo_.sequencenumber)));
			List<KeyworkInfo> os = em.createQuery(cq).getResultList();
			logger1.info("listKeyworkListByDate::OS::" + os.size());
			return os;
		} catch (Exception e) {

			throw e;
		}
	}

	/*
	public static List<KeyworkInfo> listKeyworkListByDate(Date date) throws Exception {
		Logger logger1 = LoggerFactory.getLogger(ActionListAllAndRelatedPeriodOfValidity.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(KeyworkInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
			Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
			Expression<java.sql.Date> testdate = cb.literal(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
	
			Expression<Integer> year = cb.function("year", Integer.class, testdate);
			Expression<Integer> month = cb.function("month", Integer.class, testdate);
	
			Path<Date> enddate = root.get(KeyworkInfo_.keyworkenddate);
			Expression<Integer> _enddateYearInt = cb.function("year", Integer.class, enddate);
			Expression<Integer> _enddateMonthInt = cb.function("month", Integer.class, enddate);
			Expression<Integer> _enddateYearprod12 = cb.prod(_enddateYearInt, 12); //年份乘12，得到_yearprod12
			Expression<Integer> _enddateparamInt = cb.sum(_enddateYearprod12, _enddateMonthInt); //_enddateYearprod12+month
			
			Path<Date> begindate = root.get(KeyworkInfo_.keyworkbegindate);
			Expression<Integer> _begindateYearInt = cb.function("year", Integer.class, begindate);
			Expression<Integer> _begindateMonthInt = cb.function("month", Integer.class, begindate);
			Expression<Integer> _begindateYearprod12 = cb.prod(_begindateYearInt, 12); //年份乘12，得到_yearprod12
			Expression<Integer> _begindateparamInt = cb.sum(_begindateYearprod12, _begindateMonthInt); //_enddateYearprod12+month
			
			Expression<Integer> _yearprod12 = cb.prod(year, 12); //年份乘12，得到_yearprod12
			Expression<Integer> _paramInt = cb.sum(_yearprod12, month); //_yearprod12+month
	
			Predicate p = cb.greaterThanOrEqualTo(_enddateparamInt, _paramInt);
			p = cb.and(p, cb.lessThanOrEqualTo(_begindateparamInt, _paramInt));
	
			cq.select(root).where(p).orderBy(cb.asc(root.get(KeyworkInfo_.sequencenumber)));
			List<KeyworkInfo> os = em.createQuery(cq).getResultList();
			logger1.info("listKeyworkListByDate::OS::" + os.size());
			return os;
		} catch (Exception e) {
	
			throw e;
		}
	}
	*/
	public void test() {

	}

	/*	protected static List<KeyworkInfo> listKeyworkListByYear(String _year) throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = business.entityManagerContainer().get(KeyworkInfo.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
				Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
				Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), _year);
				cq.select(root).where(p).orderBy(cb.asc(root.get(KeyworkInfo_.sequencenumber)));
				List<KeyworkInfo> os = em.createQuery(cq).getResultList();
				return os;
			} catch (Exception e) {
				throw e;
			}
		}
	*/
	public static List<MeasuresInfo> listMeasuresListByIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<MeasuresInfo> os = business.measuresInfoFactory().getListByIds(ids);
			return os;
		} catch (Exception e) {
			throw e;
		}
	}

	public static StrategyDeploy getStrategyDeployByparentid(String _id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			StrategyDeploy strategy = business.strategyDeployFactory().getById(_id);
			return strategy;
		} catch (Exception e) {
			throw e;
		}
	}

}
