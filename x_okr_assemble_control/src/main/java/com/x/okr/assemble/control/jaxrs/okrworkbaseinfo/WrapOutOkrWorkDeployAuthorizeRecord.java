package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrWorkDeployAuthorizeRecord.class)
public class WrapOutOkrWorkDeployAuthorizeRecord{

	@EntityFieldDescribe( "工作ID" )
	private String workId = "";
	
	@EntityFieldDescribe( "工作标题" )
	private String workTitle = "";
	
	@EntityFieldDescribe( "工作部署|授权操作者身份" )
	private String source = "";
	
	@EntityFieldDescribe( "工作部署|授权操作接收者身份" )
	private String target;
	
	@EntityFieldDescribe( "工作部署|授权操作时间" )
	private String operationTime;
	
	@EntityFieldDescribe( "工作部署|授权操作类型:授权|收回|部署" )
	private String operationTypeCN;
	
	@EntityFieldDescribe( "DEPLOY|AUTHORIZE|TACKBACK" )
	private String operationType;
	
	@EntityFieldDescribe( "工作部署|授权操作意见" )
	private String opinion;
	
	private String description = "";

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	
	public String getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getOperationTypeCN() {
		return operationTypeCN;
	}

	public void setOperationTypeCN(String operationTypeCN) {
		this.operationTypeCN = operationTypeCN;
	}
	
}
