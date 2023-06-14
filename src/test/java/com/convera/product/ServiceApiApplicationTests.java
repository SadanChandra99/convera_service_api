package com.convera.product;

<<<<<<< HEAD


import org.junit.jupiter.api.Test;
=======
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServiceApiApplicationTests {

<<<<<<< HEAD

=======
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228

	@Autowired
	private com.convera.product.data.api.ProductsApi productsApi;

	@Autowired
	private RestTemplate restTemplate;
	
// uncomment below code after adding redis server in container
	 @Test
	 void contextLoads() {
	 	
	 	assertThat(productsApi).isNotNull();
	 	assertThat(restTemplate).isNotNull();
	 }

}
