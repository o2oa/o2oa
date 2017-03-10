package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.DocumentPictureInfo;

@Wrap( DocumentPictureInfo.class )
public class WrapOutDocumentPictureInfo extends DocumentPictureInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
}