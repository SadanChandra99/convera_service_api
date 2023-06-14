package com.convera.product;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
class ApplicationStartUpTests {

  
  // uncomment below code after adding redis server in container
   @Test
   void shouldHaveWorkingMainMethod() {
     ServiceApiApplication.main(new String[] {"--spring.profiles.active=test"});
     assertTrue(true);
   }

}
