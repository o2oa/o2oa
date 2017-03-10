package com.x.cms.assemble.control.jaxrs.documentpermission;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.DocumentPermission;

@Wrap( DocumentPermission.class )
public class WrapOutDocumentPermission extends DocumentPermission{
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}