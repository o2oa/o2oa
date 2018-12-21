package com.x.strategydeploy.assemble.control.measures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo_;

public class ActionListDeptObjectsByYear extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListDeptObjectsByYear.class);

/*	public static class WoUnit extends Unit {
		private static final long serialVersionUID = 7748579354998531123L;
		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null, JpaObject.FieldsInvisible);
		
		private String ZH_HK;
		private String EN;
		
		public String getZH_HK() {
			return ZH_HK;
		}

		public void setZH_HK(String zH_HK) {
			ZH_HK = zH_HK;
		}

		public String getEN() {
			return EN;
		}

		public void setEN(String eN) {
			EN = eN;
		}

		public WoUnit() {
			
		}
		
		public WoUnit(Unit u) throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> _zh_hk_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), "Name_zh_HK");
				String _zh_hk = "";
				if (!_zh_hk_list.isEmpty()) {
					_zh_hk = _zh_hk_list.get(0);
				} else {
					_zh_hk = "";
				}

				String _en = "";
				List<String> _en_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), "Name_en");
				if (!_en_list.isEmpty()) {
					_en = _en_list.get(0);
				}else {
					_en = "";
				}

				//this.setZH_CN(_zh_cn);
				this.setZH_HK(_zh_hk);
				this.setEN(_en);
			}
		}
		
	}*/

	public List<UnitML> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year) throws Exception {
		List<UnitML> wounits = new ArrayList<UnitML>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(MeasuresInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
			Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
			Predicate p = cb.isNotEmpty(root.get(MeasuresInfo_.deptlist));
			List<MeasuresInfo> os = em.createQuery(cq.select(root).where(p)).getResultList();
			List<String> list = new ArrayList<>();
			for (MeasuresInfo measuresInfo : os) {
				if (ListTools.isNotEmpty(measuresInfo.getDeptlist())) {
					list.addAll(measuresInfo.getDeptlist());
				}
			}
			//logger.info("DeptList:" + list.toString());
			list = list.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted().collect(Collectors.toList());
			for (String string : list) {
				Unit u = business.organization().unit().getObject(string);
				UnitML wou = new UnitML();
				wou = wou.creatUnitMLByUnitName(string);
				wou = UnitML.copier.copy(u, wou);
				wounits.add(wou);
			}
			
			return wounits;
		} catch (Exception e) {
			throw e;
		}

	}
}
