package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;


public class ActionCaptchaLoginRSAPublicKey extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(ActionCaptchaLoginRSAPublicKey.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson) throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setPublicKey(getPublicKey());
			result.setData(wo);
			return result;
		}

	//获取PublicKey
	public String  getPublicKey() {
		String publicKey = "";
		 try {
			 publicKey = Config.publicKey();
			 byte[] publicKeyB = Base64.decodeBase64(publicKey);
			 publicKey = new String(Base64.encodeBase64(publicKeyB));
			 //logger.info("publicKey=" + publicKey);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}
	
	
	//获取privateKey
	public String  getPrivateKey() {
		 String privateKey = "";
		 try {
			 privateKey = Config.privateKey();
			 byte[] privateKeyB = Base64.decodeBase64(privateKey);
			 privateKey = new String(Base64.encodeBase64(privateKeyB));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privateKey;
	}
	

	public static class Wo  extends GsonPropertyObject {

		@FieldDescribe("RSA公钥")
		private String publicKey;

		public String getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}
	}

}