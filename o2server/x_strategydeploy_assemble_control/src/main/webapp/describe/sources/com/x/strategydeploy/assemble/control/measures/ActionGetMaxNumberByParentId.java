package com.x.strategydeploy.assemble.control.measures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.service.MeasuresInfoQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class ActionGetMaxNumberByParentId extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionGetMaxNumberByParentId.class);

	protected ActionResult<WrapOutString> execute(HttpServletRequest request, EffectivePerson effectivePerson, String parentid) throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<WrapOutString>();
		MeasuresInfoQueryService measuresInfoQueryService = new MeasuresInfoQueryService();
		List<MeasuresInfo> measuresinfoList = new ArrayList<MeasuresInfo>();
		measuresinfoList = measuresInfoQueryService.getListStrategyDeployId(parentid);
		List<Integer> umberList_Left = new ArrayList<Integer>();
		List<Integer> umberList_Right = new ArrayList<Integer>();
		String _sn = "";

		for (MeasuresInfo measuresinfo : measuresinfoList) {
			_sn = measuresinfo.getSequencenumber();
			String[] _array = _sn.split("\\.");
			Integer _leftinteger = Integer.valueOf(_array[0]);
			Integer _rightinteger = Integer.valueOf( _array[1]);
			umberList_Left.add(_leftinteger);
			umberList_Right.add(_rightinteger);
		}

		String _resultstr = String.valueOf(umberList_Left.get(0));
		Integer _maxVal = (Integer) Collections.max(umberList_Right);
		_maxVal++;
		logger.info("max："+ String.valueOf(_maxVal));
		_resultstr = _resultstr + "." + String.valueOf(_maxVal);
		logger.info("_resultstr："+_resultstr);
		WrapOutString wrapOutString = new WrapOutString();
		wrapOutString.setValue(_resultstr);
		result.setData(wrapOutString);
		return result;
	}
	

}
