# istallo_kezelo

Ez a projekt egy webes alapú istálló kezelő rendszer, amely segít a lovak és az istálló mindennapi adminisztrációjában.  
Az alkalmazás célja, hogy egy átlátható, könnyen kezelhető felületen lehessen követni az oltásokat, patkolásokat, etetéseket, valamint a lovak egyéb adatait.


## Fő funkciók

- Lovak adatainak kezelése (név, fajta, nem, születési dátum stb.)
- Oltási, patkolási és egyéb események nyilvántartása
- Etetési naplók rögzítése különböző napszakokhoz
- Felhasználói szerepkörök kezelése (Admin, Tulajdonos, Alkalmazott)
- REST API alapú backend Spring Boot keretrendszerrel, PostgreSQL adatbázissal


## Stack

Backend: Spring Boot (Java 17+)
Frontend: Angular
Adatbázis: PostgreSQL


## Telepítés és futtatás

### 1. Előfeltételek

Szükséges komponensek:

    -Java 17 vagy újabb
    -Maven
    -PostgreSQL 14+


### 2. Adatbázis előkészítése

1. Hozz létre egy üres adatbázist `istallo_kezelo` néven a PostgreSQL-ben.
2. Indítsd el a backendet: a Flyway automatikusan lefuttatja a `src/main/resources/db/migration` alatt lévő migrációkat (pl. `V1__init.sql`), létrehozza a táblákat és az alapértelmezett admin felhasználót.
   - Alapértelmezett admin felhasználó: username: `admin`, jelszó: `admin123`.


### 3. Backend konfigurálása és futtatása

1. A `src/main/resources/application.properties` fájlban állítsd be a PostgreSQL elérési adatokat:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/istallo_kezelo
spring.datasource.username=postgres        # saját Postgres felhasználód
spring.datasource.password=your_password   # saját Postgres jelszavad
```

   - Cseréld le a `spring.datasource.username` és `spring.datasource.password` értékeit a saját PostgreSQL felhasználódra és jelszavadra.
   - A Flyway migrációk kezelik a sémát, ezért a `spring.jpa.hibernate.ddl-auto` alapértelmezett `none` értéke maradhat.

2. A backend indítása terminálból:
```
mvn spring-boot:run
```

3. Az alkalmazás alapértelmezés szerint a `http://localhost:8080` címen érhető el.
