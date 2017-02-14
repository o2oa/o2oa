package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.catagoryinfo.WrapOutCatagoryInfo;
import com.x.cms.core.entity.AppInfo;

@Wrap( AppInfo.class )
public class WrapOutAppInfo extends AppInfo implements Comparable<Object> {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	private List<WrapOutCatagoryInfo> wrapOutCatagoryList = null;
	
	/**
	 * 排序的实现方式
	 */
	@Override
	public int compareTo( Object o ) {
		String seq1 = ((this.getAppInfoSeq() == null) ? "" : this.getAppInfoSeq());
		String seq2 = ((((WrapOutAppInfo)o).getAppInfoSeq() == null) ? "" : ((WrapOutAppInfo)o).getAppInfoSeq());		
		return seq1.compareTo(seq2);
	}

	public List<WrapOutCatagoryInfo> getWrapOutCatagoryList() {
		return wrapOutCatagoryList;
	}

	public void setWrapOutCatagoryList(List<WrapOutCatagoryInfo> wrapOutCatagoryList) {
		this.wrapOutCatagoryList = wrapOutCatagoryList;
	}
	
}