package com.x.strategydeploy.assemble.common.checkuuid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Checkuuid {

	public boolean IsUUIDString(String _uuid) {
		boolean rs = false;
		if (StringUtils.isBlank(_uuid)) {
			return rs;
		} else {
			String regEx = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
			// 忽略大小写的写法
			Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(_uuid);
			rs = matcher.matches();
			return rs;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*		String str = "6c83c287-2119-41cb-9bba-e276a1bd34ca";
				//uuid表达式
				String regEx = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
				// 编译正则表达式
				
				//Pattern pattern = Pattern.compile(regEx);
				// 忽略大小写的写法
				Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		
				Matcher matcher = pattern.matcher(str);
		
				// 字符串是否与正则表达式相匹配
		
				boolean rs = matcher.matches();*/
		Checkuuid checkuuid = new Checkuuid();
		boolean res = checkuuid.IsUUIDString("6c83c287-2119-41cb-9bba-e276a1bd34ca");
		System.out.println(res);
		String testString = "A​‌B​‌C​‌Ć​‌Č​‌D​‌Đ​‌E​‌F​‌G​‌H​‌I​‌J​‌K​‌L​‌M​‌N​‌O​‌P​‌Q​‌R​‌S​‌Š​‌T​‌U​‌V​‌W​‌X​‌Y​‌Z​‌Ž​‌a​‌b​‌c​‌č​‌ć​‌d​‌đ​‌e​‌f​‌g​‌h​‌i​‌j​‌k​‌l​‌m​‌n​‌o​‌p​‌q​‌r​‌s​‌š​‌t​‌u​‌v​‌w​‌x​‌y​‌z​‌ž​‌Ă​‌Â​‌Ê​‌Ô​‌Ơ​‌Ư​‌ă​‌â​‌ê​‌ô​‌ơ​‌ư​‌1​‌2​‌3​‌4​‌5​‌6​‌7​‌8​‌9​‌0​‌‘​‌?​‌’​‌“​‌!​‌”​‌(​‌%​‌)​‌[​‌#​‌]​‌{​‌@​‌}​‌/​‌&​‌\\​‌<​‌-​‌+​‌÷​‌×​‌=​‌>​‌®​‌©​‌$​‌€​‌£​‌¥​‌¢​‌:​‌;​‌,​‌.​‌*";
		System.out.println(testString);
	}

}
