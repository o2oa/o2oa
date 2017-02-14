package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.hotpic.entity.HotPictureInfo;

@Wrap( HotPictureInfo.class)
public class WrapInHotPictureInfo extends HotPictureInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
}
