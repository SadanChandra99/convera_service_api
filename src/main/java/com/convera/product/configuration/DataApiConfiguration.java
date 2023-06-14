package com.convera.product.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataApiConfiguration {

    @Autowired
    private final DataApiPropertiesConfiguration dataApiPropertiesConfiguration;

    public DataApiConfiguration(DataApiPropertiesConfiguration dataApiPropertiesConfiguration) {
        this.dataApiPropertiesConfiguration = dataApiPropertiesConfiguration;
    }

}
