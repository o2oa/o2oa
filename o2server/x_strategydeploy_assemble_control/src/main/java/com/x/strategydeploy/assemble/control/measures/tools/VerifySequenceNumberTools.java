package com.x.strategydeploy.assemble.control.measures.tools;

import org.apache.commons.lang3.StringUtils;

public class VerifySequenceNumberTools {
	public static boolean VerifySequenceNumber(String _sn) {
		//小数点左右两侧，都为Integer.valueOf后的非“0” 正整数
		boolean ispass = true;
		int countMatches = StringUtils.countMatches(_sn, ".");
		//System.out.println("countMatches:" + countMatches);
		if (countMatches <= 0) {
			//必须有一个“.”
			//System.out.println("SequenceNumber must be like 1.1,1.10");
			//SequenceNumber must be like 1.1,1.10
			return false;
		} else {
			String[] _array = _sn.split("\\.");
			String _leftStr = _array[0];
			String _rightStr = _array[0];

			for (int i = _leftStr.length(); --i >= 0;) {
				if (!Character.isDigit(_leftStr.charAt(i))) {
					ispass = false;
				}
			}

			for (int i = _rightStr.length(); --i >= 0;) {
				if (!Character.isDigit(_rightStr.charAt(i))) {
					ispass = false;
				}
			}

			if (!ispass) {
				//小数点左侧、右侧，必须都是数字。
				//SequenceNumber must be like 1.1,1.10
				return false;
			} else {
				Integer _leftinteger = Integer.valueOf(_leftStr);
				Integer _rightinteger = Integer.valueOf(_rightStr);
				//System.out.println("_leftinteger:" + _leftinteger);
				if (_leftinteger == 0 || _rightinteger == 0) {
					//小数点左侧、右侧，不能都是“0”。
					//System.out.println("SequenceNumber must be like 1.13,1.20;not like 000.10，000.000");
					//SequenceNumber must be like 1.1,1.10
					return false;
				} else {
					if (NumberValidationUtils.isPositiveInteger("" + _leftinteger) && NumberValidationUtils.isPositiveInteger("" + _rightinteger)) {
						//System.out.println("ok，是数字");
						return true;
					} else {
						//System.out.println("小数点左右两侧，都为格式化后的非“0” 正整数");
						return false;
					}
				}
			}
		}
	}
}
