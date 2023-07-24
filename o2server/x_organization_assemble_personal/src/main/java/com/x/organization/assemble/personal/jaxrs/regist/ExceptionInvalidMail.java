package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidMail extends PromptException {

   private static final long serialVersionUID = 4622760821556680073L;

    ExceptionInvalidMail(String mail) {
       super("邮件地址错误:不符合格式要求:" + Objects.toString(mail) + ".");
   }
}
