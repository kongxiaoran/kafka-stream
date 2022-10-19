package finchina.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiConsumer;

/**
 * @Author: kongxr
 * @Date: 2022-10-19 8:42
 * @Description:
 */
@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.host}")
    String host;

    @Value("${elasticsearch.client.username}")
    String username;

    @Value("${elasticsearch.client.password}")
    String password;

    @Value("${elasticsearch.client.connectNum}")
    int connectNum;

    @Value("${elasticsearch.client.connectPerRoute}")
    int connectPerRoute;



    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    protected RestClientBuilder restClientBuilder() {
        String[] hosts = host.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String[] split = hosts[i].split(":");
            HttpHost httpHost = new HttpHost(split[0], Integer.parseInt(split[1]), "http");
            httpHosts[i] = httpHost;
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(httpHosts);
        //设置connectTimeout、socketTimeout
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(100000).setSocketTimeout(100000));
        //使用异步httpclient时设置并发连接数
       //index.mapping.nested_objects.limit
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setMaxConnTotal(connectNum).setMaxConnPerRoute(connectPerRoute));
        return builder;
    }


    @Bean(destroyMethod = "close")
    public BulkProcessor bulkProcessor(@Qualifier("restHighLevelClient") RestHighLevelClient restHighLevelClient) {
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = ((bulkRequest, bulkResponseActionListener) ->
                restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkResponseActionListener)
        );

        BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {
                log.info("Executing bulk [{}] with {} requests", l, bulkRequest.numberOfActions());
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                if (bulkResponse.hasFailures()) {
                    log.warn("Bulk [{}] executed with failures", l);
                } else {
                    log.info("Bulk [{}] completed in {} ms", l, bulkResponse.getTook().getMillis());
                }
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                log.error("Failed to execute bulk [{}], message: {}", l, throwable.getMessage());
            }
        });

        builder.setBulkActions(300000);   //500000，执行一次bulk
        builder.setBulkSize(new ByteSizeValue(50L, ByteSizeUnit.MB));   //每达到200M的请求大小，执行一次bulk
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));  //每10s执行一次bulk
        builder.setConcurrentRequests(1);
        builder.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(500), 3));
        return builder.build();
    }
}
