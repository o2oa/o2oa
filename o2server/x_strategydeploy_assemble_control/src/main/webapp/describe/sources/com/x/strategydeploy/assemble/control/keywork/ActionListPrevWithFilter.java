package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.KeyWorkOperationService;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionListPrevWithFilter extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, BaseAction.Wi wrapIn) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();

		if (id == null || id.isEmpty()) {
			id = EMPTY_SYMBOL;
		}

		if (count == null) {
			count = DEFAULT_COUNT;
		}

		WrapCopier<KeyworkInfo, BaseAction.Wo> wrapout_copier = WrapCopierFactory.wo(KeyworkInfo.class,
				BaseAction.Wo.class, null, JpaObject.FieldsInvisible);

		String sequenceField = wrapIn.getSequenceField();
		EqualsTerms equals = new EqualsTerms();
		NotEqualsTerms notEquals = new NotEqualsTerms();
		LikeTerms likes = new LikeTerms();
		InTerms ins = new InTerms();
		NotInTerms notIns = new NotInTerms();
		MemberTerms members = new MemberTerms();
		NotMemberTerms notMembers = new NotMemberTerms();
		Boolean andJoin = false;

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

		// 标题模糊查询
		if (null != wrapIn.getKeyworktitle() && !wrapIn.getKeyworktitle().isEmpty()) {
			logger.info("标题模糊查询:" + wrapIn.getKeyworktitle());
			likes.put("keyworktitle", wrapIn.getKeyworktitle());
		}

		// 部门精确匹配
		if (null != wrapIn.getKeyworkunit() && !wrapIn.getKeyworkunit().isEmpty()) {
			logger.info("keyworkunit:" + wrapIn.getKeyworkunit());
			equals.put("keyworkunit", wrapIn.getKeyworkunit());
		}

		// result = this.standardListPrev(wrapout_copier, id, count,
		// sequenceField, equals, notEquals, likes, ins, notIns, members,
		// notMembers, andJoin, order);
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

			if (reader_persons.indexOf(distinguishedName) >= 0 || writer_persons.indexOf(distinguishedName) >= 0) {
				result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
						notIns, members, notMembers, null, andJoin, order);
				if (reader_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					wos = result.getData();
					for (Wo wo : wos) {
						wo.setActions(actions);
					}
					result.setData(wos);
				}
				if (writer_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					actions.add("EDIT");
					actions.add("DELETE");
					wos = result.getData();
					for (Wo wo : wos) {
						wo.setActions(actions);
					}
					result.setData(wos);
				}
			} else {
				List<String> units = business.organization().unit().listWithPerson(effectivePerson);
				List<String> supunits = business.organization().unit().listWithUnitSupNested(units);
				units.addAll(supunits);
				logger.info("units:" + units);
				ins.put("keyworkunit", units);
				result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
						notIns, members, notMembers, null, andJoin, order);
				actions.add("OPEN");
				wos = result.getData();
				for (Wo wo : wos) {
					wo.setActions(actions);
				}
				result.setData(wos);
			}

		} catch (Exception e) {
			throw e;
		}

		return result;
	}
}
