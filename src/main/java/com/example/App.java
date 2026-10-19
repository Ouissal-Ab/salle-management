package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        try {
            initialiserDonnees(em);


            SalleRepository repository = new SalleRepositoryImpl(em);
            SalleService salleService = new SalleServiceImpl(repository);

            LocalDateTime debut = LocalDateTime.of(2024, 6, 1, 9, 0);
            LocalDateTime fin = LocalDateTime.of(2024, 6, 1, 11, 0);


            List<Salle> disponibles = salleService.getAvailableRooms(debut, fin);
            System.out.println("\n===== TEST 1 : Salles disponibles entre " + debut + " et " + fin + " =====");
            disponibles.forEach(s -> System.out.println("- " + s));


            List<Salle> resultat = salleService.searchRooms("salle", 20, debut, fin, 1, 10);
            System.out.println("\n===== TEST 2 : nom contient 'salle', capacité >= 20, disponibles =====");
            resultat.forEach(s -> System.out.println("- " + s));


            System.out.println("\n===== TEST 3 : Pagination (3 salles par page) =====");
            int page = 1;
            PaginationResult<Salle> p;
            do {
                p = salleService.searchRoomsPaginated(null, null, null, null, page, 3);
                System.out.println("Page " + p.getCurrentPage() + "/" + p.getTotalPages()
                        + " (total : " + p.getTotalItems() + " salles)");
                p.getItems().forEach(s -> System.out.println("   - " + s));
                page++;
            } while (p.hasNext());

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void initialiserDonnees(EntityManager em) {
        em.getTransaction().begin();

        Salle a = new Salle("Salle A", 10);
        Salle b = new Salle("Salle B", 25);
        Salle c = new Salle("Salle C", 50);
        Salle d = new Salle("Salle D", 30);
        Salle e = new Salle("Amphi 1", 200);
        Salle f = new Salle("Salle E", 15);
        Salle g = new Salle("Salle F", 40);


        b.addReservation(new Reservation(LocalDateTime.of(2024, 6, 1, 10, 0), LocalDateTime.of(2024, 6, 1, 12, 0)));

        c.addReservation(new Reservation(LocalDateTime.of(2024, 6, 1, 11, 0), LocalDateTime.of(2024, 6, 1, 13, 0)));

        d.addReservation(new Reservation(LocalDateTime.of(2024, 6, 1, 8, 0), LocalDateTime.of(2024, 6, 1, 9, 30)));

        for (Salle s : new Salle[]{a, b, c, d, e, f, g}) {
            em.persist(s);
        }

        em.getTransaction().commit();
        em.clear();
    }
}