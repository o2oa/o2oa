package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.NumberTools;

public class Organization extends ConfigObject {

	private static final long serialVersionUID = -2193428649985413384L;

	public static final Integer DEFAULT_UNITLEVELORDERNUMBERDIGITS = 10;
	public static final Boolean DEFAULT_PICKPERSONWITHNAME = true;
	/* 默认改为false,避免自动补发待办 */
	public static final Boolean DEFAULT_PICKIDENTITYWITHNAME = false;

	public static Organization defaultInstance() {
		return new Organization();
	}

	@FieldDescribe("unit中unitLevelOrderNumber扩充位数,<=0不扩充.")
	private Integer unitLevelOrderNumberDigits = DEFAULT_UNITLEVELORDERNUMBERDIGITS;

	@FieldDescribe("zhangsan@123@P人员识别过程中过程为先查找 distinguishedName 再查找中间的 unique 如果还是没有查找到是否要通过名称进行查找.")
	private Boolean pickPersonWithName = DEFAULT_PICKPERSONWITHNAME;

	@FieldDescribe("zhangsan@456@I身份识别过程中过程为先查找 distinguishedName 再查找中间的 unique 如果还是没有查找到是否要通过名称进行查找.")
	private Boolean pickIdentityWithName = DEFAULT_PICKIDENTITYWITHNAME;

	public Integer getUnitLevelOrderNumberDigits() {
		return NumberTools.nullOrLessThan(this.unitLevelOrderNumberDigits, 1) ? DEFAULT_UNITLEVELORDERNUMBERDIGITS
				: this.unitLevelOrderNumberDigits;
	}

	public Boolean getPickPersonWithName() {
		return BooleanUtils.isTrue(this.pickPersonWithName);
	}

	public Boolean getPickIdentityWithName() {
		return BooleanUtils.isTrue(this.pickIdentityWithName);
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_ORGANIZATION);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_ORGANIZATION);
	}

}