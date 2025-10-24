package app.utils;

import app.entities.Book;
import app.entities.User;
import app.security.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Database populator to create test data for the Bookshelf API
 */
public class DatabasePopulator {

    private final EntityManagerFactory emf;

    public DatabasePopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Populates the database with sample users and books
     */
    public void populate() {
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();

            // Check if data already exists
            Long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .getSingleResult();
            
            if (userCount > 0) {
                System.out.println("Database already populated. Skipping...");
                em.getTransaction().rollback();
                return;
            }

            // Create sample users
            User alice = new User("alice", PasswordUtil.hashPassword("password123"));
            User bob = new User("bob", PasswordUtil.hashPassword("securepass"));
            User admin = new User("admin", PasswordUtil.hashPassword("admin123"));
            admin.setRole("ADMIN");

            em.persist(alice);
            em.persist(bob);
            em.persist(admin);

            // Create sample books for Alice
            Book book1 = new Book("The Hobbit", "J.R.R. Tolkien", alice);
            Book book2 = new Book("1984", "George Orwell", alice);
            Book book3 = new Book("Pride and Prejudice", "Jane Austen", alice);

            em.persist(book1);
            em.persist(book2);
            em.persist(book3);

            // Create sample books for Bob
            Book book4 = new Book("Harry Potter and the Philosopher's Stone", "J.K. Rowling", bob);
            Book book5 = new Book("The Great Gatsby", "F. Scott Fitzgerald", bob);

            em.persist(book4);
            em.persist(book5);

            // Create sample books for Admin
            Book book6 = new Book("Clean Code", "Robert C. Martin", admin);
            Book book7 = new Book("Design Patterns", "Gang of Four", admin);

            em.persist(book6);
            em.persist(book7);

            em.getTransaction().commit();
            
            System.out.println("Database populated successfully!");
            System.out.println("Created 3 users (alice, bob, admin) and 7 books");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error populating database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Clears all data from the database
     */
    public void clear() {
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // Delete all books first (foreign key constraint)
            em.createQuery("DELETE FROM Book").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            
            em.getTransaction().commit();
            System.out.println("Database cleared successfully!");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error clearing database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
