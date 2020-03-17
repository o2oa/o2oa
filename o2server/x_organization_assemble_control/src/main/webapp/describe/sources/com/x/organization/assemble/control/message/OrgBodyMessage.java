package com.x.organization.assemble.control.message;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

public class OrgBodyMessage {
	
	public  String originalData;
	public  String modifyData;
	

	public String getOriginalData() {
		return originalData;
	}
	
	public void setOriginalData(String originalData) {
		this.originalData = originalData;
	}
	
	public String getModifyData() {
		return modifyData;
	}
	
	public void setModifyData(String modifyData) {
		this.modifyData = modifyData;
	}
	
    
}
