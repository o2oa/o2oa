package com.x.report.assemble.control.creator;

import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;

/**
 * 汇报生成接口
 * 
 * @author O2LEE
 *
 */
public interface ReportCreatorInf {

	/**
	 * 生成指定汇报并且发起汇报流程<br/>
	 * 
	 * 1、根据指定的应用模块，查询汇报依据，组织【汇报概要文件】，持久化到数据库<br/>
	 * 2、再依据【汇报概要文件】文件生成汇报文档并且启动汇报流程<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * 1) 根据【汇报概文件】要为依据，创建所有涉及汇报的个人以及部门汇报文档<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * 2) 如果【汇报文档】成功生成，那么开始启动汇报流程<br/>
	 * 
	 * @throws Exception 
	 */
	Boolean create( EffectivePerson effectivePerson, ReportCreateFlag flag ) throws Exception;
}
