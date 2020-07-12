package com.dfusiontech.server.services;

import com.dfusiontech.server.model.dao.LanguageConstantValueModelDAO;
import com.dfusiontech.server.model.dao.PagedResult;
import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.LanguageConstantFilter;
import com.dfusiontech.server.model.dto.ImportResultDTO;
import com.dfusiontech.server.model.dto.ItemViewDTO;
import com.dfusiontech.server.model.dto.common.LanguageConstantValueViewDTO;
import com.dfusiontech.server.model.jpa.domain.LanguageConstantScopeType;
import com.dfusiontech.server.model.jpa.entity.LanguageConstantValues;
import com.dfusiontech.server.model.jpa.entity.LanguageConstants;
import com.dfusiontech.server.model.jpa.entity.SupportedLanguages;
import com.dfusiontech.server.repository.jpa.LanguageConstantRepository;
import com.dfusiontech.server.repository.jpa.LanguageConstantValueRepository;
import com.dfusiontech.server.repository.jpa.SupportedLanguagesRepository;
import com.dfusiontech.server.rest.exception.BadRequestException;
import com.dfusiontech.server.rest.exception.InternalServerErrorException;
import com.dfusiontech.server.rest.exception.ItemNotFoundException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Language Constant Service.
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-04-09
 */
@Service
public class LanguageConstantService {

	public static final String CONSTANT_HEADER = "Constant";
	public static final String SCOPE_HEADER = "Scope";
	public static final String VALUE_HEADER = "Value";
	public static final String DEFAULT_VALUE_HEADER = "Default Value";

	public static final String DEFAULT_LANGUAGE_NAME = "English";
	public static final String DEFAULT_LANGUAGE_CODE = "eng";
	public static final String DEFAULT_LANGUAGE_LOCALE = "en_US";

	public static Map<String, Map<String, String>> serverLanguageConstants = new HashMap<>();

	public static Map<String, String> serverDefaultLanguageConstants = new HashMap<>();

	@Autowired
	private LanguageConstantRepository languageConstantRepository;

	@Autowired
	private LanguageConstantValueRepository languageConstantValueRepository;

	@Autowired
	private LanguageConstantValueModelDAO languageConstantValueModelDAO;

	@Autowired
	private SupportedLanguagesRepository supportedLanguagesRepository;

	@Autowired
	private SupportedLanguagesService supportedLanguagesService;

	@Transactional
	public void loadLanguageConstants() {

		List<SupportedLanguages> languages = supportedLanguagesRepository.findAll();
		for (SupportedLanguages language: languages) {
			String languageCode = language.getCode();
			LanguageConstantFilter filter = new LanguageConstantFilter();
			filter.setLanguageCode(languageCode);
			filter.setScope(LanguageConstantScopeType.SERVER);
			Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

			List<LanguageConstantValueViewDTO> languageConstantValues = languageConstantValueModelDAO.getItemsPageable(filter, pageable, null).getItems();

//			List<LanguageConstantValues> languageConstantValues = languageConstantValueRepository.getListByLanguageLocale(language.getLocale());
			Map<String, String> serverLanguageConstants = languageConstantValues.stream()
				.collect(Collectors.toMap(languageConstantValue -> languageConstantValue.getLanguageConstant().getName(), LanguageConstantValueViewDTO::getValue));

			LanguageConstantService.serverLanguageConstants.put(language.getLocale(), serverLanguageConstants);
		}

		if (serverDefaultLanguageConstants.size() < 1 && serverLanguageConstants.size() > 0) {
			serverDefaultLanguageConstants = serverLanguageConstants.get("en_US");
			if (serverDefaultLanguageConstants == null) serverDefaultLanguageConstants = serverLanguageConstants.values().stream().findFirst().orElse(new HashMap<>());
		}
	}

	public Map<String, String> getServerConstantsByLocale(String locale) {
		return serverLanguageConstants.get(locale);
	}

	/**
	 * Get Language Constant Value details
	 *
	 * @return Language Constant Value Details
	 */
	public LanguageConstantValues getLanguageConstantValue(Long itemId) {
		LanguageConstantValues itemDetails;

		try {
			itemDetails = languageConstantValueRepository.findById(itemId).get();
		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("Language Constant Value not found in the database [{0}]", itemId));
		}

