package com.dfusiontech.server.model.jpa.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Predefined Audit Item Types
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-07-23
 */
@Getter
@ToString(of = {"id", "name"})
public enum AuditItemType {

	UNKNOWN(0L, "Unknown")
	, OWNER_USER(45L, "Owner User for Item")
	, ORGANIZATION(91L, "Organization")
	, USER(1180L, "User")
	, USER_PASSWORD_CHANGED(1190L, "Password Changed")
	, USER_PASSWORD_LOGIN(1191L, "User Password Login")
	, USER_SMS_CODE_LOGIN(1192L, "User 2FA SMS Code Login")
	, USER_SMS_CODE_WRONG(1193L, "User 2FA Code Wrong")
	, USER_SMS_CODE_FAILED(1194L, "User 2FA Code Failed")
	, USER_GOOGLE_LOGIN(1195L, "User Google Login")
	, USER_MICROSOFT_LOGIN(1196L, "User Microsoft Login")
	, USER_LOGOUT(1198L, "User Logout")
	, DOCUMENT_FILE(131L, "Document FIle")
	, ORGANIZATION_AGREEMENT(281L, "Organization Agreement")
	, USER_AGREEMENT(282L, "User Agreement")
	, SECURITY_REQUIREMENT(283L, "Security Requirement")
	;

	private final Long id;

	private final String name;

	public static Map<Long, AuditItemType> ALL_ITEMS_MAP = Arrays.stream(AuditItemType.values()).collect(Collectors.toMap(AuditItemType::getId, itemType -> itemType));

	private AuditItemType(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns Type Entity By ID
	 *
	 * @param id
	 * @return
	 */
	public static AuditItemType of(Long id) {

		if (id != null && ALL_ITEMS_MAP.containsKey(id)) {
			return ALL_ITEMS_MAP.get(id);
		}

		return UNKNOWN;
	}

}
