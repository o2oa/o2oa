package com.x.strategydeploy.assemble.control.measures;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.MeasuresInfoOperationService;
import com.x.strategydeploy.core.entity.MeasuresInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	public final static String ML_ZH_HK = "Name_zh_HK";

	public final static String ML_EN = "Name_en";

	public final static Integer DEFAULT_COUNT = 20;

	protected MeasuresInfoOperationService measuresInfoOperationService = new MeasuresInfoOperationService();

	public static class Wi extends MeasuresInfo {
		private static final long serialVersionUID = 1221016716486005485L;
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		public static WrapCopier<Wi, MeasuresInfo> copier = WrapCopierFactory.wi(Wi.class, MeasuresInfo.class, null, JpaObject.FieldsUnmodify);

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		@FieldDescribe("升降序标志.")
		private String ordersymbol = "";

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public String getOrdersymbol() {
			return ordersymbol;
		}

		public void setOrdersymbol(String ordersymbol) {
			this.ordersymbol = ordersymbol;
		}
	}

	public static class Wo extends MeasuresInfo {
		private static final long serialVersionUID = -1124004693944906073L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MeasuresInfo, Wo> copier = WrapCopierFactory.wo(MeasuresInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		private List<String> actions = new ArrayList<>();

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public List<String> getActions() {
			return actions;
		}

		public void setActions(List<String> actions) {
			this.actions = actions;
		}

	}

	protected Ehcache cache = ApplicationCache.instance().getCache(CacheInputResult.class);

	public static class CacheInputResult {

		private String name;

		private byte[] bytes;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

	}

	public static class MeasuresInfoWithUnitML extends MeasuresInfo {

		private static final long serialVersionUID = 1460366891471610232L;
		List<UnitML> units = new ArrayList<>();
		public static WrapCopier<MeasuresInfo, MeasuresInfoWithUnitML> copier = WrapCopierFactory.wo(MeasuresInfo.class, MeasuresInfoWithUnitML.class, null, JpaObject.FieldsInvisible);

		public MeasuresInfoWithUnitML() {

		}

		public MeasuresInfoWithUnitML(MeasuresInfo measuresinfo) throws Exception {
			List<String> deptlist = measuresinfo.getDeptlist();
			for (String dept : deptlist) {
				UnitML u = new UnitML();
				u = u.creatUnitMLByUnitName(dept);
				units.add(u);
			}
			setUnits(units);
		}

		public MeasuresInfoWithUnitML getObjectIncludUnitML(MeasuresInfo measuresinfo) throws Exception {
			MeasuresInfoWithUnitML o = new MeasuresInfoWithUnitML();
			List<String> deptlist = measuresinfo.getDeptlist();
			for (String dept : deptlist) {
				UnitML u = new UnitML();
				u = u.creatUnitMLByUnitName(dept);
				units.add(u);
			}
			o = copier.copy(measuresinfo, o);
			o.setUnits(units);

//			String jsonstr = gson.toJson(o);
//			logger.info(jsonstr);
//			logger.info("！！！================================");
			return o;
		}

		public List<UnitML> getUnits() {
			return units;
		}

		public void setUnits(List<UnitML> units) {
			this.units = units;
		}
	}

	public static class UnitML extends Unit {
		private static final long serialVersionUID = 7748579354998531123L;
		static WrapCopier<Unit, UnitML> copier = WrapCopierFactory.wo(Unit.class, UnitML.class, null, JpaObject.FieldsInvisible);

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

		public UnitML() {

		}

		public UnitML creatUnitMLByUnitName(String uStr) throws Exception {
			UnitML uml = new UnitML();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Unit u = business.organization().unit().getObject(uStr);
				List<String> _zh_hk_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), ML_ZH_HK);
				String _zh_hk = "";
				if (!_zh_hk_list.isEmpty()) {
					_zh_hk = _zh_hk_list.get(0);
				} else {
					_zh_hk = "";
				}

				String _en = "";
				List<String> _en_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), ML_EN);
				if (!_en_list.isEmpty()) {
					_en = _en_list.get(0);
				} else {
					_en = "";
				}

				uml = copier.copy(u, uml);
				uml.setZH_HK(_zh_hk);
				uml.setEN(_en);
			}

			return uml;
		}

		public UnitML creatUnitMLByUnit(Unit u) throws Exception {
			UnitML uml = new UnitML();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> _zh_hk_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), ML_ZH_HK);
				String _zh_hk = "";
				if (!_zh_hk_list.isEmpty()) {
					_zh_hk = _zh_hk_list.get(0);
				} else {
					_zh_hk = "";
				}

				String _en = "";
				List<String> _en_list = business.organization().unitAttribute().listAttributeWithUnitWithName(u.getUnique(), ML_EN);
				if (!_en_list.isEmpty()) {
					_en = _en_list.get(0);
				} else {
					_en = "";
				}

				uml = copier.copy(u, uml);
				uml.setZH_HK(_zh_hk);
				uml.setEN(_en);
			}

			return uml;
		}

	}
}
