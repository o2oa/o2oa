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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.jaxrs.MemberTerms;
import com.x.base.core.project.jaxrs.NotEqualsTerms;
import com.x.base.core.project.jaxrs.NotInTerms;
import com.x.base.core.project.jaxrs.NotMemberTerms;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.ThisApplication;
import com.x.strategydeploy.assemble.control.keywork.BaseAction.Wo;
import com.x.strategydeploy.assemble.control.service.KeyWorkOperationService;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.KeyworkInfo_;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionListAllAndRelatedWithFilter extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListAllAndRelatedWithFilter.class);

	public static class WoKeyworkWithMeasures extends Wo {
		private static final long serialVersionUID = 5975138780235049687L;

		// private Long rank = 0L;

		// @FieldDescribe("用于列表排序的属性.")
		// private String sequenceField = "sequencenumber";

		// @FieldDescribe("操作列表.")
		// private List<String> actions = new ArrayList<>();

		public List<WoMeasuresWithStrategy> measuresList = new ArrayList<WoMeasuresWithStrategy>();

		public List<WoMeasuresWithStrategy> getMeasuresList() {
			return measuresList;
		}

		public void setMeasuresList(List<WoMeasuresWithStrategy> measuresList) {
			this.measuresList = measuresList;
		}

		// public Long getRank() {
		// return rank;
		// }
		//
		// public void setRank(Long rank) {
		// this.rank = rank;
		// }

		// public String getSequenceField() {
		// return sequenceField;
		// }
		//
		// public void setSequenceField(String sequenceField) {
		// this.sequenceField = sequenceField;
		// }

		// public List<String> getActions() {
		// return actions;
		// }
		//
		// public void setActions(List<String> actions) {
		// this.actions = actions;
		// }
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

	public List<WoKeyworkWithMeasures> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			Integer page, Integer count, BaseAction.Wi wrapIn) throws Exception {
		boolean ispass = true;
		List<WoKeyworkWithMeasures> result = new ArrayList<WoKeyworkWithMeasures>();
		if (null == wrapIn.getKeyworkyear() || StringUtils.isBlank(wrapIn.getKeyworkyear())) {

			Exception e = new Exception("year can not be blank!");
			ispass = false;
			return result;
		}
		if (ispass) {
			List<Wo> keyworkList = new ArrayList<Wo>();
			ActionResult<List<WoKeyworkWithMeasures>> ActionResult_keyworkList = new ActionResult<List<WoKeyworkWithMeasures>>();
			List<MeasuresInfo> measuresList = new ArrayList<MeasuresInfo>();

			List<WoKeyworkWithMeasures> keyworkwithmeasuresList = new ArrayList<WoKeyworkWithMeasures>();
			List<WoMeasuresWithStrategy> measureswithstrategyList = new ArrayList<WoMeasuresWithStrategy>();

			List<String> includes = new ArrayList<String>();
			includes.add("actions");
			// WrapCopier<KeyworkInfo, WoKeyworkWithMeasures> KeyworkInfo_wrapout_copier =
			// WrapCopierFactory.wo(KeyworkInfo.class, WoKeyworkWithMeasures.class, null,
			// null);
			WrapCopier<Wo, WoKeyworkWithMeasures> KeyworkInfo_wrapout_copier = WrapCopierFactory.wo(Wo.class,
					WoKeyworkWithMeasures.class, includes, null);
			WrapCopier<MeasuresInfo, WoMeasuresWithStrategy> MeasuresInfo_wrapout_copier = WrapCopierFactory
					.wo(MeasuresInfo.class, WoMeasuresWithStrategy.class, null, null);

			StrategyDeploy strategy = new StrategyDeploy();

			ActionListAllAndRelatedWithFilter thisObject = new ActionListAllAndRelatedWithFilter();
			keyworkList = thisObject.listKeyworkListByFilter(effectivePerson, page, count, wrapIn);
			// keyworkList =
			// ActionListAllAndRelatedWithFilter.listKeyworkListByFilter(effectivePerson,page,count,wrapIn);
			// for (KeyworkInfo keyworkInfo : keyworkList) {
			for (Wo keyworkInfo : keyworkList) {
				measuresList = ActionListAllAndRelatedWithFilter.listMeasuresListByIds(keyworkInfo.getMeasureslist());
				for (MeasuresInfo measuresInfo : measuresList) {
					strategy = ActionListAllAndRelatedWithFilter
							.getStrategyDeployByparentid(measuresInfo.getMeasuresinfoparentid());
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

	protected List<Wo> listKeyworkListByFilter(EffectivePerson effectivePerson, Integer page, Integer count,
			BaseAction.Wi wrapIn) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> KeyworkInfoList = new ArrayList<>();

		Integer _totalcount = 0;
		_totalcount = page * count;
		Integer _begin = (page - 1) * count; // list being index
		Integer _end = _begin + count; // list end index
		String id = EMPTY_SYMBOL;

		if (count == null || count <= 0) {
			count = DEFAULT_COUNT;
		}
		if (page == null || page <= 0) {
			page = 1;
		}

		// WrapCopier<KeyworkInfo, KeyworkInfo> wrapout_copier =
		// WrapCopierFactory.wo(KeyworkInfo.class, KeyworkInfo.class, null,
		// Wo.Excludes);
		WrapCopier<KeyworkInfo, BaseAction.Wo> wrapout_copier = WrapCopierFactory.wo(KeyworkInfo.class,
				BaseAction.Wo.class, null, Wo.Excludes);
		// WrapCopier<KeyworkInfo, WoKeyworkWithMeasures> wrapout_copier =
		// WrapCopierFactory.wo(KeyworkInfo.class, WoKeyworkWithMeasures.class, null,
		// Wo.Excludes);

		String sequenceField = wrapIn.getSequenceField();
		EqualsTerms equals = new EqualsTerms();
		NotEqualsTerms notEquals = new NotEqualsTerms();
		LikeTerms likes = new LikeTerms();
		InTerms ins = new InTerms();
		NotInTerms notIns = new NotInTerms();
		MemberTerms members = new MemberTerms();
		NotMemberTerms notMembers = new NotMemberTerms();
		Boolean andJoin = true;

		String order = DESC;
		if (null != wrapIn.getOrdersymbol() && !wrapIn.getOrdersymbol().isEmpty()) {
			if (StringUtils.upperCase(wrapIn.getOrdersymbol()).equals("ASC")) {
				order = ASC;
			}
		}

		// 年份精确匹配
		if (null != wrapIn.getKeyworkyear() && !wrapIn.getKeyworkyear().isEmpty()) {
			equals.put("keyworkyear", wrapIn.getKeyworkyear());
		}

		// 部门精确匹配
		if (null != wrapIn.getKeyworkunit() && !wrapIn.getKeyworkunit().isEmpty()) {
			equals.put("keyworkunit", wrapIn.getKeyworkunit());
		}

		// 标题模糊查询
		if (null != wrapIn.getKeyworktitle() && !wrapIn.getKeyworktitle().isEmpty()) {
			likes.put("keyworktitle", wrapIn.getKeyworktitle());
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// 1,判断当前人员的权限，如果在读者，或者作者组里，那么什么都不做。否则做部门过滤。
			// 2,判断当前人员的循环上级，是否已在部门列表中。
			KeyWorkOperationService keyWorkOperationService = new KeyWorkOperationService();
			String distinguishedName = effectivePerson.getDistinguishedName();
			List<String> reader_g = keyWorkOperationService.getReader_groups();
			List<String> writer_g = keyWorkOperationService.getWriter_groups();
			List<String> reader_persons = business.organization().person().listWithGroup(reader_g);
			List<String> writer_persons = business.organization().person().listWithGroup(writer_g);

			List<Wo> wos = new ArrayList<>();
			List<String> actions = new ArrayList<>();
			// sequenceField =  JpaObject.sequence_FIELDNAME;
			// logger.info("ActionListAllAndRelatedWithFilter-->distinguishedName:" +
			// distinguishedName);
			// logger.info("ActionListAllAndRelatedWithFilter-->reader_persons:" +
			// reader_persons.toString() + " writer_persons:" + writer_persons.toString());
			if (reader_persons.indexOf(distinguishedName) >= 0 || writer_persons.indexOf(distinguishedName) >= 0) {

				result = this.standardListNext(wrapout_copier, id, _totalcount, sequenceField, equals, notEquals, likes,
						ins, notIns, members, notMembers, null, andJoin, order);
				// KeyworkInfoList = result.getData();
				if (reader_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					KeyworkInfoList = result.getData();

					if (KeyworkInfoList.size() > _end) {
						KeyworkInfoList = KeyworkInfoList.subList(_begin, _end);
					} else {
						KeyworkInfoList = KeyworkInfoList.subList(_begin, KeyworkInfoList.size());
						// 注：List.subList(int fromIndex【包含】, int toIndex【不包含】)
					}

					for (Wo wo : KeyworkInfoList) {
						wo.setActions(actions);
					}
				}

				if (writer_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					actions.add("EDIT");
					actions.add("DELETE");
					KeyworkInfoList = result.getData();

					if (KeyworkInfoList.size() > _end) {
						KeyworkInfoList = KeyworkInfoList.subList(_begin, _end);
					} else {
						KeyworkInfoList = KeyworkInfoList.subList(_begin, KeyworkInfoList.size());
					}

					for (Wo wo : KeyworkInfoList) {
						wo.setActions(actions);
					}

					// logger.info("writer_persons.indexOf(distinguishedName) >= 0");
					// logger.info("KeyworkInfoList:" + KeyworkInfoList.toString());

					// result.setData(wos);
				}

			} else {
				List<String> units = business.organization().unit().listWithPerson(effectivePerson);
				List<String> supunits = business.organization().unit().listWithUnitSupNested(units);
				units.addAll(supunits);
				logger.info("units:" + units);
				// ins.put("deptlist", units);
				ins.put("keyworkunit", units);
				result = this.standardListNext(wrapout_copier, id, _totalcount, sequenceField, equals, notEquals, likes,
						ins, notIns, members, notMembers, null, andJoin, order);
				actions.add("OPEN");
				KeyworkInfoList = result.getData();

				if (KeyworkInfoList.size() > _end) {
					KeyworkInfoList = KeyworkInfoList.subList(_begin, _end);
				} else {
					KeyworkInfoList = KeyworkInfoList.subList(_begin, KeyworkInfoList.size());
				}
				for (Wo wo : KeyworkInfoList) {
					wo.setActions(actions);
				}

			}

		} catch (Exception e) {
			throw e;
		}

		// return result;
		return KeyworkInfoList;
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
