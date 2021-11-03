package cn.imcao.influxdbapi.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBAutoConfiguration {

    @Bean
    public InfluxDBClient client(InfluxDBProperties properties) {

        return InfluxDBClientFactory.create(properties.getUrl(), properties.getToken(), properties.getOrg(), properties.getBucket());
    }
}
