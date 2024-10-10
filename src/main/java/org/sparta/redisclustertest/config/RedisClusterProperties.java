package org.sparta.redisclustertest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(value = {"dev", "prod", "qa"})
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisClusterProperties {

    private String password;
    private int maxRedirects;
    private List<String> nodes;
}