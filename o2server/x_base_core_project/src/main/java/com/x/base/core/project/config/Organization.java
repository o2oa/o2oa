package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.NumberTools;

public class Organization extends ConfigObject {

	private static final long serialVersionUID = -2193428649985413384L;

	public final static Integer DEFAULT_UNITLEVELORDERNUMBERDIGITS = 10;

	public static Organization defaultInstance() {
		return new Organization();
	}

	@FieldDescribe("unit中unitLevelOrderNumber扩充位数,<=0不扩充.")
	private Integer unitLevelOrderNumberDigits = DEFAULT_UNITLEVELORDERNUMBERDIGITS;

	public Integer getUnitLevelOrderNumberDigits() {
		return NumberTools.nullOrLessThan(this.unitLevelOrderNumberDigits, 1) ? DEFAULT_UNITLEVELORDERNUMBERDIGITS
				: this.unitLevelOrderNumberDigits;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_ORGANIZATION);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_ORGANIZATION);
	}

}