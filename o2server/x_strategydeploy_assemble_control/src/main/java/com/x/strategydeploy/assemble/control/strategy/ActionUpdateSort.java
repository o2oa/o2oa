package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.StrategyDeployExcuteSave;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionUpdateSort extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionUpdateSort.class);
	private StrategyDeployExcuteSave strategyDeployExcuteSave = new StrategyDeployExcuteSave();

	//入
	public static class SortWi extends Wi {
		private static final long serialVersionUID = 4611376550282880957L;
		public List<String> ids; //这一页内的对象，重新排序后的id列表。

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

	}

	//出
	public static class SortWo extends WrapBoolean {
	}

	//protected ActionResult<SortWo> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count, SortWi wrapIn) throws Exception {
	protected ActionResult<SortWo> execute(HttpServletRequest request, EffectivePerson effectivePerson, SortWi wrapIn) throws Exception {
		ActionResult<SortWo> result = new ActionResult<>();
		SortWo sortwo = new SortWo();

		//升降序标志，默认降序
		String order = DESC;
		if (null != wrapIn.getOrdersymbol() && !wrapIn.getOrdersymbol().isEmpty()) {
			if (StringUtils.upperCase(wrapIn.getOrdersymbol()).equals("ASC")) {
				order = ASC;
			}
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> _ids = new ArrayList<String>();
			List<StrategyDeploy> strategydeploys = new ArrayList<StrategyDeploy>();

			List<Integer> snList = new ArrayList<Integer>();
			_ids = wrapIn.getIds();
			for (String _id : _ids) {
				StrategyDeploy _strategydeploy = business.strategyDeployFactory().getById(_id);
				strategydeploys.add(_strategydeploy);
			}

			snList = ListTools.extractProperty(strategydeploys, "sequencenumber", Integer.class, false, false);
			for (Integer integer : snList) {
				logger.info("没排序:" + integer);
			}
			if (order.equals(DESC)) {
				//自然序逆序元素，使用Comparator 提供的reverseOrder() 方法
				//snList = snList.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				snList = snList.stream().filter(num -> num != null).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			} else {
				//正序排列
				//snList = snList.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted().collect(Collectors.toList());
				snList = snList.stream().filter(num -> num != null).distinct().sorted().collect(Collectors.toList());
			}
			for (Integer integer : snList) {
				logger.info("排序之后:" + integer);
			}

			for (int i = 0; i < strategydeploys.size(); i++) {
				StrategyDeploy o = strategydeploys.get(i);
				o.setSequencenumber(snList.get(i));
				strategyDeployExcuteSave.save(emc, o);
			}
			sortwo.setValue(true);
			result.setData(sortwo);
		} catch (Exception e) {
			result.error(e);
		}

		return result;
	}
}
