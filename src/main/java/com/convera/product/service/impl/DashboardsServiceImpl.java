package com.convera.product.service.impl;




import com.convera.product.constants.ErrorConstants;
import com.convera.product.data.api.DashboardApi;
import com.convera.product.data.model.Dashboard;
import com.convera.product.data.model.DashboardsResponse;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.exception.ServiceExternalException;
import com.convera.product.service.DashboardsService;
import datadog.trace.api.Trace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service("DashboardsService")
@Slf4j
public class DashboardsServiceImpl implements DashboardsService {

    @Autowired
    DashboardApi dashboardApi;


    @Override
    @Trace
<<<<<<< HEAD
//    @Cacheable(cacheNames = "dashboardCache", key = "#userId")
=======
//    @Cacheable(cacheNames = "dashboardCache", key = "'#userId'")
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
    public DashboardsResponse getDashboards(Long userId, String correlationId) {
        DashboardsResponse response;
        try {
        	
            response = dashboardApi.getDashboards( userId,  correlationId);
        } catch (Exception e) {
            log.error(String.format(ErrorConstants.DASHBOARD_DATA_ERROR, userId));
            throw new DataNotFoundException(String.format(ErrorConstants.DASHBOARD_DATA_ERROR, userId));
        }
        if (CollectionUtils.isEmpty(response.getData())) {
            log.error(ErrorConstants.DASHBOARD_NOT_FOUND, userId);
            throw new DataNotFoundException(String.format(ErrorConstants.DASHBOARD_NOT_FOUND,userId));
        }
        return response;
    }

    @Override
    @Trace
    public DashboardsResponse saveDashboards(List<Dashboard> request, String correlationId) {
        DashboardsResponse response;
        try {
            response = dashboardApi.saveDashboards(request,  correlationId);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("dashboard-service bad request getLocalizedMessage:", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("dashboard-service", ex);
            throw ex;
        }
        return response;
    }
}
