package com.dfusiontech.server.i18n;

import com.dfusiontech.server.context.ApplicationContextThreadLocal;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Implementation for ClientMessage with Data Base as Source
 *
 * @author Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 */
@Component
public class ClientMessageImpl implements ClientMessage {

	@Override
	public String getMessage(String messageKey) {

		return getMessage(messageKey, null);
	}

	@Override
	public String getMessage(String messageKey, Object[] replacements) {

		return getMessage(messageKey, replacements, null);
	}

	@Override
	// replacements and locale are ignored in current implementation
	public String getMessage(String messageKey, Object[] replacements, Locale locale) {
		String localeString = ApplicationContextThreadLocal.getContext().getLocaleString();
		String result = null;
		/*
		if (LanguageConstantService.serverLanguageConstants.containsKey(localeString) && LanguageConstantService.serverLanguageConstants.get(localeString).containsKey(messageKey)) {
			result = LanguageConstantService.serverLanguageConstants.get(localeString).get(messageKey);
		} else {
			result = LanguageConstantService.serverDefaultLanguageConstants.get(messageKey);
		}
		*/
		if (result == null) result = messageKey;

		return result;
	}

}
