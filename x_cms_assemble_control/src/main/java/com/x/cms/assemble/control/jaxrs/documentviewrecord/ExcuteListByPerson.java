package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.documentviewrecord.exception.PersonNameEmptyException;
import com.x.cms.assemble.control.jaxrs.documentviewrecord.exception.ServiceLogicException;
import com.x.cms.core.entity.DocumentViewRecord;

public class ExcuteListByPerson extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListByPerson.class );
	
	protected ActionResult<List<WrapOutDocumentViewRecord>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name ) throws Exception {
		ActionResult<List<WrapOutDocumentViewRecord>> result = new ActionResult<>();
		List<WrapOutDocumentViewRecord> wraps = null;
		List<String> ids = null;
		List<DocumentViewRecord> documentViewRecordList = null;
		Boolean check = true;		

		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new PersonNameEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				ids = documentViewRecordServiceAdv.listByPerson( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e,"系统在根据人员姓名查询文档访问信息列表时发生异常。Name：" + name );
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
					Exception exception = new ServiceLogicException( e,"系统在根据访问记录ID列表查询访问记录信息列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			if( documentViewRecordList != null && !documentViewRecordList.isEmpty() ){
				try {
					wraps = WrapTools.documentViewRecord_wrapout_copier.copy( documentViewRecordList );
					result.setCount( Long.parseLong( wraps.size() + "" ) );
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