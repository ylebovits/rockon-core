package com.cis.rockon;

import com.cis.rockon.model.User;
import com.cis.rockon.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableSwagger2
@EnableJpaRepositories
public class Application {

    final
    UserRepository repository;

    public Application(UserRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "RockOn Core",
                "REST API for RockOn",
                "1.0.0",
                null,
                new Contact("Yaki Lebovits","https://www.cis.temple.edu","yakir@temple.edu"),
                "GPL 2",
                "https://www.gnu.org/licenses/old-licenses/gpl-2.0.html",
                Collections.emptyList()
        );
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {

        return (args) -> {

            repository.deleteAll();

            User[] mockUsers = new User[]{
                    new User()
                            .setFirstName("Hooter T.").setLastName("Owl")
                            .setPhoneNumber("215 420 6969").setEmail("cis1@temple.edu")
                            .setBirthday(LocalDate.ofEpochDay(0)).setBiography("I'm just here to test!").setYearsOfExperience(69)
                            .setLastSeenLocation(new double[]{39.981996346936, -75.153041291542}) // SERC
                            .setSearchRadius(25 /* km */).setTypeSportClimbing(true).setTypeFreeSolo(true)
                            .setTypeTopRope(false).setTypeFreeSolo(false).setTypeBouldering(null),
                    new User()
                            .setFirstName("Stella D.").setLastName("Owl")
                            .setPhoneNumber("215 420 6969").setEmail("cis2@temple.edu")
                            .setBirthday(LocalDate.ofEpochDay(0)).setBiography("I'm just here to test!").setYearsOfExperience(420)
                            .setLastSeenLocation(new double[]{39.981348547211, -75.154349633751}) // Bell Tower
                            .setSearchRadius(25 /* km */).setTypeSportClimbing(true).setTypeFreeSolo(true)
                            .setTypeTopRope(false).setTypeFreeSolo(false).setTypeBouldering(null),
                    new User()
                            .setFirstName("Jason").setLastName("Wingard")
                            .setPhoneNumber("215 420 6969").setEmail("president@temple.edu")
                            .setBirthday(LocalDate.ofEpochDay(0)).setBiography("I'm just here to test!").setYearsOfExperience(69)
                            .setLastSeenLocation(new double[]{39.9806376631842, -75.154914261904}) // Tuttleman
                            .setSearchRadius(25 /* km */).setTypeSportClimbing(true).setTypeFreeSolo(true)
                            .setTypeTopRope(false).setTypeFreeSolo(true).setTypeBouldering(null),
                    new User()
                            .setFirstName("Dick").setLastName("Cheney")
                            .setPhoneNumber("215 420 6969").setEmail("dick@temple.edu")
                            .setBirthday(LocalDate.ofEpochDay(0)).setBiography("I'm just here to test!").setYearsOfExperience(69)
                            .setLastSeenLocation(new double[]{38.8976579135955, -77.036553189077}) // White House
                            .setSearchRadius(25 /* km */).setTypeSportClimbing(true).setTypeFreeSolo(true)
                            .setTypeTopRope(false).setTypeFreeSolo(false).setTypeBouldering(null)
            };

            mockUsers[0].getConnections().add(mockUsers[1]);
            mockUsers[0].getConnections().add(mockUsers[2]);
            mockUsers[0].getConnections().add(mockUsers[3]);
            repository.save(mockUsers[0]);

            mockUsers[2].getConnections().add(mockUsers[0]);
            repository.save(mockUsers[2]);

            repository.saveAll(Arrays.asList(mockUsers));
        };
    }
}
