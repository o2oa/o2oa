package com.x.crm.assemble.control.jaxrs.customer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.jaxrs.WoId;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.core.entity.CustomerBaseInfo;

public class ActionCreate extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	protected ActionResult<WoId> execute(HttpServletRequest request, JsonElement jsonElement) throws Exception {
		ActionResult<WoId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getDistinguishedName();
			Business business = new Business(emc);
			CustomerBaseInfo customer = new CustomerBaseInfo();
			boolean isPassTest = true;
			boolean isCreate = true;
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			//如果传入的id为空，那么一定是创建新用户；
			//如果传入的id不为空，用此id查找用户，如果用户存在，则为更新；如果用户存在则认为是创建
			String _WrapInId = wi.getId();
			if (StringUtils.isBlank(_WrapInId)) {
				isCreate = true;
			} else {
				if (business.customerBaseInfoFactory().IsExistById(wi.getId())) {
					isCreate = false;
				} else {
					isCreate = true;
				}
			}

			if (isCreate) {
				//创建新的客户

				//客户名称必须填写
				if (StringUtils.isEmpty(wi.getCustomername())) {
					Exception exception = new CustomernameNullException();
					result.error(exception);
					isPassTest = false;
				}

				//检查属于登陆人的客户名称是否唯一
				if (business.customerBaseInfoFactory().checkCustomerByCustomerName(wi.getCustomername(), _loginPersonName)) {
					Exception exception = new CustomerMustUniqueException();
					result.error(exception);
					isPassTest = false;
				}

				if (isPassTest) {
					//logger.error("_creator:" + _loginPersonName);
					customer = WrapCrmTools.CustomerBaseInfoInCopier_create.copy(wi);
					customer.setCreatorname(_loginPersonName);
					String customersequence = business.customerBaseInfoFactory().defaultSequence();
					customer.setCustomersequence(customersequence);
					emc.beginTransaction(CustomerBaseInfo.class);
					emc.persist(customer);
					emc.commit();
					_WoId _woid = new _WoId();
					_woid.setId(customer.getId());
					result.setData(_woid);
				}

			} else {
				//更新一个客户

				//客户名称必须填写
				if (StringUtils.isEmpty(wi.getCustomername())) {
					Exception exception = new CustomernameNullException();
					result.error(exception);
					isPassTest = false;
				}

				if (isPassTest) {
					customer = emc.find(wi.getId(), CustomerBaseInfo.class);
					emc.beginTransaction(CustomerBaseInfo.class);
					//BeanUtils.copyProperties(customer,WrapIncustomer);
					WrapCrmTools.CustomerBaseInfoInCopier_update.copy(wi, customer);
					emc.persist(customer, CheckPersistType.all);
					emc.commit();
					_WoId _woid = new _WoId();
					_woid.setId(customer.getId());
					result.setData(_woid);
				}
			}

		}
		return result;
	}


}
