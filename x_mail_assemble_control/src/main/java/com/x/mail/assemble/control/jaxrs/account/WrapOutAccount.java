package com.x.mail.assemble.control.jaxrs.account;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.mail.core.entity.Account;

@Wrap( Account.class ) 
public class WrapOutAccount extends Account implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		String seq1 = ((this.getSequence() == null) ? "" : this.getSequence());
		String seq2 = ((((WrapOutAccount)o).getSequence() == null) ? "" : ((WrapOutAccount)o).getSequence());		
		return seq1.compareTo(seq2);
	}

}