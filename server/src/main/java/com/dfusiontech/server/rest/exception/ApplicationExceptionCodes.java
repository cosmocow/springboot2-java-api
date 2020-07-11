package com.dfusiontech.server.rest.exception;

/**
 * Application Exception Codes
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-22
 */
public class ApplicationExceptionCodes {
	// Default ERROR codes
	public static final int PASSWORD_DOESNT_MATCH = 1141;

	public static final int CREATE_IS_NOT_ALLOWED_FOR_ITEM_WITH_EXISTING_ID = 1201;
	public static final int UPDATE_IS_NOT_ALLOWED_FOR_ITEM_WITHOUT_ID = 1202;

	// User ERRORs range: 1300-1399
	public static final int USER_WITH_EMAIL_ALREADY_EXISTS = 1301;
	public static final int USER_NOT_EXISTS = 1302;
	public static final int USER_NOT_BELONGS_TO_CURRENT_ORGANIZATION = 1303;
	public static final int USER_WITH_EMAIL_NOT_EXISTS = 1304;
	public static final int USER_MANAGEMENT_FORBIDDEN = 1313;
	public static final int USER_REGISTRATION_EMAIL_FAILED = 1314;
	public static final int RESET_PASSWORD_LINK_EMAIL_FAILED = 1323;
	public static final int RESET_PASSWORD_LINK_NOT_EXISTS = 1324;
	public static final int RESET_PASSWORD_LINK_EXPIRED = 1325;
	public static final int RESET_PASSWORD_LINK_ALREADY_APPLIED = 1326;
	public static final int RESET_PASSWORD_TOO_WEAK = 1327;

	// Organization ERRORs range: 1400-1499
	public static final int ORGANIZATION_WITH_NAME_ALREADY_EXISTS = 1401;
	public static final int ORGANIZATION_NOT_EXISTS = 1402;
	public static final int ACCESS_TO_ORGANIZATION_FORBIDDEN = 1403;

	// Supported Languages ERRORs range: 4870 - 4879
	public static final int SUPPORTED_LANGUAGE_NOT_EXIST = 4870;

}
