package com.dfusiontech.server.auth.mfa;

import com.dfusiontech.server.auth.OAuthClientAuthority;
import com.dfusiontech.server.auth.UserDetailsImpl;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.model.jpa.domain.IdpType;
import com.dfusiontech.server.model.jpa.entity.AuditLogItemId;
import com.dfusiontech.server.model.jpa.entity.IdpUsers;
import com.dfusiontech.server.model.jpa.entity.Organizations;
import com.dfusiontech.server.model.jpa.entity.Users;
import com.dfusiontech.server.repository.jpa.IdpUserRepository;
import com.dfusiontech.server.repository.jpa.UserRepository;
import com.dfusiontech.server.services.CacheWrapperService;
import com.microsoft.graph.models.extensions.User;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * oAuth2 Multi Factor Authentication Service
 *
 * @author Eugene A. Kalosha <ekalosha@dfusiontech.com>
 */
@Service
@Slf4j
public class MultiFactorAuthenticationService {

	// private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

	@Autowired
	private CacheWrapperService cacheWrapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Environment environment;

	// @Autowired
	// private AuditLogService auditLogService;

	@Autowired
	private IdpUserRepository idpUserRepository;

	@Autowired
	private RestTemplate restTemplate;

	public boolean isEnabled(String username) {
		return isEnabled(username, null);
	}

	/**
	 * Check is MFA Enabled for User by its Username
	 *
	 * @param username
	 * @param client
	 * @return
	 */
	@Transactional
	public boolean isEnabled(String username, ClientDetails client) {
		Users userDetails = userRepository.findFirstByEmailIgnoreCase(username).get();

		// Verify that Authorization Client Allowed to use MFA
		if (client != null) {
			if (client.getAuthorities().contains(new SimpleGrantedAuthority(OAuthClientAuthority.MFA_IOS))) {
				// Skip MFA for iOS
				return false;
			} else if (client.getAuthorities().contains(new SimpleGrantedAuthority(OAuthClientAuthority.MFA_ANDROID))) {
				// Skip MFA for Android
				return false;
			} else if (client.getAuthorities().contains(new SimpleGrantedAuthority(OAuthClientAuthority.MFA))) {
				// Go To Multi Factor Auth detection
			} else {
				// Skip MFA by Default
				return false;
			}
		}

		if (Boolean.TRUE.equals(userDetails.getUseMultiFactorAuth())) {
			return true;
		}

		Organizations organization = userDetails.getOrganization();
		if (organization != null && Boolean.TRUE.equals(organization.getUseMultiFactorAuth())) {
			return true;
		}

		return false;
	}

	/**
	 * Audit user password login event
	 *
	 * @param authentication
	 * @return
	 */
	@Transactional
	public boolean auditUserAuthEvent(Authentication authentication, AuditItemType event) {
		Object principal = authentication.getPrincipal();

		UserDetailsImpl user = (UserDetailsImpl) principal;

		return auditUserAuthEvent(user.getUserId(), event);
	}


	/**
	 * Audit user password login event
	 *
	 * @param email
	 * @return
	 */
	@Transactional
	public UserDetails getIDPUserByIdentityAndType(String email, IdpType idpType) {
		// Check the email in common users DB table
		Optional<Users> userOptional = userRepository.findFirstByEmailIgnoreCase(email);
		Users user = null;
		// If there is no any user with given email..
		if (!userOptional.isPresent()) {
			//  ..then check is given email is allied to any user through idp_users table
			Optional<IdpUsers> idpUserOptional = this.idpUserRepository.findFirstByUserIdentityIgnoreCaseAndIdpId(email, idpType);

			// in case that there is no users allied to given email - throw exception
			if (!idpUserOptional.isPresent() || idpUserOptional.get().getUserId() == null) {
				throw new InvalidGrantException("Could not authenticate user: " + email + " . Make sure you are registered in the system.");
			} else {
				user = idpUserOptional.get().getUser();
			}
		} else {
			user = userOptional.get();
		}
		// Other way authorize
		UserDetails userDetails = UserDetailsImpl.of(user);

		return userDetails;
	}

	/**
	 * Audit user password login event
	 *
	 * @param userId
	 * @param event
	 * @return
	 */
	@Transactional
	public boolean auditUserAuthEvent(Long userId, AuditItemType event) {

		/*
		Users userDetails = userRepository.findById(userId).get();
		UserRefDTO userRef = new UserRefDTO(userDetails);

		// Save Audit Log CREATE event
		auditLogService.audit(
			AuditOperationType.EVENT,
			event,
			userDetails.getId(),
			null,
			userRef,
			userDetails,
			collectAuditLogItems(userDetails)
		);
		*/

		return true;
	}

	/**
	 * Audit user password login event
	 *
	 * @param authentication
	 * @return
	 */
	public void auditUserPasswordLogin(Authentication authentication) {
		boolean result = auditUserAuthEvent(authentication, AuditItemType.USER_PASSWORD_LOGIN);
	}

	/**
	 * Audit user Google login event
	 *
	 * @param authentication
	 * @return
	 */
	public void auditUserGoogleLogin(Authentication authentication) {
		boolean result = auditUserAuthEvent(authentication, AuditItemType.USER_GOOGLE_LOGIN);
	}

	/**
	 * Audit user Microsoft login event
	 *
	 * @param authentication
	 * @return
	 */
	public void auditUserMicrosoftLogin(Authentication authentication) {
		boolean result = auditUserAuthEvent(authentication, AuditItemType.USER_MICROSOFT_LOGIN);
	}

