package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.config.Mpweixin;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

/**
 * 绑定公众号openid 到当前登录用户
 * Created by fancyLou on 3/8/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionBindWithCode extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionBindWithCode.class);

    ActionResult<Wo>  execute(EffectivePerson effectivePerson, String code) throws Exception {

        if (StringUtils.isEmpty(code)) {
            throw new ExceptionNoCode();
        }
        if (Config.mpweixin() == null || BooleanUtils.isFalse(Config.mpweixin().getEnable())) {
            throw new ExceptionConfigError();
        }
        ActionResult<Wo> result = new ActionResult<Wo>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Mpweixin.WeixinAuth2AccessResp resp = Config.mpweixin().mpAuth2(code);
            if (resp == null) {
                throw new ExceptionGetAccessTokenFail();
            }
            logger.info("获取到用户openid accessToken ，" + resp.toString());
            String dn = effectivePerson.getDistinguishedName();
            Business business = new Business(emc);
            String personId = business.person().getWithCredential(dn);
            Person person = emc.find(personId, Person.class);
            person.setMpwxopenId(resp.getOpenid());
            emc.beginTransaction(Person.class);
            emc.persist(person, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }

    public static class Wo extends WrapBoolean {

    }
}
