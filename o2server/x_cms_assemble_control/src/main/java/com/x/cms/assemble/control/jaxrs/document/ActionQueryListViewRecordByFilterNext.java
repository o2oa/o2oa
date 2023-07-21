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

public class ActionQueryListViewRecordByFilterNext extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionQueryListViewRecordByFilterNext.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String docId, String id, Integer count ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<DocumentViewRecord> documentViewRecordList = null;
		Long total = null;
		Boolean check = true;

		if( check ){
			if( StringUtils.isEmpty(docId) ){
				check = false;
				Exception exception = new ExceptionDocumentIdEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				total = documentViewRecordServiceAdv.countWithDocIds(docId);
				if( total == null ){
					total = 0L;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic( e,"系统在根据文档ID查询文档访问信息总数时发生异常。ID：" + docId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				documentViewRecordList = documentViewRecordServiceAdv.listNextWithDocIds( id, docId, count, "DESC" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic( e,"系统在根据文档ID查询文档访问信息列表时发生异常。ID：" + docId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( documentViewRecordList != null && !documentViewRecordList.isEmpty() ){
				try {
					wraps = Wo.copier.copy( documentViewRecordList );
					result.setCount( total );
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
