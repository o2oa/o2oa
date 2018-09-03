package com.x.strategydeploy.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.measures.BaseAction;
import com.x.strategydeploy.assemble.control.measures.BaseAction.Wi;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class MeasuresInfoOperationService {
	private static  Logger logger = LoggerFactory.getLogger(MeasuresInfoOperationService.class);

	private MeasuresInfoExcuteSave measuresInfoExcuteSave = new MeasuresInfoExcuteSave();

	List<String> reader_groups = new ArrayList<>();
	List<String> writer_groups = new ArrayList<>();

	public MeasuresInfoOperationService() {
		//List<String> reader_groups = new ArrayList<>();
		reader_groups.add("战略读者@strategy_reader_g@G");
		//List<String> writer_groups = new ArrayList<>();
		writer_groups.add("战略管理者@strategy_writer_g@G");
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
	
	/**
	 * 向数据库保存举措对象
	 * 
	 * @param wrapIn
	 */
	public MeasuresInfo save(BaseAction.Wi wrapIn) throws Exception {
		MeasuresInfo measuresinfo = new MeasuresInfo();
		if (wrapIn == null) {
			throw new Exception("measuresinfo is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean IsExist = false;
			List<String> excludes = new ArrayList<String>();

			String _id = wrapIn.getId();
			logger.info("save _id:" + _id);
			// IsExistById
			if (null == _id || _id.isEmpty()) {
				IsExist = false;
			} else {
				IsExist = business.measuresInfoFactory().IsExistById(_id);
			}

			if (IsExist) {
				logger.info("IsExistById true");
				// IsExist true 如果文档存在，那么更新。更新的时候id和name不改变。
				MeasuresInfo origin_measuresinfo = business.measuresInfoFactory().getById(_id);

				excludes.add("id");
				excludes.add("measuresinfoname");
				WrapCopier<BaseAction.Wi, MeasuresInfo> beanCopyTools = WrapCopierFactory.wi(BaseAction.Wi.class,
						MeasuresInfo.class, null, excludes);
				beanCopyTools.copy(wrapIn, origin_measuresinfo);
				measuresinfo = measuresInfoExcuteSave.save(emc, origin_measuresinfo);

				// emc.beginTransaction(MeasuresInfo.class);
				// emc.persist(origin_measuresinfo);
				// emc.commit();
				// return origin_measuresinfo;
			} else {
				// IsExist false 如果文档不存在，那么创建
				logger.info("IsExistById false");
				measuresinfo = Wi.copier.copy(wrapIn);
				measuresinfo = measuresInfoExcuteSave.save(emc, measuresinfo);
			}
		} catch (Exception e) {
			logger.warn("measuresInfo update/ get a error!");
			throw e;
		}
		return measuresinfo;
	}
	
}
