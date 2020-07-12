package com.dfusiontech.api.controller;

import com.dfusiontech.server.model.data.AuditLogFilter;
import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.dto.audit.AuditLogViewDTO;
import com.dfusiontech.server.model.dto.audit.ItemTypeDTO;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.services.AuditLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Audit Log management controller. Basic risk model CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-07-23
 */
@RestController
@RequestMapping(
	value = AuditLogsController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Audit Log Management Controller"
)
@Api(tags = "Audit Log Management")
public class AuditLogsController {

	static final String CONTROLLER_URI = "/api/audit-logs";

	@Autowired
	private AuditLogService auditLogService;

	/**
	 * Get Audit Log List for current Risk Model
	 *
	 * @return Audit Log List
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/filter", name = "Audit Log List for current Filters and Risk Model")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).AUDIT_LOG_READ)")
	public FilteredResponse<AuditLogFilter, AuditLogViewDTO> getListFiltered(
		@ApiParam(value = "Data Filtering Object", required = true) @RequestBody FilteredRequest<AuditLogFilter> filteredRequest
	) {

		FilteredResponse<AuditLogFilter, AuditLogViewDTO> result = auditLogService.getListFiltered(filteredRequest);

		return result;
	}

	/**
	 * Get Audit Log item types
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/item-types", name = "Get Audit Log item types")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public List<ItemTypeDTO> getItemTypes() {
		return Arrays.stream(AuditItemType.values()).map(ItemTypeDTO::new).collect(Collectors.toList());
	}

	/**
	 * Get Audit Log details
	 *
	 * @return Audit Log Details
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{itemId}", name = "Get Audit Log details")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).AUDIT_LOG_DETAILS)")
	public AuditLogViewDTO getDetails(
		@PathVariable("itemId") @NotNull @Size(min = 1) Long itemId
	) {
		AuditLogViewDTO itemDTO = auditLogService.getDetails(itemId);

		return itemDTO;
	}

}
