package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }

        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(dataSource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearTable() {
        sql2oUserRepository.deleteAll();
    }

    @Test
    public void whenSavedAndGetTheSame() {
        var user = sql2oUserRepository.save(new User(0, "email", "name", "111")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(user).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "email1", "name1", "111")).get();
        var user2 = sql2oUserRepository.save(new User(0, "email2", "name2", "111")).get();
        var user3 = sql2oUserRepository.save(new User(0, "email3", "name3", "111")).get();
        var savedUsers = sql2oUserRepository.findAll();
        assertThat(savedUsers).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenSaveTheSameEmailAndGetOptional() {
        var email = "email";
        var user1 = sql2oUserRepository.save(new User(0, email, "name", "111"));
        var user2 = sql2oUserRepository.save(new User(0, email, "name", "111"));
        assertThat(user1).isPresent();
        assertThat(user2).isEmpty();
    }

    @Test
    public void whenNotFoundByWrongEmailAndPassword() {
        var wrongEmail = "new_email";
        var password = "password";
        sql2oUserRepository.save(new User(0, "email", "name", "111"));
        assertThat(sql2oUserRepository.findByEmailAndPassword(wrongEmail, password)).isEqualTo(empty());
    }
}