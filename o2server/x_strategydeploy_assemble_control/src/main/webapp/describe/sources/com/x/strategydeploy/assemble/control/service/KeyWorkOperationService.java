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
import com.x.strategydeploy.assemble.control.keywork.BaseAction;
import com.x.strategydeploy.assemble.control.keywork.BaseAction.Wi;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class KeyWorkOperationService {
	private static  Logger logger = LoggerFactory.getLogger(KeyWorkOperationService.class);

	private KeyWorkInfoExcuteSave keyWorkInfoExcuteSave = new KeyWorkInfoExcuteSave();

	List<String> reader_groups = new ArrayList<>();
	List<String> writer_groups = new ArrayList<>();
	List<String> dept_writer_groups = new ArrayList<>();

	public KeyWorkOperationService() {
		//战略读者(公司级别)
		reader_groups.add("战略读者@strategy_reader_g@G");
		//战略作者(公司级别)
		writer_groups.add("战略管理者@strategy_writer_g@G");
		//部门战略作者(部门级别)
		dept_writer_groups.add("部门战略管理员@strategy_dept_writer_g@G");
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
	
	public List<String> getDept_writer_groups() {
		return dept_writer_groups;
	}

	public void setDept_writer_groups(List<String> dept_writer_groups) {
		this.dept_writer_groups = dept_writer_groups;
	}

	/**
	 * 向数据库保存举措对象
	 * 
	 * @param wrapIn
	 */
	public KeyworkInfo save(BaseAction.Wi wrapIn) throws Exception {
		KeyworkInfo keyworkinfo = new KeyworkInfo();
		if (wrapIn == null) {
			throw new Exception("KeyworkInfo is null!");
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
				IsExist = business.keyworkInfoFactory().IsExistById(_id);
			}

			if (IsExist) {
				logger.info("IsExistById true");
				// IsExist true 如果文档存在，那么更新。更新的时候id和name不改变。
				KeyworkInfo origin_keyworkinfo = business.keyworkInfoFactory().getById(_id);

				excludes.add("id");
				excludes.add("keyworkinfotitle");
				WrapCopier<BaseAction.Wi, KeyworkInfo> beanCopyTools = WrapCopierFactory.wi(BaseAction.Wi.class,
						KeyworkInfo.class, null, excludes);
				beanCopyTools.copy(wrapIn, origin_keyworkinfo);
				keyworkinfo = keyWorkInfoExcuteSave.save(emc, origin_keyworkinfo);

			} else {
				// IsExist false 如果文档不存在，那么创建
				logger.info("IsExistById false");
				keyworkinfo = Wi.copier.copy(wrapIn);
				keyworkinfo = keyWorkInfoExcuteSave.save(emc, keyworkinfo);
			}
		} catch (Exception e) {
			logger.warn("measuresInfo update/ get a error!");
			throw e;
		}
		return keyworkinfo;
	}
}