		return itemDetails;
	}

	public FilteredResponse<LanguageConstantFilter, LanguageConstantValueViewDTO> getLanguageConstantValuesListFiltered(String languageCode, FilteredRequest<LanguageConstantFilter> filteredRequest) {

		// Set Language Code
		filteredRequest.getFilter().setLanguageCode(languageCode);
//		filteredRequest.getFilter().setScope(LanguageConstantScopeType.UI);

		PagedResult<LanguageConstantValueViewDTO> result = languageConstantValueModelDAO.getItemsPageable(filteredRequest.getFilter(), filteredRequest.toPageRequest(), filteredRequest.getSort());
		FilteredResponse<LanguageConstantFilter, LanguageConstantValueViewDTO> filteredResponse = new FilteredResponse<>(filteredRequest, result);

		return filteredResponse;
	}

	/**
	 * Get List of Language Constants with Values for Language
	 *
	 * @return Language Constants and Values Map
	 */
	public Map<String, String> getListForLanguage(String languageCode) {

		SupportedLanguages language = supportedLanguagesService.getSupportedLanguage(languageCode);

		List<LanguageConstantValues> items = languageConstantValueRepository.getListByLanguageForAllConstants(language);

		Map<String, String> resultMap = items.stream()
			.collect(Collectors.toMap(lcValue -> lcValue.getLanguageConstant().getName(), lcValue -> lcValue.getValue(), (o1, o2) -> o1, LinkedHashMap::new));

		return resultMap;
	}

	/**
	 * Insert data from JSON file
	 */
	@Deprecated
	@Transactional
	public ImportResultDTO importLanguageConstantsFromJSONFile(String languageCode, MultipartFile file) {

		Optional<SupportedLanguages> language = supportedLanguagesRepository.findFirstByCode(languageCode);

		InputStream fileContentStream = null;
		ImportResultDTO result = new ImportResultDTO();

		try {
			fileContentStream = file.getInputStream();

			try {
				fileContentStream.reset();
			} catch (IOException exception) { }

			// Parse JSON file
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> dataMap = objectMapper.readValue(fileContentStream, HashMap.class);

			if (language.isPresent()) {
				result = importLanguageConstantsFromStringMap(language.get(), dataMap);
			} else {
				throw new BadRequestException("Language does not exists!. Import Failed.");
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return result;
	}

	/**
	 * Insert data from CSV file
	 */
	@Transactional
	public ImportResultDTO importLanguageConstantsFromCSVFile(String languageCode, MultipartFile file) {

		Optional<SupportedLanguages> language = supportedLanguagesRepository.findFirstByCode(languageCode);

		InputStream fileContentStream = null;
		ImportResultDTO result = new ImportResultDTO();

		try {
			fileContentStream = file.getInputStream();

			try {
				fileContentStream.reset();
			} catch (IOException exception) { }

			// Parse CSV file
			Reader reader = new InputStreamReader(fileContentStream, Charset.defaultCharset());
			CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
			CSVParser csvParser =csvFormat.parse(reader);

			List<CSVRecord> csvRecordList = csvParser.getRecords();
			if (language.isPresent()) {
				result = importLanguageConstantsFromCSVItems(language.get(), csvRecordList);
			} else {
				throw new BadRequestException("Language does not exists!. Import Failed.");
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return result;
	}

	/**
	 * Insert data from JSON file
	 */
	@Deprecated
	@Transactional
	public ImportResultDTO importLanguageConstantsFromStringMap(SupportedLanguages language, Map<String, String> dataMap) {

		ImportResultDTO result = new ImportResultDTO();

		for (Map.Entry<String, String> dataRecord : dataMap.entrySet()) {
			Optional<LanguageConstants> languageConstantOptional = languageConstantRepository.findFirstByName(dataRecord.getKey());
			LanguageConstants languageConstant;

			if (languageConstantOptional.isPresent()) {
				languageConstant = languageConstantOptional.get();
			} else {
				// create language constant
				languageConstant = new LanguageConstants();
				languageConstant.setName(dataRecord.getKey());
				// save language constant
				languageConstant = languageConstantRepository.save(languageConstant);
				result.getMessages().add(MessageFormat.format("New Language Constant [{0}] [id: {1}] was created", languageConstant.getName(), languageConstant.getId()));
			}

			Optional<LanguageConstantValues> languageConstantValueOptional = languageConstantValueRepository.findFirstByLanguageIdAndLanguageConstantId(language.getId(), languageConstant.getId());
			LanguageConstantValues languageConstantValue;
			if (languageConstantValueOptional.isPresent()) {
				languageConstantValue = languageConstantValueOptional.get();
				languageConstantValue.setValue(dataRecord.getValue());

			} else {
				// create language constant value
				languageConstantValue = new LanguageConstantValues();
				languageConstantValue.setLanguage(language);
				languageConstantValue.setLanguageConstant(languageConstant);
			}
			// update existed or new language constant value
			languageConstantValue.setValue(dataRecord.getValue());
			// save new or updated language constant value
			languageConstantValue = languageConstantValueRepository.save(languageConstantValue);
			if (languageConstantOptional.isPresent()) {
				result.getUpdated().add(new ItemViewDTO(languageConstantValue.getId(), MessageFormat.format("[{0}] - [{1}]", languageConstant.getName(), languageConstantValue.getValue())));
			} else {
				result.getCreated().add(new ItemViewDTO(languageConstantValue.getId(), MessageFormat.format("[{0}] - [{1}]", languageConstant.getName(), languageConstantValue.getValue())));
			}
		}

		return result;
	}

	/**
	 * Insert data from CSV file
	 */
	public ImportResultDTO importLanguageConstantsFromCSVItems(SupportedLanguages language, List<CSVRecord> csvRecordList) {

		ImportResultDTO result = new ImportResultDTO();

		for (CSVRecord csvRecord : csvRecordList) {
			Optional<LanguageConstants> languageConstantOptional = languageConstantRepository.findFirstByName(csvRecord.get(CONSTANT_HEADER));
			LanguageConstants languageConstant;

			if (languageConstantOptional.isPresent()) {
				languageConstant = languageConstantOptional.get();
				languageConstant.setScope(LanguageConstantScopeType.of(csvRecord.get(SCOPE_HEADER)));
			} else {
				// create language constant
				languageConstant = new LanguageConstants();
				languageConstant.setName(csvRecord.get(CONSTANT_HEADER));
				languageConstant.setScope(LanguageConstantScopeType.of(csvRecord.get(SCOPE_HEADER)));
				// save language constant
				languageConstant = languageConstantRepository.save(languageConstant);
				result.getMessages().add(MessageFormat.format("New Language Constant [{0}] [id: {1}] was created", languageConstant.getName(), languageConstant.getId()));
			}

			Optional<LanguageConstantValues> languageConstantValueOptional = languageConstantValueRepository.findFirstByLanguageIdAndLanguageConstantId(language.getId(), languageConstant.getId());
			LanguageConstantValues languageConstantValue;
			if (languageConstantValueOptional.isPresent()) {
				languageConstantValue = languageConstantValueOptional.get();
				languageConstantValue.setValue(csvRecord.get(VALUE_HEADER));

			} else {
				// create language constant value
				languageConstantValue = new LanguageConstantValues();
				languageConstantValue.setLanguage(language);
				languageConstantValue.setLanguageConstant(languageConstant);
			}
			// update existed or new language constant value
			languageConstantValue.setValue(csvRecord.get(VALUE_HEADER));
			// save new or updated language constant value
			languageConstantValue = languageConstantValueRepository.save(languageConstantValue);
			if (languageConstantOptional.isPresent()) {
				result.getUpdated().add(new ItemViewDTO(languageConstantValue.getId(), MessageFormat.format("[{0}] - [{1}]", languageConstant.getName(), languageConstantValue.getValue())));
			} else {
				result.getCreated().add(new ItemViewDTO(languageConstantValue.getId(), MessageFormat.format("[{0}] - [{1}]", languageConstant.getName(), languageConstantValue.getValue())));
			}
		}

		return result;
	}

	/**
	 * Get JSON document with Language Constants Data for Language
	 *
	 * @param languageCode
	 * @return
	 */
	@Deprecated
	public ByteArrayInputStream getDownloadDataAsJSON(String languageCode) {
		ByteArrayInputStream byteArrayInputStream = null;

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

			SupportedLanguages language = supportedLanguagesRepository.findFirstByCode(languageCode).get();

			List<LanguageConstantValues> items = languageConstantValueRepository.getListByLanguageForAllConstants(language);
			Map<String, String> resultMap = items.stream()
				.collect(Collectors.toMap(lcValue -> lcValue.getLanguageConstant().getName(), lcValue -> lcValue.getValue(), (o1, o2) -> o1, LinkedHashMap::new));
			writer.writeValue(outputStream, resultMap);

			byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException exception) {
			exception.printStackTrace();
			throw new InternalServerErrorException("Failed to generate CSV Template file for Language Constants");
		}

		return byteArrayInputStream;
	}

	/**
	 * Get CSV document with Language Constants Data for Language
	 *
	 * @param languageCode
	 * @return
	 */
	public ByteArrayInputStream getDownloadDataAsCSV(String languageCode) {
		ByteArrayInputStream byteArrayInputStream = null;

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			CSVPrinter csvPrinter = createLanguageConstantCsvPrinter(outputStream);

			SupportedLanguages language = supportedLanguagesRepository.findFirstByCode(languageCode).get();
			List<LanguageConstantValues> items = languageConstantValueRepository.getListByLanguageForAllConstants(language);

			SupportedLanguages defaultLanguage = supportedLanguagesRepository.findFirstByCode(DEFAULT_LANGUAGE_CODE).get();
			List<LanguageConstantValues> defaultItems = languageConstantValueRepository.getListByLanguageForAllConstants(defaultLanguage);
			Map<Long, String> defaultItemsMap = defaultItems.stream().collect(Collectors.toMap(lcValue -> lcValue.getLanguageConstant().getId(), lcValue -> lcValue.getValue()));

			for(LanguageConstantValues value: items) {
				csvPrinter.printRecord(
					value.getLanguageConstant().getName(),
					value.getLanguageConstant().getScope().name(),
					value.getValue(),
					defaultItemsMap.get(value.getLanguageConstant().getId())
				);
			}
			csvPrinter.flush();

			byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException exception) {
			exception.printStackTrace();
			throw new InternalServerErrorException("Failed to generate CSV Template file for Language Constants");
		}

		return byteArrayInputStream;
	}

	/**
	 * Create CSV Printer to build Language Constant
	 *
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	private CSVPrinter createLanguageConstantCsvPrinter(ByteArrayOutputStream outputStream) throws IOException {
		Writer writer = new OutputStreamWriter(outputStream);
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(CONSTANT_HEADER, SCOPE_HEADER, VALUE_HEADER, DEFAULT_VALUE_HEADER);
		return new CSVPrinter(writer, csvFormat);
	}

	/**
	 * Update Language Constant Values List
	 *
	 * @return Updated Language Constant Values
	 */
	public List<LanguageConstantValueViewDTO> updateLanguageConstantValuesList(List<LanguageConstantValueViewDTO> itemsList, String languageCode) {

		SupportedLanguages language = supportedLanguagesService.getSupportedLanguage(languageCode);

		List<LanguageConstantValueViewDTO> result = new ArrayList<>();

		for (LanguageConstantValueViewDTO itemDTO : itemsList) {

			LanguageConstantValues entity;

			if (itemDTO.getId() != null) {
				entity = getLanguageConstantValue(itemDTO.getId());
			} else {
				LanguageConstants languageConstant = languageConstantRepository.findById(itemDTO.getLanguageConstant().getId()).orElse(null);

				entity = new LanguageConstantValues();
				entity.setLanguageConstant(languageConstant);
				entity.setLanguage(language);
			}

			LanguageConstantValueViewDTO existingItemDTO = new LanguageConstantValueViewDTO(entity);

			// Save only if item changed
			if (!itemDTO.getValue().equals(existingItemDTO.getValue())) {
				entity.setValue(itemDTO.getValue());
				languageConstantValueRepository.save(entity);
			}

			result.add(itemDTO);
		}

		return result;
	}

	/**
	 * Delete All Language Constant Values for Language
	 *
	 * @param languageCode 	code of language which language constant values should be deleted
	 * @return languageId
	 */
	@Transactional
	public Long clearVocabulary(String languageCode) {

		SupportedLanguages language = supportedLanguagesService.getSupportedLanguage(languageCode);

		List<LanguageConstantValues> itemsToDelete = languageConstantValueRepository.getListByLanguage(language);

		if(itemsToDelete.size() > 0) {
			languageConstantValueRepository.deleteAll(itemsToDelete);
			languageConstantValueRepository.flush();
		}

		return language.getId();
	}

	/**
	 * Delete All Language Constant Values for Language
	 *
	 * @return
	 */
	@Transactional
	public void clearAllVocabularies() {

		languageConstantValueRepository.deleteAll();
		languageConstantValueRepository.flush();
		languageConstantRepository.deleteAll();
		languageConstantRepository.flush();

	}
}
