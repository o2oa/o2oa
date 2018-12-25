package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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

public class ActionListAllAndRelated extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionListAllAndRelated.class);

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

	public List<WoKeyworkWithMeasures> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year) throws Exception {
		boolean ispass = true;
		List<WoKeyworkWithMeasures> result = new ArrayList<WoKeyworkWithMeasures>();
		logger.info("test 111111111111");
		if (null == year || StringUtils.isBlank(year)) {
			
			Exception e = new Exception("year can not be blank!");
			ispass = false;
			return result;
		}
		logger.info("test 22222222222222222222");
		if (ispass) {
			logger.info("test 333333333333333");
			List<KeyworkInfo> keyworkList = new ArrayList<KeyworkInfo>();
			List<MeasuresInfo> measuresList = new ArrayList<MeasuresInfo>();

			List<WoKeyworkWithMeasures> keyworkwithmeasuresList = new ArrayList<WoKeyworkWithMeasures>();
			List<WoMeasuresWithStrategy> measureswithstrategyList = new ArrayList<WoMeasuresWithStrategy>();

			WrapCopier<KeyworkInfo, WoKeyworkWithMeasures> KeyworkInfo_wrapout_copier = WrapCopierFactory.wo(KeyworkInfo.class, WoKeyworkWithMeasures.class, null, null);
			WrapCopier<MeasuresInfo, WoMeasuresWithStrategy> MeasuresInfo_wrapout_copier = WrapCopierFactory.wo(MeasuresInfo.class, WoMeasuresWithStrategy.class, null, null);

			StrategyDeploy strategy = new StrategyDeploy();
			keyworkList = ActionListAllAndRelated.listKeyworkListByYear(year);
			for (KeyworkInfo keyworkInfo : keyworkList) {
				measuresList = ActionListAllAndRelated.listMeasuresListByIds(keyworkInfo.getMeasureslist());
				for (MeasuresInfo measuresInfo : measuresList) {
					strategy = ActionListAllAndRelated.getStrategyDeployByparentid(measuresInfo.getMeasuresinfoparentid());
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
			logger.info("test 444444444444444444");
			return result;
		}
	}

	protected static List<KeyworkInfo> listKeyworkListByYear(String _year) throws Exception {
		//List<KeyworkInfo> result = new ArrayList<KeyworkInfo>();
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
