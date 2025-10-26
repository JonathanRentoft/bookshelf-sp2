package app.utils;

import app.entities.User;
import app.entities.Book;
import app.security.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DataSeeder {
    
    public static void seedDatabase(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            // Tjek om der allerede er data
            Long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            if (userCount > 0) {
                System.out.println("Database er allerede populeret, springer seeding over");
                tx.commit();
                return;
            }
            
            // Opret test brugere
            User user1 = new User();
            user1.setUsername("test");
            user1.setPassword(PasswordUtil.hashPassword("test123"));
            em.persist(user1);
            
            User user2 = new User();
            user2.setUsername("admin");
            user2.setPassword(PasswordUtil.hashPassword("admin123"));
            em.persist(user2);
            
            // Flush for at få IDs
            em.flush();
            
            // Opret nogle bøger til test brugeren
            Book book1 = new Book();
            book1.setTitle("Harry Potter og De Vises Sten");
            book1.setAuthor("J.K. Rowling");
            book1.setPublishedYear(1997);
            book1.setGenre("Fantasy");
            book1.setRating(5);
            book1.setUser(user1);
            em.persist(book1);
            
            Book book2 = new Book();
            book2.setTitle("Ringenes Herre");
            book2.setAuthor("J.R.R. Tolkien");
            book2.setPublishedYear(1954);
            book2.setGenre("Fantasy");
            book2.setRating(5);
            book2.setUser(user1);
            em.persist(book2);
            
            Book book3 = new Book();
            book3.setTitle("1984");
            book3.setAuthor("George Orwell");
            book3.setPublishedYear(1949);
            book3.setGenre("Dystopi");
            book3.setRating(4);
            book3.setUser(user1);
            em.persist(book3);
            
            Book book4 = new Book();
            book4.setTitle("Clean Code");
            book4.setAuthor("Robert C. Martin");
            book4.setPublishedYear(2008);
            book4.setGenre("Programmering");
            book4.setRating(5);
            book4.setUser(user2);
            em.persist(book4);
            
            Book book5 = new Book();
            book5.setTitle("Manden i det Høje Slot");
            book5.setAuthor("Philip K. Dick");
            book5.setPublishedYear(1962);
            book5.setGenre("Science Fiction");
            book5.setRating(4);
            book5.setUser(user1);
            em.persist(book5);
            
            tx.commit();
            System.out.println("✓ Database er blevet populeret med test data!");
            System.out.println("  - 2 brugere: test/test123 og admin/admin123");
            System.out.println("  - 5 bøger");
            
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Fejl ved seeding af database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
