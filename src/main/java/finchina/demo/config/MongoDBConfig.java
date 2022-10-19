package finchina.demo.config;


import ch.qos.logback.classic.gaffer.PropertyUtil;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * @Author: kongxr
 * @Date: 2022-10-18 16:16
 * @Description:
 */

@Configuration
@Order(0)
public class MongoDBConfig {

    @Value("${spring.data.mongodb.host}")
    String host;

    @Value("${spring.data.mongodb.port}")
    String port;

    @Value("${spring.data.mongodb.username}")
    String username;

    @Value("${spring.data.mongodb.password}")
    String password;

    @Value("${spring.data.mongodb.database}")
    String database;


    @Bean
    public MongoTemplate mongoTemplate() throws Exception {

        return new MongoTemplate(mongoMpcFactory());
    }

    public MongoDatabaseFactory mongoMpcFactory() throws Exception {
        String url = "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database;
        SimpleMongoClientDatabaseFactory simpleMongoClientDbFactory = new SimpleMongoClientDatabaseFactory(url);
        return simpleMongoClientDbFactory;
    }


}
