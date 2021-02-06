package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.tools.MD5Tool;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionCaptchaLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCaptchaLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			String credential = wi.getCredential();
			String password = wi.getPassword();

			String isEncrypted = wi.getIsEncrypted();

			// RSA解秘
			if (!StringUtils.isEmpty(isEncrypted)) {
				if (isEncrypted.trim().equalsIgnoreCase("y")) {
					password = this.decryptRSA(password);
				}
			}

			String captcha = wi.getCaptcha();
			String captchaAnswer = wi.getCaptchaAnswer();
			if (StringUtils.isEmpty(credential)) {
				throw new ExceptionCredentialEmpty();
			}
			if (StringUtils.isEmpty(password)) {
				throw new ExceptionPasswordEmpty();
			}
			/* 可以通过设置跳过图片验证码. */
			if (Config.person().getCaptchaLogin()) {
				if (StringUtils.isEmpty(captcha) || StringUtils.isEmpty(captchaAnswer)) {
					throw new ExceptionCaptchaEmpty();
				}
				if (!business.instrument().captcha().validate(captcha, captchaAnswer)) {
					throw new ExceptionInvalidCaptcha();
				}
			}
			if (Config.token().isInitialManager(credential)) {
				if (!StringUtils.equals(Config.token().getPassword(), password)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.manager(request, response, business, Wo.class);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色.增加同中文的认证 */
				String personId = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(personId)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}

				Person o = null;
				// 处理同中文问题
				if (personId.indexOf(",") > -1) {
					String[] arrPerson = personId.split(",");
					for (int i = 0; i < arrPerson.length; i++) {
						personId = arrPerson[i];
						o = emc.find(personId, Person.class);
						if (StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), o.getPassword())
								|| StringUtils.equals(MD5Tool.getMD5Str(password), o.getPassword())) {
							break;
						}
					}
				} else {
					o = emc.find(personId, Person.class);
				}

				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), password)) {
					logger.warn("user: {} use superPermission.", credential);
				} else {
					if (this.failureLocked(o)) {
						throw new ExceptionFailureLocked(o.getName(), Config.person().getFailureInterval());
					} else {
						if (!StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), o.getPassword())
								&& !StringUtils.equals(MD5Tool.getMD5Str(password), o.getPassword())) {
							emc.beginTransaction(Person.class);
							this.failure(o);
							emc.commit();
							throw new ExceptionPersonNotExistOrInvalidPassword();
						}
					}
				}

				wo = this.user(request, response, business, o, Wo.class);
				audit.log(o.getDistinguishedName(), "登录");
			}
			result.setData(wo);
			return result;
		}
	}

	// 用户登入解密
	public String decryptRSA(String strDecrypt) {
		String privateKey;
		String decrypt = null;
		try {
			privateKey = getPrivateKey();
			decrypt = Crypto.rsaDecrypt(strDecrypt, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}

	// 转成Base64
	public String encryptRSA(String strEncrypt) {
		String encrypt = null;
		try {
			String publicKey = Config.publicKey();
			byte[] publicKeyB = Base64.decodeBase64(publicKey);

			encrypt = Crypto.rsaEncrypt(strEncrypt, new String(Base64.encodeBase64(publicKeyB)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypt;
	}

	// 转成Base64
	public String getPublicKey() {
		String publicKey = "";
		try {
			publicKey = Config.publicKey();
			byte[] publicKeyB = Base64.decodeBase64(publicKey);
			publicKey = new String(Base64.encodeBase64(publicKeyB));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	// 转成Base64
	public String getPrivateKey() {
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

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("凭证")
		private String credential;

		@FieldDescribe("密码")
		private String password;

		@FieldDescribe("图片认证编号")
		private String captcha;

		@FieldDescribe("图片认证码")
		private String captchaAnswer;

		@FieldDescribe("是否启用加密,默认不加密,启用(y)。注意:使用加密先要在服务器运行 create encrypt key")
		private String isEncrypted;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

		public String getCaptcha() {
			return captcha;
		}

		public void setCaptcha(String captcha) {
			this.captcha = captcha;
		}

		public String getCaptchaAnswer() {
			return captchaAnswer;
		}

		public void setCaptchaAnswer(String captchaAnswer) {
			this.captchaAnswer = captchaAnswer;
		}

		public String getIsEncrypted() {
			return isEncrypted;
		}

		public void setIsEncrypted(String isEncrypted) {
			this.isEncrypted = isEncrypted;
		}
	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 4940814657548190978L;
	}

}
