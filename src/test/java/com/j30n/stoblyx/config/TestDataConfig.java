package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

@TestConfiguration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.j30n.stoblyx.domain.repository")
@EnableTransactionManagement
public class TestDataConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.j30n.stoblyx.domain.model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);
        
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public boolean initializeTestData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // 기존 데이터 삭제
        userRepository.deleteAll();

        // 관리자 계정 생성
        User adminUser = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .nickname("관리자")
            .email("admin@example.com")
            .role(UserRole.ADMIN)
            .build();
        userRepository.save(adminUser);

        // 일반 사용자 계정 생성
        User testUser = User.builder()
            .username("test")
            .password(passwordEncoder.encode("test"))
            .nickname("테스트 사용자")
            .email("test@example.com")
            .role(UserRole.USER)
            .build();
        userRepository.save(testUser);

        return true;
    }
} 