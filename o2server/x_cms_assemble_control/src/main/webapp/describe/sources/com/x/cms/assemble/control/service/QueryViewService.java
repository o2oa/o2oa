package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.SelectEntries;
import com.x.query.core.express.plan.SelectEntry;

/**
 * 查询数据中心视图
 * @author O2LEE
 *
 */
public class QueryViewService {
	
	private static Gson gson = XGsonBuilder.instance();
	
	public View getQueryView( String id ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( id, View.class );
		}
	}
	
	public List<String> listColumnsFromQueryView( String id ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			View view = emc.find( id, View.class );
			if (null == view) {
				throw new Exception("view is not exists.id:" + id);
			}
			CmsPlan cmsPlan = gson.fromJson( view.getData(), CmsPlan.class );
			SelectEntries selectEntries = cmsPlan.selectList;
			List<String> columnNames = new ArrayList<>();
			for (SelectEntry o : selectEntries ) {
				columnNames.add( o.column );
			}
			return columnNames;
		}
	}
	
	public List<String> listColumnsFromQueryView( View view ) throws Exception{
		if (null == view) {
			throw new Exception("view is null");
		}
		CmsPlan cmsPlan = gson.fromJson( view.getData(), CmsPlan.class );
		SelectEntries selectEntries = cmsPlan.selectList;
		List<String> columnNames = new ArrayList<>();
		for (SelectEntry o : selectEntries ) {
			columnNames.add( o.column );
		}
		return columnNames;
	}
	
	public List<String> listColumnPathsFromQueryView( View view ) throws Exception{
		if (null == view) {
			throw new Exception("view is null");
		}
		CmsPlan cmsPlan = gson.fromJson( view.getData(), CmsPlan.class );
		SelectEntries selectEntries = cmsPlan.selectList;
		List<String> columnNames = new ArrayList<>();
		for (SelectEntry o : selectEntries ) {
			if( o.path != null ) {
				o.path = o.path.trim();
			}
			if( StringUtils.isNotEmpty(o.path)) {
				columnNames.add( o.path );
			}
		}
		return columnNames;
	}
}
