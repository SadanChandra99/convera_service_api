package com.convera.product.web;


import com.convera.product.data.model.*;
import com.convera.product.service.DashboardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardsServiceController.class)

public class DashboardsServiceControllerTest {


    private static final String BASE_PATH = "/convera/dashboard";

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    DashboardsService dashboardsService;

    @Test
    void getDashboards_success() throws Exception {
        when(dashboardsService.getDashboards(any(),any()))
                .thenReturn(new DashboardsResponse(
                        List.of(new Dashboard()),
                        new ResponseMetadata(LocalDateTime.now(), 200, "success", BASE_PATH + "/",
                                Collections.emptyList(), "412fba3a-1517-4dfb-9eba-3195b21b9bc8")));

        MvcResult mvcResult = this.mockMvc.perform(get(BASE_PATH + "/10"))
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void shouldThrowNotFoundWhenNotExist() throws Exception {
        doThrow(HttpClientErrorException.NotFound.class).when(dashboardsService).getDashboards(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/10")).andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowExceptionWhenErrorOccurred() throws Exception {
        doThrow(HttpServerErrorException.InternalServerError.class).when(dashboardsService).getDashboards(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/10")).andExpect(status().isInternalServerError());
    }

    @Test
    void saveDashboards_success() throws Exception {
        Dashboard d1 = new Dashboard(10L,"widget1",1, "1","10","200","100","");
        List<Dashboard> lst = List.of( d1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(lst);


        when(dashboardsService.saveDashboards(any(),any()))
                .thenReturn(new DashboardsResponse(
                        lst,
                        new ResponseMetadata(LocalDateTime.now(), 200, "success", BASE_PATH + "/",
                                Collections.emptyList(), "412fba3a-1517-4dfb-9eba-3195b21b9bc8")));

        MvcResult mvcResult = this.mockMvc.perform(
                        post(BASE_PATH + "/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void shouldThrowExceptionWhenErrorOccurred_saveDashboards() throws Exception {
        Dashboard d1 = new Dashboard(10L,"widget1",1, "1","10","200","100","");
        List<Dashboard> lst = List.of( d1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(lst);

        doThrow(HttpServerErrorException.InternalServerError.class).when(dashboardsService).saveDashboards(any(),any());
        this.mockMvc.perform(
                post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    void shouldThrow400Error_saveDashboards() throws Exception {
        Dashboard d1 = new Dashboard(10L,"widget1",1, "1","10","200","100","");
        List<Dashboard> lst = List.of( d1);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(lst);
        json = "[{\"user_id\":10,\"widget_name\":\"widget1\",\"x\":\"1\",\"y\":\"10\",\"width\":\"200\",\"height\":\"100\",\"description\":\"\"}]";

        ResponseError error = new ResponseError();
        error.setErrorCode(40000);
        error.setErrorMessage("Bad request");
        error.setErrorDetails("correlationId Invalid UUID format");
        error.setServiceName("DashboardService");

        when(dashboardsService.saveDashboards(any(),any()))
        .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatus.BAD_REQUEST,"400",null,null,null));

        this.mockMvc.perform(
                post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isBadRequest());
    }
}
