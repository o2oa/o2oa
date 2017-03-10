package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Document;

@Wrap( Document.class )
public class WrapOutDocument extends Document implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		Date seq1 = ((this.getUpdateTime() == null) ? new Date() : this.getUpdateTime());
		Date seq2 = ((((WrapOutDocument)o).getUpdateTime() == null) ? new Date() : ((WrapOutDocument)o).getUpdateTime());		
		return seq1.compareTo(seq2);
	}
}