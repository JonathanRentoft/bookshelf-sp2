package app.repository;

import app.entities.Book;
import jakarta.persistence.EntityManager;

import java.util.List;

public class BookRepository {

    private final EntityManager em;

    public BookRepository(EntityManager em) {
        this.em = em;
    }

    public Book save(Book book) {
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        return book;
    }

    public Book update(Book book) {
        em.getTransaction().begin();
        Book updated = em.merge(book);
        em.getTransaction().commit();
        return updated;
    }

    public void delete(Book book) {
        em.getTransaction().begin();
        em.remove(em.contains(book) ? book : em.merge(book));
        em.getTransaction().commit();
    }

    public Book findById(Long id) {
        return em.find(Book.class, id);
    }

    public List<Book> findByUserId(Long userId) {
        return em.createQuery("SELECT b FROM Book b WHERE b.user.id = :userId", Book.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Book findByIdAndUserId(Long id, Long userId) {
        try {
            return em.createQuery("SELECT b FROM Book b WHERE b.id = :id AND b.user.id = :userId", Book.class)
                    .setParameter("id", id)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}