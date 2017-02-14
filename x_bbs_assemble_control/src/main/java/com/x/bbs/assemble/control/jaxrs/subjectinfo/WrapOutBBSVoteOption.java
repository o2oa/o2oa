package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSVoteOption;

@Wrap( BBSVoteOption.class)
public class WrapOutBBSVoteOption extends BBSVoteOption{
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "图片选项时,optionBinary为图片的base64编码." )
	private String optionBinary = null;

	public String getOptionBinary() {
		return optionBinary;
	}

	public void setOptionBinary(String optionBinary) {
		this.optionBinary = optionBinary;
	}
	
}
