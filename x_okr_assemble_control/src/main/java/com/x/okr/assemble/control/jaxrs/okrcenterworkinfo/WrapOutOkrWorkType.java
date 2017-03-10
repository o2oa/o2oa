package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrWorkType.class)
public class WrapOutOkrWorkType{

	@EntityFieldDescribe( "工作类别ID" )
	private String id = null;
	
	@EntityFieldDescribe( "工作类别名称" )
	private String workTypeName = null;
	
	@EntityFieldDescribe( "工作类别排序号" )
	private Integer orderNumber = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public WrapOutOkrWorkType(){
		
	}
	
	public WrapOutOkrWorkType( String id, String name, Integer orderNumber ){
		this.id = id;
		this.workTypeName = name;
		this.orderNumber = orderNumber;
	}
	
	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
