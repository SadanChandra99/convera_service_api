package com.convera.product.web;

import com.convera.common.template.CommonResponse;
import com.convera.common.template.response.error.constants.ResponseErrorCode400;
import com.convera.common.template.response.error.constants.ResponseErrorCode404;
import com.convera.common.template.response.error.constants.ResponseErrorCode500;
import com.convera.common.template.response.util.CommonResponseUtil;
import com.convera.product.constants.DashboardApiConstants;
import com.convera.product.data.model.Dashboard;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.service.DashboardsService;
import datadog.trace.api.Trace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("convera/dashboard")
@Validated
@Tag(name = "the dashboard service api")
public class DashboardsServiceController {

    @Autowired
    DashboardsService dashboardsService;

    /**
     * Get the Dashboards by account id.
     *
     * @param userId the user id
     * @return Dashboards details if the dashboards are found by account id, otherwise returns a stub dashboards empty list
     */
    @Operation(
            operationId = "getDashboards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "payment response", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = DashboardsResponse.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardsResponse.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "unexpected error", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardsResponse.class))
                    })
            }
    )
    @GetMapping("/{userId}")
    @Trace
    public ResponseEntity<CommonResponse<List<Dashboard>>> getDashboards(
            @Parameter(description = "User Id ", example = "1")
            @NotEmpty @PathVariable(required = true) Long userId,
            @RequestHeader(value = "correlationId", required = false) String correlationId) {

    	List<Dashboard> dashboards = new ArrayList<>();
        try {
        	
        	

        		 com.convera.product.data.model.DashboardsResponse dashboardsResponse = dashboardsService.getDashboards(userId, correlationId);

                 return ResponseEntity.ok(CommonResponseUtil.createResponse200(dashboardsResponse.getData(),"convera/dashboard/",
                         correlationId));
        	
        	

        }catch (HttpClientErrorException.NotFound | DataNotFoundException ex ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponseUtil.createResponse404(dashboards, DashboardApiConstants.ORDER_BASE_VALUE + userId, correlationId, Collections
                            .singletonList(ResponseErrorCode404.ERR_40400.build("dashboard-service", "Record for given id not found."))));

        }catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(dashboards, DashboardApiConstants.ORDER_BASE_VALUE + userId, correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("dashboard-service",
                                    "Error in getting records. Message: " + ex.getMessage()))));
        }
    }


    @Operation(
            operationId = "saveDashboards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "dashboard response", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = DashboardsResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "bad request", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = DashboardsResponse.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "unexpected error", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = DashboardsResponse.class))
                    })
            }
    )
    @PostMapping("/")
    @Trace
    public ResponseEntity<CommonResponse<List<Dashboard>>> saveDashboards(
            @Valid @NotNull @RequestBody(required = true) List<Dashboard> request,
            @RequestHeader(value = "correlationId", required = false) String correlationId,
            HttpServletRequest httpServletRequest) {

        try {
            com.convera.product.data.model.DashboardsResponse dashboardsResponse = dashboardsService.saveDashboards(request, correlationId);

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(dashboardsResponse.getData(), httpServletRequest.getRequestURI(),
                    correlationId));
        } catch (HttpClientErrorException ex ){
            log.error("dashboard-service_getMessage:", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponseUtil.createResponse400(List.of(), httpServletRequest.getRequestURI(), correlationId,
                            Collections
                                    .singletonList(ResponseErrorCode400.ERR_40000.build("dashboard-service", ex.getMessage()))));

        } catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(List.of() , DashboardApiConstants.ORDER_BASE_VALUE, correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("dashboard-service",
                                    "Error in getting records. Message: " + ex.getMessage()))));
        }
    }
    private class DashboardsResponse extends CommonResponse<List<Dashboard>> {
    }

}
