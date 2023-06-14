package com.convera.product.service.impl;

import com.convera.product.constants.ErrorConstants;
import com.convera.product.data.api.DashboardApi;
import com.convera.product.data.model.Dashboard;
import com.convera.product.data.model.DashboardsResponse;
import com.convera.product.data.model.ResponseMetadata;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.exception.ServiceExternalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DashboardsServiceImplTest {

    @InjectMocks
    private DashboardsServiceImpl dashboardsService;

    @Mock
    DashboardApi dashboardApi;

    @Test
    void getDashboards() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of(dashboard);
        DashboardsResponse dr = new DashboardsResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        dr.setData(data);
        dr.setMetadata(metadata);

        Mockito.when(dashboardApi.getDashboards(any(),any())).thenReturn(dr);
        Assertions.assertNotNull(dashboardsService.getDashboards(userId, correlationId));
    }

    @Test
    void getDashboardsWithNotFound() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of();
        DashboardsResponse dr = new DashboardsResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        dr.setData(data);
        dr.setMetadata(metadata);

        Mockito.when(dashboardApi.getDashboards(any(),any())).thenReturn(dr);

        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            dashboardsService.getDashboards(userId, correlationId);
        });

        Assertions.assertEquals(String.format(ErrorConstants.DASHBOARD_NOT_FOUND,userId), thrown .getMessage());

    }

    @Test
    void getDashboardsWithErrorApi() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of();
        DashboardsResponse dr = new DashboardsResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        dr.setData(data);
        dr.setMetadata(metadata);

        Mockito.when(dashboardApi.getDashboards(any(),any())).thenThrow(new RestClientException(""));

        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            dashboardsService.getDashboards(userId, correlationId);
        });

        Assertions.assertNotNull(thrown);

    }

    @Test
    void saveDashboards() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of(dashboard);
        DashboardsResponse dashboardsResponse = new DashboardsResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        dashboardsResponse.setData(data);
        dashboardsResponse.setMetadata(metadata);

        Mockito.when(dashboardApi.saveDashboards(any(),any())).thenReturn(dashboardsResponse);
        DashboardsResponse result = dashboardsService.saveDashboards(data, correlationId);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(1L, result.getData().size());
        Assertions.assertEquals(userId, result.getData().get(0).getUserId());
    }


    @Test
    void saveDashboardsWithException() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of(dashboard);

        Mockito.when(dashboardApi.saveDashboards(any(),any())).thenThrow(new RestClientException(""));


        Exception t = Assertions.assertThrows(RestClientException.class, () -> {
            dashboardsService.saveDashboards(data, correlationId);
        });

        Assertions.assertNotNull(t);
    }

    @Test
    void saveDashboardsWithBadRequedt() {
        String correlationId = "79d69567-013a-4b29-ae8c-8e007a711858";
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(userId);
        dashboard.setWidgetName("WidgetName");

        List<Dashboard> data = List.of(dashboard);

        Mockito.when(dashboardApi.saveDashboards(any(),any()))
                .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatus.BAD_REQUEST,"400",null,null,null));


        HttpClientErrorException t = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            dashboardsService.saveDashboards(data, correlationId);
        });

        Assertions.assertNotNull(t);
    }

}