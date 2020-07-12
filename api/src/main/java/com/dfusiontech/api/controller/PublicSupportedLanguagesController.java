package com.dfusiontech.api.controller;

import com.dfusiontech.server.model.dto.organization.SupportedLanguageViewDTO;
import com.dfusiontech.server.services.LanguageConstantService;
import com.dfusiontech.server.services.SupportedLanguagesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(
	value = PublicSupportedLanguagesController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Public Supported Languages Management Controller"
)
@Api(tags = "Public Supported Languages Management Controller")
public class PublicSupportedLanguagesController {

	static final String CONTROLLER_URI = "api/info/supported-languages";

	@Autowired
	private SupportedLanguagesService supportedLanguagesService;

	@Autowired
	private LanguageConstantService languageConstantService;

	/**
	 * Get Public Supported Languages List
	 *
	 * @return Supported Languages List
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/", name = "Public Supported Languages List")
	public List<SupportedLanguageViewDTO> getPublicSupportedLanguagesList() {

		List<SupportedLanguageViewDTO> result = supportedLanguagesService.getPublicLanguagesList();

		return result;
	}

	/**
	 * Get Language Constant Values for current Language
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/vocabulary/{languageCode}", name = "")
	public Map<String, String> getValuesByLanguage(
		@ApiParam(defaultValue = "eng") @PathVariable("languageCode") String languageCode
	) {

		Map<String, String> result = languageConstantService.getListForLanguage(languageCode);

		return result;
	}
}
