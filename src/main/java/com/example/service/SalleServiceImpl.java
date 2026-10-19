package com.example.service;

import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.util.PaginationResult;
import java.time.LocalDateTime;
import java.util.List;

public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;

    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Override
    public List<Salle> getAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        verifierCreneau(debut, fin);
        return salleRepository.findAvailableRooms(debut, fin);
    }

    @Override
    public List<Salle> searchRooms(String nom, Integer capaciteMin, LocalDateTime debut, LocalDateTime fin,
                                   int page, int size) {
        verifierPagination(page, size);
        if (debut != null && fin != null) verifierCreneau(debut, fin);
        return salleRepository.searchRooms(nom, capaciteMin, debut, fin, page, size);
    }

    @Override
    public long countRooms(String nom, Integer capaciteMin, LocalDateTime debut, LocalDateTime fin) {
        return salleRepository.countRooms(nom, capaciteMin, debut, fin);
    }

    @Override
    public PaginationResult<Salle> searchRoomsPaginated(String nom, Integer capaciteMin,
                                                        LocalDateTime debut, LocalDateTime fin,
                                                        int page, int size) {
        List<Salle> items = searchRooms(nom, capaciteMin, debut, fin, page, size);
        long total = countRooms(nom, capaciteMin, debut, fin);
        return new PaginationResult<>(items, page, size, total);
    }


    private void verifierCreneau(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null)
            throw new IllegalArgumentException("Le début et la fin du créneau sont obligatoires");
        if (!fin.isAfter(debut))
            throw new IllegalArgumentException("La fin du créneau doit être après le début");
    }

    private void verifierPagination(int page, int size) {
        if (page < 1) throw new IllegalArgumentException("La page doit être >= 1");
        if (size < 1) throw new IllegalArgumentException("La taille de page doit être >= 1");
    }
}