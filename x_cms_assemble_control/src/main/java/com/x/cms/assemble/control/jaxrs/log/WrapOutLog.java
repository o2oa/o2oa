package com.x.cms.assemble.control.jaxrs.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Log;

@Wrap( Log.class )
public class WrapOutLog extends Log implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		Date seq1 = ((this.getCreateTime() == null) ? new Date() : this.getCreateTime());
		Date seq2 = ((((WrapOutLog)o).getCreateTime() == null) ? new Date() : ((WrapOutLog)o).getCreateTime());		
		return seq1.compareTo(seq2);
	}
}