package com.sparta;

import com.sparta.config.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApplication {

    private final TestBean testBean;

    @Autowired
    public ProductApplication(TestBean testBean) {
        this.testBean = testBean;
    }

    @PostConstruct
    public void dependencyTest() {
        testBean.dependencyTest();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
