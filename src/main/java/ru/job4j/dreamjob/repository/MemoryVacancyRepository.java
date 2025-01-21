package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final ConcurrentMap<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Intern vacancy", LocalDateTime.now()));
        save(new Vacancy(0, "Junior Java Developer", "Junior vacancy", LocalDateTime.now()));
        save(new Vacancy(0, "Junior+ Java Developer", "Upper Junior vacancy", LocalDateTime.now()));
        save(new Vacancy(0, "Middle Java Developer", "Middle vacancy", LocalDateTime.now()));
        save(new Vacancy(0, "Middle+ Java Developer", "Upper Middle vacancy", LocalDateTime.now()));
        save(new Vacancy(0, "Senior Java Developer", "Senior vacancy", LocalDateTime.now()));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(id, vacancy.getTitle(),
                        vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}