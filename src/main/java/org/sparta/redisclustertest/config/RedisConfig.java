package org.sparta.redisclustertest.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Slf4j
@Profile(value = {"dev", "prod", "qa"})
@EnableRedisRepositories
@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    private final RedisClusterProperties redisClusterProperties;

    @Value("${spring.data.redis.cluster.password}")
    private String password;

    @Value("${spring.data.redis.cluster.nodes}")
    private List<String> nodes;

    @Value("${spring.data.redis.cluster.max-redirects}")
    private int maxRedirects;

    @PostConstruct
    public void test() {
        for (String node : nodes) {
            log.info("node : {}" , node);
        }
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        return container;
    }

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        List<RedisNode> redisNodes = nodes.stream()
                .map(node -> {
                    String[] parts = node.split(":");
                    return new RedisNode(parts[0], Integer.parseInt(parts[1]));
                }).toList();
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(redisNodes);
        return new LettuceConnectionFactory(redisClusterConfiguration);
    }

//    @Bean
//    public RedissonClient redissonClient() {
//        final Config config = new Config();
//
//        ClusterServersConfig csc = config.useClusterServers()
//                .setScanInterval(2000)
//                .setConnectTimeout(100)
//                .setTimeout(3000)
//                .setRetryAttempts(3)
//                .setRetryInterval(1500);
//
//        nodes.forEach(node -> csc.addNodeAddress("redis://" + node));
//
//        return Redisson.create(config);
//    }

    // lettuce 사용시
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        List<RedisNode> redisNodes = nodes.stream()
//                .map(node -> {
//                    String[] parts = node.split(":");
//                    return new RedisNode(parts[0], Integer.parseInt(parts[1]));
//                }).toList();
//
//        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.REPLICA_PREFERRED) // 복제본 노드에서 읽지 만 사용할 수없는 경우 마스터에서 읽습니다.
//                .build();
//        // 모든 클러스터(master, slave) 정보를 적는다. (해당 서버중 접속되는 서버에서 cluster nodes 명령어를 통해 모든 클러스터 정보를 읽어오기에 다운 됐을 경우를 대비하여 모든 노드 정보를 적어두는편이 좋다.)
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
//        redisClusterConfiguration.setClusterNodes(redisNodes);
//
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
//        return lettuceConnectionFactory;
//    }
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        List<RedisNode> redisNodes = nodes.stream()
//                .map(node -> {
//                    String[] parts = node.split(":");
//                    return new RedisNode(parts[0], Integer.parseInt(parts[1]));
//                }).toList();
//
//        // (1) Redis Cluster 설정
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//        clusterConfiguration.setClusterNodes(redisNodes);
//        clusterConfiguration.setMaxRedirects(maxRedirects);
////        clusterConfiguration.setPassword(password);
//
//        // (2) Socket 옵션
//        SocketOptions socketOptions = SocketOptions.builder()
//                .connectTimeout(Duration.ofMillis(100L))    // 소켓 연결 시간 초과 설정
//                .keepAlive(true)                            // 소켓 연결이 일정 시간 동안 사용되지 않더라도 TCP Connection 유지
//                .build();
//
//        // (3) Cluster topology refresh 옵션 (갱신을 제어하기 위한 설정)
//        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                .dynamicRefreshSources(true)                        // 클러스터의 동적 소스 갱신 활성화
//                .enableAllAdaptiveRefreshTriggers()                 // 적응형 갱신 트리거 활성화
//                .enablePeriodicRefresh(Duration.ofMinutes(30L))     // 30분마다 cluster 토폴로지를 업데이트
//                .build();
//
//        // (4) Cluster Client 옵션
//        ClientOptions clientOptions = ClusterClientOptions.builder()
//                .topologyRefreshOptions(clusterTopologyRefreshOptions)
//                .socketOptions(socketOptions)
//                .build();
//
//        // (5) Lettuce Client 옵션
//        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
//                .clientOptions(clientOptions)
//                .commandTimeout(Duration.ofMillis(3000L))
//                .build();
//
//        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
//    }
}
