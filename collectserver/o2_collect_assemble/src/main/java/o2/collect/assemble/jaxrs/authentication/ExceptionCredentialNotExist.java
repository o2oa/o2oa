package o2.collect.assemble.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionCredentialNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionCredentialNotExist(String credential) {
		super("凭证:{},不存在.", credential);
	}

}