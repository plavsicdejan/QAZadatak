package rs.hooloovoo.test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import rs.hooloovoo.test.configuration.DbConfiguration;
import rs.hooloovoo.test.configuration.EnvironmentConfig;
import rs.hooloovoo.test.repository.UserRepository;


@Configuration
@Import({
        EnvironmentConfig.class,
        DbConfiguration.class
})
public class TestConfig {

    @Autowired
    DbConfiguration databaseConfig;

    @Autowired
    UserRepository userRepository;


}
