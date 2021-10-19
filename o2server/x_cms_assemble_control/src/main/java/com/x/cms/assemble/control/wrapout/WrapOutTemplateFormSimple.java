package com.x.cms.assemble.control.wrapout;

public class WrapOutTemplateFormSimple extends WrapOutTemplateForm {

	private static final long serialVersionUID = 2735294690359613313L;

	static {
		excludes.add("data");
		excludes.add("mobileData");
	}

}
