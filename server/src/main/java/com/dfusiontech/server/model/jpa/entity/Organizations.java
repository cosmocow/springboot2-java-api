package com.dfusiontech.server.model.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Organization Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-08
 */
@Entity
@Table(name = "organizations")
@NoArgsConstructor
@Setter
@Getter
@ToString(of = {"id", "name", "organizationType"})
@EqualsAndHashCode(of = {"id", "name"})
public class Organizations {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "tax_id")
	private String taxId;

	@Column(name = "vat_id")
	private String vatId;

	@Column(name = "street_address_1")
	private String streetAddress1;

	@Column(name = "street_address_2")
	private String streetAddress2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "state_id")
	private State state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id")
	private Language language;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	@Column(name = "zip", length = 16)
	private String zip;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "site", length = 255)
	private String site;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Organizations parent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "root_parent_id")
	private Organizations rootParent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Users owner;

	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(
		name = "organization_to_language",
		joinColumns = {@JoinColumn(name = "organization_id")},
		inverseJoinColumns = {@JoinColumn(name = "language_id")}
	)
	private Set<SupportedLanguages> supportedLanguages = new HashSet<>();

	@Column(name = "is_technology_vendor")
	private Boolean isTechnologyVendor;

	@Column(name = "is_system_vendor")
	private Boolean isSystemVendor;

	@Column(name = "is_service_vendor")
	private Boolean isServiceVendor;

	@Column(name = "is_public_company")
	private Boolean isPublicCompany;

	@Column(name = "is_multi_language")
	private Boolean isMultiLanguage;

	@Column(name = "use_multi_factor_auth")
	private Boolean useMultiFactorAuth;

	@Column(name = "insurance_limit")
	private Double insuranceLimit;

}
