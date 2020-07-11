package com.dfusiontech.server.model.dto.organization;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.ItemViewDTO;
import com.dfusiontech.server.model.dto.user.UserRefDTO;
import com.dfusiontech.server.model.jpa.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Organization View Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-08
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class OrganizationEditDTO extends DTOBase<Organizations> {

	@ApiModelProperty(position = 0, example = "105")
	private Long id;

	@ApiModelProperty(position = 2, example = "Jefferies")
	private String name;

	@ApiModelProperty(position = 4, example = "Investment bank")
	private String description;

	@ApiModelProperty(position = 8, example = "14-0968532")
	private String taxId;

	@ApiModelProperty(position = 10)
	private String vatId;

	@ApiModelProperty(position = 12, example = "525 Madison Ave")
	private String streetAddress1;

	@ApiModelProperty(position = 14)
	private String streetAddress2;

	@ApiModelProperty(position = 16, dataType = "com.cyberintech.vrisk.server.model.dto.ItemViewDTO", example = "{\"id\": 229}")
	private ItemViewDTO<Country> country;

	@ApiModelProperty(position = 18, example = "{\"id\": 4006}")
	private ItemViewDTO<State> state;

	@ApiModelProperty(position = 20, example = "{\"id\": 47561}")
	private ItemViewDTO<City> city;

	@ApiModelProperty(position = 22, example = "{\"id\": 2}")
	private CurrencyViewDTO currency;

	@ApiModelProperty(position = 24, example = "{\"id\": 18}")
	private ItemViewDTO<Language> language;

	@ApiModelProperty(position = 26, example = "{\"id\": 1}")
	private ItemViewDTO<Status> status;

	@ApiModelProperty(position = 27, example = "{\"id\": 105}")
	private OrganizationRefDTO parent;

	@ApiModelProperty(position = 28)
	private UserRefDTO owner;

	@ApiModelProperty(position = 28, example = "10024")
	private String zip;

	@ApiModelProperty(position = 30, example = "123456789")
	private String phone;

	@ApiModelProperty(position = 32, example = "www.jefferies.co")
	private String site;

	@ApiModelProperty(position = 34)
	private String notes;

	@ApiModelProperty(position = 36, example = "4000000000")
	private Double averageRevenue;

	@ApiModelProperty(position = 38, example = "0.75", dataType = "float")
	private Double qualThreshold;

	@ApiModelProperty(position = 40)
	private Boolean isCloudVendor;

	@ApiModelProperty(position = 42)
	private Double marketCapitalizationNumber;

	@ApiModelProperty(position = 44)
	private Boolean isPublicCompany;

	@ApiModelProperty(position = 46)
	private List<SupportedLanguageEditDTO> supportedLanguages;

	@ApiModelProperty(position = 48)
	private Boolean isMultiLanguage;

	@ApiModelProperty(position = 50)
	private Boolean useMultiFactorAuth;

	@ApiModelProperty(position = 52)
	private Boolean isServiceVendor;

	@ApiModelProperty(position = 54)
	private Boolean isTechnologyVendor;

	@ApiModelProperty(position = 56)
	private Boolean isSystemVendor;

	@ApiModelProperty(position = 58)
	private OrganizationRefDTO rootParent;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public OrganizationEditDTO(Organizations entity) {
		super(entity);
	}

	/**
	 * Converts from entity to DTO
	 *
	 * @param entity
	 */
	@Override
	public void fromEntity(Organizations entity) {
//		super.fromEntity(entity);

		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
		taxId = entity.getTaxId();
		vatId = entity.getVatId();
		streetAddress1 = entity.getStreetAddress1();
		streetAddress2 = entity.getStreetAddress2();
		zip = entity.getZip();
		phone = entity.getPhone();
		site = entity.getSite();
		isServiceVendor = entity.getIsServiceVendor();
		isTechnologyVendor = entity.getIsSystemVendor();
		isSystemVendor = entity.getIsTechnologyVendor();
		isPublicCompany = entity.getIsPublicCompany();
		isMultiLanguage = entity.getIsMultiLanguage();
		useMultiFactorAuth = entity.getUseMultiFactorAuth();

		if (entity.getCountry() != null) setCountry(new ItemViewDTO<Country>(entity.getCountry()));
		if (entity.getCity() != null) setCity(new ItemViewDTO<City>(entity.getCity()));
		if (entity.getState() != null) setState(new ItemViewDTO<State>(entity.getState()));
		if (entity.getLanguage() != null) setLanguage(new ItemViewDTO<Language>(entity.getLanguage()));
		if (entity.getCurrency() != null) setCurrency(new CurrencyViewDTO(entity.getCurrency()));
		if (entity.getStatus() != null) setStatus(new ItemViewDTO<Status>(entity.getStatus()));
		if (entity.getParent() != null) setParent(new OrganizationRefDTO(entity.getParent()));
		if (entity.getRootParent() != null) setRootParent(new OrganizationRefDTO(entity.getRootParent()));
		if (entity.getOwner() != null) setOwner(new UserRefDTO(entity.getOwner()));

		supportedLanguages = Optional.ofNullable(entity.getSupportedLanguages()).orElse(new HashSet<>()).stream().map(SupportedLanguageEditDTO::new).collect(Collectors.toList());
	}

}
