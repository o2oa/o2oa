package com.x.report.assemble.control.jaxrs.workprog;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionSaveWorkProg;

public class ActionUpdateOrderNumber extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionUpdateOrderNumber.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wi wi = null;
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		List<WiProgOrder> progOrderList = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			progOrderList = wi.getOrderList();
			if( ListTools.isEmpty( progOrderList ) ) {
				return result;
			}
		}
		
		if( check ){
			for( WiProgOrder progOrder : progOrderList ) {
				try {			
					report_C_WorkProgServiceAdv.updateOrderNumber( progOrder.getId(), progOrder.getOrderNumber() );
					wrapOutBoolean.setValue( true );
					result.setData( wrapOutBoolean );
				} catch (Exception e) {
					wrapOutBoolean.setValue( false );
					result.setData( wrapOutBoolean );
					check = false;
					Exception exception = new ExceptionSaveWorkProg( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "工作完成情况顺序" )
		private List<WiProgOrder> orderList = null;

		public List<WiProgOrder> getOrderList() {
			return orderList;
		}

		public void setOrderList(List<WiProgOrder> orderList) {
			this.orderList = orderList;
		}
	}
	
	public static class WiProgOrder {
		
		@FieldDescribe( "工作完成情况信息ID" )
		private String id = "";
		
		@FieldDescribe( "排序号" )
		private Integer orderNumber = 0;

		public String getId() {
			return id;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}