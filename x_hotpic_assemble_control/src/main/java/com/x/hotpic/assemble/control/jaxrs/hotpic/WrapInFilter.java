package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.hotpic.entity.HotPictureInfo;

@Wrap( HotPictureInfo.class)
public class WrapInFilter{
	
	@EntityFieldDescribe( "应用名称：CMS|BBS等等." )
	private String application = null;
	
	@EntityFieldDescribe( "信息ID." )
	private String infoId = null;
	
	@EntityFieldDescribe( "信息标题，模糊查询." )
	private String title = null;
	
	public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodifies );

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	
}