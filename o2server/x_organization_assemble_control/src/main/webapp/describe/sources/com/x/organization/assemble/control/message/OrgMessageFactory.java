package com.x.organization.assemble.control.message;

import com.google.gson.Gson;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.ThisApplication;

/**创建 组织变更org消息通信 */
public class OrgMessageFactory <T extends SliceJpaObject>{

	private static Logger logger = LoggerFactory.getLogger(OrgMessageFactory.class);
	
	public  boolean  createMessageCommunicate(String strOperType, String strOrgType, T t, EffectivePerson effectivePerson) {
		try{
			Gson gson = new Gson();
			String strT = gson.toJson(t);
			OrgMessage orgMessage = new OrgMessage();
			
			orgMessage.setOperType(strOperType);
			orgMessage.setOrgType(strOrgType);
			orgMessage.setOperUerId(effectivePerson.getDistinguishedName());
			orgMessage.setOperDataId(t.getId());
			orgMessage.setReceiveSystem("");
			orgMessage.setConsumed(false);
			orgMessage.setConsumedModule("");
			
			OrgBodyMessage orgBodyMessage = new OrgBodyMessage();
			orgBodyMessage.setOriginalData(strT);
			orgMessage.setBody( gson.toJson(orgBodyMessage));
			
			String path ="org/create"; 
			ActionResponse resp =  ThisApplication.context().applications()
						.postQuery(x_message_assemble_communicate.class, path, orgMessage);
		
			String mess = resp.getMessage();
			String data = resp.getData().toString();
			return true;
			}catch(Exception e) {
				logger.print(e.toString());
				return false;
			}	
	}
	
	/**创建 组织变更org消息通信 */
	public  boolean createMessageCommunicate(String strOperType, String strOrgType,String strOriginaGroup,T t, EffectivePerson effectivePerson) {
		try{
			Gson gson = new Gson();
			String strT = gson.toJson(t);
			OrgMessage orgMessage = new OrgMessage();
			orgMessage.setOperType(strOperType);
			orgMessage.setOrgType(strOrgType);
			orgMessage.setOperUerId(effectivePerson.getDistinguishedName());
			orgMessage.setOperDataId(t.getId());
			orgMessage.setReceiveSystem("");
			orgMessage.setConsumed(false);
			orgMessage.setConsumedModule("");
			
			OrgBodyMessage orgBodyMessage = new OrgBodyMessage();
			orgBodyMessage.setOriginalData(strOriginaGroup);
			orgBodyMessage.setModifyData(strT);
			
			orgMessage.setBody( gson.toJson(orgBodyMessage));
			
			String path ="org/create";
			ActionResponse resp =  ThisApplication.context().applications()
						.postQuery(x_message_assemble_communicate.class, path, orgMessage);
		
			String mess = resp.getMessage();
			String data = resp.getData().toString();
			return true;
			}catch(Exception e) {
				logger.print(e.toString());
				return false;
			}	
	}
	
}
