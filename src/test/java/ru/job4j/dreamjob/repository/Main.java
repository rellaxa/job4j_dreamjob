package ru.job4j.dreamjob.repository;

import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;

import java.util.Collection;
import java.util.Properties;

public class Main {

    private static Sql2oVacancyRepository sql2oVacancyRepository;

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oVacancyRepository = new Sql2oVacancyRepository(sql2o);
        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    public static void clearTables() {
        try (var connection = sql2o.open()) {
            var deleteCandidates = "DELETE FROM candidates";
            var deleteVacancies = "DELETE FROM vacancies";
            var deleteFiles = "DELETE FROM files";
            var deleteUsers = "DELETE FROM users";

            connection.createQuery(deleteCandidates).executeUpdate();
            connection.createQuery(deleteVacancies).executeUpdate();
            connection.createQuery(deleteFiles).executeUpdate();
            connection.createQuery(deleteUsers).executeUpdate();
        }
    }

    public static Collection<File> getFromFiles() {
        try (var connection = sql2o.open()) {
            var getFromFiles = "SELECT * FROM files";
            return connection.createQuery(getFromFiles).executeAndFetch(File.class);
        }
    }

    public static void getFromTables() {
        var vacanciesList = sql2oVacancyRepository.findAll();
        var candidatesList = sql2oCandidateRepository.findAll();
        var usersList = sql2oUserRepository.findAll();
        var filesList = getFromFiles();

        System.out.println("Vacancies list: " + vacanciesList);
        System.out.println("Candidates list: " + candidatesList);
        System.out.println("Users list: " + usersList);
        System.out.println("Files list: " + filesList);
    }

    public static void main(String[] args) throws Exception {
        initRepositories();
        clearTables();
        getFromTables();
    }
}
