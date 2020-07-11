package com.dfusiontech.server.auth.mfa;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * oAuth2 Multi Factor Authentication Exception
 *
 * @author Eugene A. Kalosha <ekalosha@dfusiontech.com>
 */
public class MFARequiredException extends OAuth2Exception {

	/**
	 * MFA error Constructor
	 *
	 * @param mfaToken
	 */
	public MFARequiredException(String mfaToken) {
		super("Multi-factor authentication required");
        this.addAdditionalInformation("mfa_token", mfaToken);
	}

	/**
	 * Get oAuth Error Code
	 *
	 * @return
	 */
	public String getOAuth2ErrorCode() {
		return "mfa_required";
	}

	public int getHttpErrorCode() {
		return 403;
	}
}
