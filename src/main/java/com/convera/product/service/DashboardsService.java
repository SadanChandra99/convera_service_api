package com.convera.product.service;

import com.convera.product.data.model.Dashboard;
import com.convera.product.data.model.DashboardsResponse;

import java.util.List;

public interface DashboardsService {

    DashboardsResponse getDashboards(Long userId, String correlationId);

    DashboardsResponse saveDashboards(List<Dashboard> request, String correlationId);
}
