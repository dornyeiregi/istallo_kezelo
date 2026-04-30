# Istálló Kezelő Backend

Az `istallo_kezelo` projekt az Istálló Kezelő rendszer Spring Boot alapú backendje. REST API-t biztosít a frontend számára, kezeli a hitelesítést, az üzleti logikát, valamint a PostgreSQL adatbázis kapcsolatot és a Flyway migrációkat.

Ez a README a backend Docker-alapú használatát írja le.

## Projektek klónozása

Javasolt létrehozni egy közös mappát, például `StableManager` néven, és ebbe klónozni a frontend és a backend projektet is:

```bash
mkdir StableManager
cd StableManager
git clone <frontend-repo-url> istallo_kezelo_frontend
git clone <backend-repo-url> istallo_kezelo
```

Elvárt könyvtárstruktúra:

```text
StableManager/
  istallo_kezelo/
  istallo_kezelo_frontend/
```

## A backend szerepe a Dockeres rendszerben

A backend konténer:

- a [Dockerfile](./Dockerfile) alapján buildelődik
- Java 17 runtime környezetben fut
- PostgreSQL adatbázishoz csatlakozik
- induláskor ellenőrzi és lefuttatja a Flyway migrációkat

Alapértelmezetten a backend a frontend projekt [docker-compose.yml](../istallo_kezelo_frontend/docker-compose.yml) fájljából indul.

Dockeres futtatáskor a frontend és a backend alapértelmezetten LAN-on is elérhető. Ehhez a frontend projekt `.env` fájljában meg kell adni a futtató gép helyi IP-címét.

## Ajánlott indítási mód

A teljes rendszer indítását nem ebből a mappából, hanem a frontend projektből érdemes végezni, mert a Compose ott fogja össze a teljes stacket:

```bash
cd StableManager/istallo_kezelo_frontend
docker compose up --build
```

Ez egyszerre indítja:

- a PostgreSQL adatbázist
- a Spring Boot backendet
- az Angular frontendet

Indítás előtt ellenőrizni kell, hogy a frontend projekt `.env` fájljában a megfelelő helyi IP-cím szerepel-e. Ha szükséges, a minta alapján létrehozható:

```bash
cd StableManager/istallo_kezelo_frontend
cp .env.example .env
```

Ezután a `.env` fájlban az `APP_HOST_IP` értékét a futtató gép saját IP-címére kell állítani, például:

```env
APP_HOST_IP=192.168.0.61
```

## Backend image build

Ha csak a backend image-et szeretnéd felépíteni:

```bash
cd StableManager/istallo_kezelo
docker build -t istallo-kezelo-backend .
```

## A backend futtatása külön konténerként

Ha nem a teljes Compose stacket használod, a backend konténer külön is futtatható, de ilyenkor külső PostgreSQL adatbázist kell megadni környezeti változókkal.

Példa:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/istallo_kezelo \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e APP_CORS_ALLOWED_ORIGINS=http://localhost:4200 \
  -e APP_MAIL_ENABLED=false \
  istallo-kezelo-backend
```

Ez a példa a host gépen futó PostgreSQL adatbázishoz kapcsolja a backendet.

## Fontos környezeti változók

A backend működéséhez Dockerben tipikusan ezek a változók fontosak:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_MAIL_ENABLED`

A Compose-os indítás során ezek automatikusan beállításra kerülnek.

## Adatbázis migrációk

A séma kezelését a Flyway végzi a `src/main/resources/db/migration` könyvtárból.

Induláskor a backend:

- csatlakozik a megadott PostgreSQL adatbázishoz
- ellenőrzi a migrációk állapotát
- szükség esetén lefuttatja az új migrációkat

Ezért külön SQL inicializálás az alap Dockeres működéshez nem szükséges.

## Elérhetőség

Ha a backend a Compose részeként fut, akkor kívülről az alábbi címen érhető el:

```text
http://localhost:8080
```

A frontend a normál működés során ezt az API-t használja.

## Naplók és hibakeresés

Ha a teljes rendszert Compose-ból indítottad, a backend naplója így nézhető meg:

```bash
cd StableManager/istallo_kezelo_frontend
docker compose logs -f backend
```

Állapot ellenőrzése:

```bash
docker compose ps
```

## Gyakori hibák

`A backend elindul, de a frontend nem kap adatot`

Leggyakoribb okok:

- a PostgreSQL adatbázis üres
- a backend más adatbázishoz csatlakozik, mint amire számítasz
- a frontend és a backend nem ugyanazon a hostnéven érhető el a kliens szemszögéből

`Kapcsolódási hiba az adatbázishoz`

Ellenőrizd a `SPRING_DATASOURCE_*` változókat, illetve azt, hogy az adatbázis konténer vagy külső PostgreSQL szerver valóban elérhető-e.

`CORS hiba`

Ellenőrizd a frontend projekt `.env` fájljában az `APP_HOST_IP` értékét. A backend CORS beállítása ebből épül fel, ezért hibás IP-cím esetén a frontend LAN-on nem fog tudni megfelelően kommunikálni az API-val.
