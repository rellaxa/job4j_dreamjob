package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final ConcurrentMap<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Candidate 1", "Claim to be Junior", LocalDateTime.now()));
        save(new Candidate(0, "Candidate 2", "Claim to be Junior+", LocalDateTime.now()));
        save(new Candidate(0, "Candidate 3", "Claim to be Middle", LocalDateTime.now()));
        save(new Candidate(0, "Candidate 4", "Claim to be Middle+", LocalDateTime.now()));
        save(new Candidate(0, "Candidate 5", "Claim to be Senior", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(),
                        candidate.getDescription(), candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
