package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutString;

public class ActionGetCustomerNameById extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	protected  ActionResult<WrapOutString> execute(String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
/*		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CustomerBaseInfo customerbaseinfo = new CustomerBaseInfo();
			customerbaseinfo = emc.find(id, CustomerBaseInfo.class);
			WrapOutString wrap = new WrapOutString();
			if (null == customerbaseinfo) {
				Exception exception = new CustomernameNullException();
				result.error(exception);
			} else {
				wrap.setValue(customerbaseinfo.getCustomername());
				result.setData(wrap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		//--------------------------
		return result;
		//--------------------------
	}


	
}
