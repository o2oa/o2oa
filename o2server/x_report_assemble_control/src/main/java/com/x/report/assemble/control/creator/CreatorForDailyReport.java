package com.x.report.assemble.control.creator;

import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;

public class CreatorForDailyReport implements ReportCreatorInf {

	/**
	 * TODO(uncomplete) 尝试生成每日汇报内容(暂未实现)
	 */
	@Override
	public Boolean create( EffectivePerson effectivePerson, ReportCreateFlag flag ) {
		return true;
	}

}
