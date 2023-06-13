package com.Silver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@SpringBootTest
@Component
@ConfigurationProperties(prefix = "datasource")
public class DataSourceTest {

    @Autowired
    private DataSource dataSource;
    @Test
    public void test() throws Exception {

        System.out.println(dataSource);
    }
}
