package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oCandidateRepository implements CandidateRepository {

    private final Sql2o sql2o;

    public Sql2oCandidateRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Candidate save(Candidate candidate) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO candidates(name, description, creation_date, city_id, file_id)
                    VALUES(:name, :description, :creationDate, :cityId, :fileId)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", candidate.getName())
                    .addParameter("description", candidate.getDescription())
                    .addParameter("creationDate", candidate.getCreationDate())
                    .addParameter("cityId", candidate.getCityId())
                    .addParameter("fileId", candidate.getFileId());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            candidate.setId(generatedId);
            return candidate;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            return connection.createQuery("DELETE FROM candidates WHERE id = :id")
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult() > 0;
        }
    }

    @Override
    public boolean update(Candidate candidate) {
        try (var connection = sql2o.open()) {
            var sql = """
                    UPDATE candidates
                    SET name = :name, description = :description, creation_date = :creationDate,
                        city_id = :cityId, file_id = :fileId
                    WHERE id = :id
                    """;
            return connection.createQuery(sql)
                    .addParameter("name", candidate.getName())
                    .addParameter("description", candidate.getDescription())
                    .addParameter("creationDate", candidate.getCreationDate())
                    .addParameter("cityId", candidate.getCityId())
                    .addParameter("fileId", candidate.getFileId())
                    .addParameter("id", candidate.getId())
                    .executeUpdate()
                    .getResult() > 0;
        }
    }

    @Override
    public Optional<Candidate> findById(int id) {
        try (var connection = sql2o.open()) {
            return Optional.ofNullable(connection.createQuery("SELECT * FROM candidates WHERE id = :id")
                    .addParameter("id", id)
                    .setColumnMappings(Candidate.COLUMN_MAPPING)
                    .executeAndFetchFirst(Candidate.class));
        }
    }

    @Override
    public Collection<Candidate> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM candidates")
                    .setColumnMappings(Candidate.COLUMN_MAPPING)
                    .executeAndFetch(Candidate.class);
        }
    }
}
