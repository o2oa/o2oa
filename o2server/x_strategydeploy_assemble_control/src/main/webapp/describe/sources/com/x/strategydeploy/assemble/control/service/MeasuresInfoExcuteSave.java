package com.x.strategydeploy.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class MeasuresInfoExcuteSave {
	private static Logger logger = LoggerFactory.getLogger(MeasuresInfoExcuteSave.class);
	private static Integer formatLength = 3;

	public MeasuresInfo save(EntityManagerContainer emc, MeasuresInfo measuresinfo) throws Exception {
		measuresinfo = MeasuresInfoSetFormatSequencenumber(measuresinfo);
		emc.beginTransaction(MeasuresInfo.class);
		emc.persist(measuresinfo);
		emc.commit();
		return measuresinfo;
	}

	public static MeasuresInfo MeasuresInfoSetFormatSequencenumber(MeasuresInfo measuresinfo) {
		String sequencenumber = measuresinfo.getSequencenumber();
	
		if (null != sequencenumber && StringUtils.isNotBlank(sequencenumber)) {
			double d;
			d = FormatZero(sequencenumber);
			measuresinfo.setFormatsequencenumber(d);
			return measuresinfo;
		} else {
			return measuresinfo;
		}

	}

	public static double FormatZero(String _doubleStr) {
		System.out.println(_doubleStr.split("\\.").length);
		String[] _array = _doubleStr.split("\\.");
		String leftnum = addZeroForNum(_array[0].toString(), formatLength);
		String rightnum = addZeroForNum(_array[1].toString(), formatLength);
		String resultStr = leftnum + "." + rightnum;
		double d = Double.valueOf(resultStr).doubleValue();
		return d;
	}

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
}
