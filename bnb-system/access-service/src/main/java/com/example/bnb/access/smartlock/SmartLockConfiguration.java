package com.example.bnb.access.smartlock;

import com.example.bnb.access.smartlock.seam.SeamProperties;
import com.example.bnb.access.smartlock.seam.SeamSmartLockClient;
import com.example.bnb.access.smartlock.yale.YaleProperties;
import com.example.bnb.access.smartlock.yale.YaleSmartLockClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SmartLockConfiguration {
  @Bean
  public StubSmartLockClient stubSmartLockClient() {
    return new StubSmartLockClient();
  }

  @Bean
  public RestTemplate seamRestTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public RestTemplate yaleRestTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public SeamSmartLockClient seamSmartLockClient(RestTemplate seamRestTemplate, SeamProperties props) {
    return new SeamSmartLockClient(seamRestTemplate, props);
  }

  @Bean
  public YaleSmartLockClient yaleSmartLockClient(RestTemplate yaleRestTemplate, YaleProperties props) {
    return new YaleSmartLockClient(yaleRestTemplate, props);
  }

  @Bean
  @Primary
  @ConditionalOnProperty(prefix = "bnb.smartlock", name = "mode", havingValue = "routing")
  public SmartLockClient routingSmartLockClient(YaleSmartLockClient yaleSmartLockClient, SeamSmartLockClient seamSmartLockClient) {
    return new RoutingSmartLockClient(yaleSmartLockClient, seamSmartLockClient);
  }

  @Bean
  @Primary
  @ConditionalOnProperty(prefix = "bnb.smartlock", name = "mode", havingValue = "stub", matchIfMissing = true)
  public SmartLockClient stubSmartLockClientPrimary(StubSmartLockClient stubSmartLockClient) {
    return stubSmartLockClient;
  }
}

