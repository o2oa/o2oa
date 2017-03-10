package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.DocumentViewRecord;

@Wrap( DocumentViewRecord.class )
public class WrapOutDocumentViewRecord extends DocumentPermission{
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}