package com.kibo.reservation.config;

import com.kibo.reservation.entity.Drop;
import com.kibo.reservation.repository.DropRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class DataSeeder implements CommandLineRunner {
    private final DropRepository repository;

    public DataSeeder(DropRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;
        repository.save(drop("Limited Sneaker Drop", 100));
        repository.save(drop("Concert Ticket Drop", 50));
        repository.save(drop("Restaurant Table Drop", 20));
    }

    private Drop drop(String name, int units) {
        Drop d = new Drop();
        d.setName(name);
        d.setTotalUnits(units);
        d.setAvailableUnits(units);
        d.setStartTime(Instant.now());
        d.setCreatedAt(Instant.now());
        d.setUpdatedAt(Instant.now());
        return d;
    }
}
