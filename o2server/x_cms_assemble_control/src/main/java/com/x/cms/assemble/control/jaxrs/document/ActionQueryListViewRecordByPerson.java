package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.DocumentViewRecord;

public class ActionQueryListViewRecordByPerson extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionQueryListViewRecordByPerson.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<DocumentViewRecord> documentViewRecordList = null;
		Boolean check = true;

		if( check ){
			if( StringUtils.isEmpty(name) ){
				check = false;
				Exception exception = new ExceptionPersonNameEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				ids = documentViewRecordServiceAdv.listByPerson( name, 100 );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic( e,"系统在根据人员姓名查询文档访问信息列表时发生异常。Name：" + name );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					documentViewRecordList = documentViewRecordServiceAdv.list(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionServiceLogic( e,"系统在根据访问记录ID列表查询访问记录信息列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			if( documentViewRecordList != null && !documentViewRecordList.isEmpty() ){
				try {
					wraps = Wo.copier.copy( documentViewRecordList );
					result.setCount( Long.parseLong( wraps.size() + "" ) );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionServiceLogic( e,"系统将查询结果转换为可输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends DocumentViewRecord{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<DocumentViewRecord, Wo> copier = WrapCopierFactory.wo( DocumentViewRecord.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
