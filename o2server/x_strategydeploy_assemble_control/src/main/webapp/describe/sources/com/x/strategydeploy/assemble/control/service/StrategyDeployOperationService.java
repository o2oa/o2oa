package com.x.strategydeploy.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.strategy.BaseAction;
import com.x.strategydeploy.assemble.control.strategy.BaseAction.Wi;
import com.x.strategydeploy.assemble.control.strategy.BaseAction.Wo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class StrategyDeployOperationService {
	private static  Logger logger = LoggerFactory.getLogger(StrategyDeployOperationService.class);

	private StrategyDeployExcuteSave strategyDeployExcuteSave = new StrategyDeployExcuteSave();

	List<String> reader_groups = new ArrayList<>();
	List<String> writer_groups = new ArrayList<>();

	public StrategyDeployOperationService() {
		//List<String> reader_groups = new ArrayList<>();
		reader_groups.add("战略读者@strategy_reader_g@G");
		//List<String> writer_groups = new ArrayList<>();
		writer_groups.add("战略管理者@strategy_writer_g@G");
	}

	/**
	 * 向数据库保存战略部署对象,先进行判断。
	 * 
	 * @param wrapIn
	 */
	public StrategyDeploy save(BaseAction.Wi wrapIn) throws Exception {
		StrategyDeploy strategydeploy = null;
		if (wrapIn == null) {
			throw new Exception("strategydeploy is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean IsExist = false;
			String _id = wrapIn.getId();
			logger.info("save _id:" + _id);
			//IsExistById

			if (null == _id || _id.isEmpty()) {
				IsExist = false;
			} else {
				IsExist = business.strategyDeployFactory().IsExistById(_id);
			}

			if (IsExist) {
				//IsExist true	如果文档存在，那么更新。更新的时候id和name不改变。
				StrategyDeploy origin_strategydeploy = business.strategyDeployFactory().getById(_id);
				List<String> excludes = new ArrayList<String>();
				excludes.add("id");
				excludes.add("strategydeployname");
				WrapCopier<BaseAction.Wi, StrategyDeploy> beanCopyTools = WrapCopierFactory.wi(BaseAction.Wi.class, StrategyDeploy.class, null,excludes);
				beanCopyTools.copy(wrapIn, origin_strategydeploy);
				strategydeploy = strategyDeployExcuteSave.save(emc, origin_strategydeploy);
			} else {
				//IsExist false		如果文档不存在，那么创建
				logger.info("StrategyDeploy_wrapIn：" + wrapIn.toString());
				strategydeploy = Wi.copier.copy(wrapIn);
				strategydeploy = strategyDeployExcuteSave.save(emc, strategydeploy);
			}
		} catch (Exception e) {
			logger.warn("strategydeploy update/ get a error!");
			throw e;
		}
		return strategydeploy;
	}

	public List<Wo> setActions(List<Wo> wos, EffectivePerson effectivePerson) throws Exception {
		//ActionResult<List<Wo>> result = new ActionResult<>();
		//List<Wo> result = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			List<String> reader_group_subnested = business.organization().group().listWithGroupSubNested(reader_groups);
			List<String> writer_group_subnested = business.organization().group().listWithGroupSubNested(writer_groups);

			if (ListTools.isNotEmpty(reader_group_subnested)) {
				reader_groups.addAll(reader_group_subnested);
			}
			if (ListTools.isNotEmpty(writer_group_subnested)) {
				writer_groups.addAll(writer_group_subnested);
			}
			List<String> readers = business.organization().person().listWithGroup(reader_groups);
			List<String> writers = business.organization().person().listWithGroup(writer_groups);
			logger.info("readers:" + readers.toString());
			logger.info("writers:" + writers.toString());

			String distinguishedname = effectivePerson.getDistinguishedName();
			List<String> actions = new ArrayList<>();

			if (readers.indexOf(distinguishedname) >= 0) {
				actions.add("OPEN");
			}
			if (writers.indexOf(distinguishedname) >= 0) {
				actions.add("OPEN");
				actions.add("EDIT");
				actions.add("DELETE");
			}

			List<String> identitys = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithIdentity(identitys);
			List<String> unitSubNested = business.organization().unit().listWithUnitSubNested(units);

			units.addAll(unitSubNested);

			for (Wo wo : wos) {
				List<String> _deptlist = wo.getDeptlist();
				if (ListTools.containsAny(units, _deptlist)) {
					if (actions.indexOf("OPEN") < 0) {
						actions.add("OPEN");
					}
				}
				wo.setActions(actions);
				wo.getDeptlist();
			}

		} catch (Exception e) {
			logger.info("strategydeploy setActions error:" + e.getStackTrace());
			throw e;
		}
		return wos;
	}

	public List<String> getReader_groups() {
		return reader_groups;
	}

	public void setReader_groups(List<String> reader_groups) {
		this.reader_groups = reader_groups;
	}

	public List<String> getWriter_groups() {
		return writer_groups;
	}

	public void setWriter_groups(List<String> writer_groups) {
		this.writer_groups = writer_groups;
	}

}
