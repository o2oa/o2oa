package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMail extends LanguagePromptException {

   private static final long serialVersionUID = 4622760821556680073L;

    ExceptionInvalidMail(String mail) {
       super("邮件地址错误:不符合格式要求:{}.",Objects.toString(mail));
   }
}
