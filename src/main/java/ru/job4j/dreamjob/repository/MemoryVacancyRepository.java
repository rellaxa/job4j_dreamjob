package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private int nextId = 1;

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Intern vacancy"));
        save(new Vacancy(0, "Junior Java Developer", "Junior vacancy"));
        save(new Vacancy(0, "Junior+ Java Developer", "Upper Junior vacancy"));
        save(new Vacancy(0, "Middle Java Developer", "Middle vacancy"));
        save(new Vacancy(0, "Middle+ Java Developer", "Upper Middle vacancy"));
        save(new Vacancy(0, "Senior Java Developer", "Senior vacancy"));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(),
                        vacancy.getDescription())) != null;
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