package com.x.cms.assemble.control.jaxrs.catagoryinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.CatagoryInfo;

@Wrap( CatagoryInfo.class )
public class WrapOutCatagoryInfo extends CatagoryInfo implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		String seq1 = ((this.getCatagorySeq() == null) ? "" : this.getCatagorySeq());
		String seq2 = ((((WrapOutCatagoryInfo)o).getCatagorySeq() == null) ? "" : ((WrapOutCatagoryInfo)o).getCatagorySeq());		
		return seq1.compareTo(seq2);
	}
}