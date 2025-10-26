# 🚀 Hurtig Start Guide

## Hvad er blevet lavet?

✅ **Swagger/OpenAPI dokumentation er tilføjet**
- Dependencies tilføjet til `pom.xml`
- Konfiguration tilføjet til `Main.java`
- Swagger UI vil være tilgængelig på: http://localhost:7070/swagger

✅ **Data seeding er tilføjet**
- `DataSeeder.java` opretter automatisk test data når app'en starter
- Opretter 2 test brugere:
  - `test` / `test123` (med 4 bøger)
  - `admin` / `admin123` (med 1 bog)

✅ **HTTP test fil oprette**
- `seed-data.http` til manuel test af endpoints

---

## Kom i gang NU (3 trin):

### 1. Rebuild og restart din app
```bash
docker-compose down
docker-compose up --build
```

### 2. Åbn Swagger i browseren
```
http://localhost:7070/swagger
```

### 3. Test API'et
1. Find `/api/auth/login` endpoint i Swagger
2. Klik "Try it out"
3. Login med: `test` / `test123`
4. Kopier JWT token fra response
5. Klik på "Authorize" knappen øverst
6. Skriv: `Bearer DIN_TOKEN_HER`
7. Klik "Authorize"
8. Nu kan du teste alle endpoints! 🎉

---

## Test brugere i databasen:

| Username | Password | Antal bøger |
|----------|----------|-------------|
| test     | test123  | 4           |
| admin    | admin123 | 1           |

---

## Bøger i databasen:

**Test bruger's bøger:**
- Harry Potter og De Vises Sten (1997) - Fantasy - Rating: 5
- Ringenes Herre (1954) - Fantasy - Rating: 5
- 1984 (1949) - Dystopi - Rating: 4
- Manden i det Høje Slot (1962) - Science Fiction - Rating: 4

**Admin bruger's bog:**
- Clean Code (2008) - Programmering - Rating: 5

---

## Alle endpoints:

### 🔓 Public (ingen auth påkrævet):
- `POST /api/auth/register` - Opret ny bruger
- `POST /api/auth/login` - Login og få JWT token

### 🔒 Protected (JWT token påkrævet):
- `GET /api/books` - Hent alle dine bøger
- `GET /api/books/{id}` - Hent en specifik bog
- `POST /api/books` - Opret en ny bog
- `PUT /api/books/{id}` - Opdater en bog
- `DELETE /api/books/{id}` - Slet en bog

---

## Tip til Swagger brug:

1. **Authorization værdi format:**
   ```
   Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0...
   ```
   (Husk mellemrum efter "Bearer")

2. **Test flow:**
   - Login → Kopier token → Authorize → Test endpoints

3. **Hver bruger ser kun sine egne bøger**
   - Login som `test` for at se 4 bøger
   - Login som `admin` for at se 1 bog

---

## Alternative test metoder:

### Med VS Code REST Client extension:
- Åbn `seed-data.http`
- Klik "Send Request" på hver endpoint

### Med curl:
```bash
# Login
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

# Hent bøger (indsæt token)
curl http://localhost:7070/api/books \
  -H "Authorization: Bearer DIN_TOKEN"
```

---

## Fejlfinding:

**"Missing or invalid auth header"**
→ Du mangler at authorize i Swagger eller token er forkert

**Ingen data i databasen**
→ DataSeeder kører automatisk første gang. Hvis du vil resette:
```bash
docker-compose down -v  # Sletter database
docker-compose up --build  # Genopret med ny data
```

**Swagger vises ikke**
→ Genstart containeren: `docker-compose restart`

---

## Næste skridt:

Se `NAESTE_TRIN.md` for mere detaljeret dokumentation og forslag til nye features.

God fornøjelse! 🎉📚
