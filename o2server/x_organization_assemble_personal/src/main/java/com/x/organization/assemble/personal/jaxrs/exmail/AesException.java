package com.x.organization.assemble.personal.jaxrs.exmail;

/**
 * 
 * @author ray
 *
 */
public class AesException extends Exception {
	public static final int OK = 0;
	public static final int ValidateSignatureError = -40001;
	public static final int ParseXmlError = -40002;
	public static final int ComputeSignatureError = -40003;
	public static final int IllegalAesKey = -40004;
	public static final int ValidateCorpidError = -40005;
	public static final int EncryptAESError = -40006;
	public static final int DecryptAESError = -40007;
	public static final int IllegalBuffer = -40008;
	private int code;

	private static String getMessage(int code) {
		switch (code) {
		case ValidateSignatureError:
			return "签名验证错误";
		case ParseXmlError:
			return "xml解析失败";
		case ComputeSignatureError:
			return "sha加密生成签名失败";
		case IllegalAesKey:
			return "SymmetricKey非法";
		case ValidateCorpidError:
			return "corpid校验失败";
		case EncryptAESError:
			return "aes加密失败";
		case DecryptAESError:
			return "aes解密失败";
		case IllegalBuffer:
			return "解密后得到的buffer非法";
		default:
			return null;
		}
	}

	public int getCode() {
		return code;
	}

	AesException(int code) {
		super(getMessage(code));
		this.code = code;
	}
}
