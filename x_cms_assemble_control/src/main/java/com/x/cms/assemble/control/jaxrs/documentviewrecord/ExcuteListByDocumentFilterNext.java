package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.documentviewrecord.exception.DocumentIdEmptyException;
import com.x.cms.assemble.control.jaxrs.documentviewrecord.exception.ServiceLogicException;
import com.x.cms.core.entity.DocumentViewRecord;

public class ExcuteListByDocumentFilterNext extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListByDocumentFilterNext.class );
	
	protected ActionResult<List<WrapOutDocumentViewRecord>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String docId, String id, Integer count ) throws Exception {
		ActionResult<List<WrapOutDocumentViewRecord>> result = new ActionResult<>();
		List<WrapOutDocumentViewRecord> wraps = null;
		List<DocumentViewRecord> documentViewRecordList = null;
		Long total = null;
		Boolean check = true;		

		if( check ){
			if( docId == null || docId.isEmpty() ){
				check = false;
				Exception exception = new DocumentIdEmptyException();
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
				Exception exception = new ServiceLogicException( e,"系统在根据文档ID查询文档访问信息总数时发生异常。ID：" + docId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				documentViewRecordList = documentViewRecordServiceAdv.listNextWithDocIds( id, docId, count, "DESC" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e,"系统在根据文档ID查询文档访问信息列表时发生异常。ID：" + docId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( documentViewRecordList != null && !documentViewRecordList.isEmpty() ){
				try {
					wraps = WrapTools.documentViewRecord_wrapout_copier.copy( documentViewRecordList );
					result.setCount( total );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new ServiceLogicException( e,"系统将查询结果转换为可输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}