package com.x.cms.assemble.control.jaxrs.viewcatagory;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.ViewCatagory;

@Wrap( ViewCatagory.class )
public class WrapOutViewCatagory extends ViewCatagory implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		String seq1 = ((this.getSequence() == null) ? "" : this.getSequence());
		String seq2 = ((((WrapOutViewCatagory)o).getSequence() == null) ? "" : ((WrapOutViewCatagory)o).getSequence());		
		return seq1.compareTo(seq2);
	}
}