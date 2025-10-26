# Næste Trin - Bookshelf API

## Hvad mangler du at gøre?

Din API kører nu på `http://localhost:7070`, men der mangler to vigtige ting:

1. **API Dokumentation (Swagger/OpenAPI)** - En side hvor du kan se alle endpoints og teste dem
2. **Test data i databasen** - Nogle bøger og brugere til at arbejde med

---

## 1. Tilføj Swagger/OpenAPI Dokumentation

### Trin 1.1: Tilføj dependency til pom.xml

Åbn `pom.xml` og tilføj denne dependency efter de andre dependencies (før `</dependencies>`):

```xml
<!-- Swagger/OpenAPI for Javalin -->
<dependency>
    <groupId>io.javalin.community.openapi</groupId>
    <artifactId>javalin-openapi-plugin</artifactId>
    <version>6.5.0</version>
</dependency>
<dependency>
    <groupId>io.javalin.community.openapi</groupId>
    <artifactId>javalin-swagger-plugin</artifactId>
    <version>6.5.0</version>
</dependency>
```

### Trin 1.2: Opdater Main.java

Erstat din nuværende `Javalin.create()` linje med følgende konfiguration:

```java
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;

// ...

Javalin app = Javalin.create(config -> {
    config.plugins.register(new OpenApiPlugin(pluginConfig -> {
        pluginConfig.withDefinitionConfiguration((version, definition) -> {
            definition.withInfo(info -> {
                info.setTitle("Bookshelf API");
                info.setVersion("1.0.0");
                info.setDescription("API til at håndtere din personlige boghylde");
            });
        });
    }));
    config.plugins.register(new SwaggerPlugin());
}).start(7070);
```

### Trin 1.3: Rebuild projektet

```bash
mvn clean package
docker-compose up --build
```

### Trin 1.4: Åbn Swagger UI

Gå til: **http://localhost:7070/swagger**

Her kan du se alle dine endpoints og teste dem direkte i browseren!

---

## 2. Populer databasen med test data

### Option A: Opret en DataSeeder klasse (Anbefalet)

Opret filen `src/main/java/app/utils/DataSeeder.java`:

```java
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
            
            tx.commit();
            System.out.println("✓ Database er blevet populeret med test data!");
            System.out.println("  - 2 brugere: test/test123 og admin/admin123");
            System.out.println("  - 4 bøger");
            
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Fejl ved seeding af database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

Opdater derefter `Main.java` til at kalde seederen:

```java
// Efter EntityManager er oprettet, tilføj denne linje:
EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookshelfPU");
EntityManager em = emf.createEntityManager();

// Tilføj denne linje:
app.utils.DataSeeder.seedDatabase(em);

// Resten af koden...
```

### Option B: Brug HTTP requests (Hurtig test)

Opret en fil `seed-data.http` i roden af projektet:

```http
### Opret test bruger
POST http://localhost:7070/api/auth/register
Content-Type: application/json

{
  "username": "test",
  "password": "test123"
}

### Login som test bruger
POST http://localhost:7070/api/auth/login
Content-Type: application/json

{
  "username": "test",
  "password": "test123"
}

### Opret bog 1 (husk at indsætte JWT token fra login)
POST http://localhost:7070/api/books
Content-Type: application/json
Authorization: Bearer DIN_JWT_TOKEN_HER

{
  "title": "Harry Potter og De Vises Sten",
  "author": "J.K. Rowling",
  "publishedYear": 1997,
  "genre": "Fantasy",
  "rating": 5
}

### Opret bog 2
POST http://localhost:7070/api/books
Content-Type: application/json
Authorization: Bearer DIN_JWT_TOKEN_HER

{
  "title": "Ringenes Herre",
  "author": "J.R.R. Tolkien",
  "publishedYear": 1954,
  "genre": "Fantasy",
  "rating": 5
}

### Hent alle bøger
GET http://localhost:7070/api/books
Authorization: Bearer DIN_JWT_TOKEN_HER
```

---

## 3. Test din API

### Med Swagger (Anbefalet):
1. Gå til http://localhost:7070/swagger
2. Find `/api/auth/login` endpoint
3. Klik "Try it out"
4. Indtast credentials: `test` / `test123`
5. Kopier JWT token fra response
6. Klik "Authorize" øverst på siden
7. Indtast: `Bearer DIN_TOKEN`
8. Nu kan du teste alle endpoints!

### Med din eksisterende test.http fil:
Opdater `test-with-variables.http` med login først for at få en token.

---

## Dine nuværende endpoints:

### Authentication
- `POST /api/auth/register` - Opret ny bruger
- `POST /api/auth/login` - Login og få JWT token

### Books (kræver JWT token i Authorization header)
- `GET /api/books` - Hent alle dine bøger
- `GET /api/books/{id}` - Hent en specifik bog
- `POST /api/books` - Opret en ny bog
- `PUT /api/books/{id}` - Opdater en bog
- `DELETE /api/books/{id}` - Slet en bog

---

## Hurtig start guide:

1. **Tilføj Swagger** (følg trin 1.1-1.3)
2. **Tilføj DataSeeder** (følg Option A under trin 2)
3. **Genstart din app**: `docker-compose restart`
4. **Åbn Swagger**: http://localhost:7070/swagger
5. **Login med**: username: `test`, password: `test123`
6. **Kopier JWT token** og authorize
7. **Test alle endpoints!**

---

## Troubleshooting

**Problem: Swagger vises ikke**
- Sørg for at dependencies er tilføjet korrekt
- Kør `mvn clean package` igen
- Tjek at du har genstartet containeren

**Problem: "Missing or invalid auth header"**
- Du skal bruge en JWT token fra login
- Husk at inkludere "Bearer " før token
- Token er gyldig i 24 timer

**Problem: Ingen data i databasen**
- Kør DataSeeder (se option A)
- Eller opret data manuelt via Swagger/HTTP requests

---

## Næste features du kunne tilføje:

1. ✅ Swagger dokumentation (følg guide ovenfor)
2. ✅ Test data (følg guide ovenfor)
3. 📝 Søgning og filtrering af bøger
4. 📊 Statistik (antal bøger, gennemsnitsrating, osv.)
5. 🏷️ Tags til bøger
6. 📚 Læselister
7. 📖 "Læser lige nu" status
8. 🔍 Fuld tekst søgning

God fornøjelse med din Bookshelf API! 🚀📚