	/**
	 * Audit user SMS code login event
	 *
	 * @param authentication
	 * @return
	 */
	public void auditSMSCodeLogin(Authentication authentication) {
		boolean result = auditUserAuthEvent(authentication, AuditItemType.USER_SMS_CODE_LOGIN);
	}

	/**
	 * Generate Code and save it to cache Wrapper
	 *
	 * @param token
	 * @return
	 */
	public String generateCode(String token, String username) {

		Users userDetails = userRepository.findFirstByEmailIgnoreCase(username).get();

		MFACodeTokenDTO codeToken = MFACodeTokenDTO.of(token, userDetails.getId());

		log.info(MessageFormat.format("Registered Code {0}, for user {1}", codeToken.getCode(), userDetails.getEmail()));

		// Save Code token
		cacheWrapper.putCodeToken(codeToken);
		sendCodeBySMS(codeToken.getCode(), userDetails);

		return codeToken.getCode();
	}

	/**
	 * Send code by SMS
	 *
	 * @param code
	 * @param userDetails
	 * @return
	 */
	private boolean sendCodeBySMS(String code, Users userDetails) {

		String accountSid = environment.getProperty("twilio.sid");
		String accountToken = environment.getProperty("twilio.token");
		String fromNumber = environment.getProperty("twilio.from");
		String phoneNumber = userDetails.getMobilePhone();

		String fromNumberE164Format = verifyPhone(fromNumber);
		String phoneNumberE164Format = verifyPhone(phoneNumber);

		if (StringUtils.isEmpty(phoneNumberE164Format)) {
			throw new MFACodeException("Your phone number is empty. Please contact to administrator to verify your phone number.");
		}

		String smsMessage = MessageFormat.format("{0} is your verification code from vRisk", code);

		boolean result = true;
		try {
			Twilio.init(accountSid, accountToken);
			Message message = Message.creator(new PhoneNumber(phoneNumberE164Format), new PhoneNumber(fromNumberE164Format), smsMessage).create();
			String messageId = message.getSid();
		} catch (ApiException exception) {
			throw new MFACodeException("Failed to send verification SMS. " + exception.getMessage());
		}

		return result;
	}

	/**
	 * Verify Code by its token
	 *
	 * @param code
	 * @param token
	 * @return
	 */
	public boolean verifyCode(String code, String token) {

		MFACodeTokenDTO codeToken = cacheWrapper.getCodeToken(token);
		if (codeToken == null) {
			return false;
		}

		boolean result = code.equalsIgnoreCase(codeToken.getCode());

		if (!result) {
			// Proceed with attempts count
			codeToken.setAttempt(codeToken.getAttempt() + 1);
			if (codeToken.getAttempt() <= 3) {
				cacheWrapper.putCodeToken(codeToken);
				auditUserAuthEvent(codeToken.getUserId(), AuditItemType.USER_SMS_CODE_WRONG);
				throw new MFACodeException(MessageFormat.format("Invalid Authentication code. You have {0} attempts left.", 3 - codeToken.getAttempt()));
			} else {
				auditUserAuthEvent(codeToken.getUserId(), AuditItemType.USER_SMS_CODE_FAILED);
				cacheWrapper.removeCodeToken(codeToken.getToken());
				throw new MFACodeException("Too many Authentication attempts with the code. Please try again later.");
			}
		} else {
			auditUserAuthEvent(codeToken.getUserId(), AuditItemType.USER_SMS_CODE_LOGIN);
			cacheWrapper.removeCodeToken(codeToken.getToken());
		}

		return result;
	}

	/**
	 * Check is code expired
	 *
	 * @param token
	 * @param code
	 * @return
	 */
	public boolean checkExpiration(String token, String code) {
		return true;
	}

	/**
	 * Verify phone number
	 *
	 * @param phone
	 * @return
	 */
	public String verifyPhone(String phone) {
		String result = phone != null ? phone : "";

		result = result.replaceAll("[^\\d]+", "");

		// We must add "+" for all international numbers (except USA and Canada)
		result = "+" + result;

		return result;
	}

	/**
	 * Collect items for Audit Log record
	 *
	 * @param userDetails
	 * @return
	 */
	private AuditLogItemId[] collectAuditLogItems(Users userDetails) {
		List<AuditLogItemId> logItems = new ArrayList<>(Arrays.asList(AuditLogItemId.of(AuditItemType.ORGANIZATION, userDetails.getId())));
		logItems.add(AuditLogItemId.of(AuditItemType.USER, userDetails.getId()));

		return logItems.stream().toArray(AuditLogItemId[]::new);
	}

	/**
	 * Finds microsoft credentials
	 *
	 * @param accessToken
	 * @return
	 */
	public String getMicrosoftUserIdentity(String accessToken) {
		String microsoftUserIdentity = null;

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + accessToken);
		final HttpEntity request = new HttpEntity(headers);

		// Build request url
		String apiUrl = "https://graph.microsoft.com/v1.0/me";

		// Performing request
		ResponseEntity<User> responce = restTemplate.exchange(apiUrl, HttpMethod.GET, request, new ParameterizedTypeReference<User>() {});

		User microsoftUser = responce.getBody();
		log.info("Microsoft user: {}", microsoftUser);
		if (microsoftUser != null && StringUtils.isNotEmpty(microsoftUser.userPrincipalName)) {
			microsoftUserIdentity = microsoftUser.userPrincipalName;
		}
//		restTemplate

		return microsoftUserIdentity;
	}

}
