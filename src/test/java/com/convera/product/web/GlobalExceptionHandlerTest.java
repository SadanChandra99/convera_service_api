package com.convera.product.web;

import com.convera.product.data.model.Dashboard;
import com.convera.product.data.model.ResponseError;
import com.convera.product.service.DashboardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = DashboardsServiceController.class)
class GlobalExceptionHandlerTest {

    private static final String BASE_PATH = "/convera/dashboard";
    private MockMvc mvc;

    @InjectMocks
    private  DashboardsServiceController controller ;

    @MockBean
    DashboardsService dashboardsService;


    @BeforeEach
    public void setup() {

        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleConstraintViolationException() throws Exception {
        Dashboard d1 = new Dashboard(10L,"widget1",1, "1","10","200","100","");
        List<Dashboard> lst = List.of( d1);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(lst);
        json = "[{\"user_id\":\"a\",\"widget_name\":\"widget1\",\"x\":\"1\",\"y\":\"10\",\"width\":\"200\",\"height\":\"100\",\"description\":\"\"}]";

        ResponseError error = new ResponseError();
        error.setErrorCode(40000);
        error.setErrorMessage("Bad request");
        error.setErrorDetails("correlationId Invalid UUID format");
        error.setServiceName("DashboardService");

        when(dashboardsService.saveDashboards(any(),any()))
                .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatus.BAD_REQUEST,"400",null,null,null));

        MockHttpServletResponse response = this.mvc.perform(
                post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andReturn().getResponse();

    }

    @Test
    void onConstraintValidationException() throws IOException {
        ConstraintViolationException exception = Mockito.mock(ConstraintViolationException.class);
        ServletWebRequest webRequest = Mockito.mock(ServletWebRequest.class);
    }

    @Test
    void handleServiceCallException() {
    }

    @Test
    void handleGlobalException() {
    }
}