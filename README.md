# Istálló Kezelő Backend

Ez a mappa a rendszer backendje. A teljes alkalmazást nem innen, hanem a frontend projektből érdemes elindítani, mert ott van a `docker-compose.yml`, ami együtt indítja:

- a frontendet
- a backendet
- az adatbázist

## Mire lesz szükséged?

- Docker Desktop
- Git
- a két projekt egymás mellett legyen ugyanabban a mappában

Az elvárt mappaszerkezet:

```text
StableManager/
  istallo_kezelo/
  istallo_kezelo_frontend/
```

## Lépésről lépésre

### 1. Klónozd le a két projektet

```bash
mkdir StableManager
cd StableManager
git clone <frontend-repo-url> istallo_kezelo_frontend
git clone <backend-repo-url> istallo_kezelo
```

### 2. Menj a frontend mappába

```bash
cd StableManager/istallo_kezelo_frontend
```

Innen fogod indítani az egész rendszert.

### 3. Hozd létre a `.env` fájlt

```bash
cp .env.example .env
```

Ezután nyisd meg a `.env` fájlt.

### 4. Írd át a `.env` fájlt

A legfontosabb sorok:

```env
APP_HOST_IP=192.168.0.61
JWT_SECRET=ide-egy-hosszu-sajat-titok
APP_MAIL_ENABLED=false
```

Mit jelentenek?

- `APP_HOST_IP`: annak a gépnek a helyi IP-címe, amelyen a Docker fut
- `JWT_SECRET`: saját titkos kulcs, ezt mindig töltsd ki egy hosszú egyedi értékkel
- `APP_MAIL_ENABLED=false`: ha nem akarsz email küldést használni, maradhat így

### 5. Ha kell email küldés, ezt is töltsd ki

Ha az alkalmazás emailt is küldjön, akkor a `.env` fájlban ezt állítsd be:

```env
APP_MAIL_ENABLED=true
APP_MAIL_FROM=pelda@ceg.hu
APP_MAIL_TO=
SPRING_MAIL_USERNAME=pelda@ceg.hu
SPRING_MAIL_PASSWORD=ide-a-sajat-app-jelszo
```

Fontos:

- minden telepítő a saját email címét és saját jelszavát adja meg
- ne ossz ki mindenkinek ugyanazt a közös email jelszót
- `APP_MAIL_TO` maradhat üresen

### 6. Indítsd el a rendszert

```bash
docker compose up --build
```

Ez első indításkor több perc is lehet.

### 7. Nyisd meg a böngészőben

Frontend:

```text
http://localhost:4200
```

Backend:

```text
http://localhost:8080
```

### 8. Alapértelmezett admin belépés

- Felhasználónév: `admin`
- Jelszó: `admin123`

## Mit kell pontosan beírni a `.env` fájlba?

Ha csak az alap működés kell email nélkül, akkor elég valami ilyesmi:

```env
APP_HOST_IP=192.168.0.61
FRONTEND_BIND_HOST=0.0.0.0
BACKEND_BIND_HOST=0.0.0.0
DB_BIND_HOST=127.0.0.1
JWT_SECRET=ide-egy-hosszu-sajat-titok
APP_MAIL_ENABLED=false
APP_MAIL_FROM=
APP_MAIL_TO=
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
```

Ha kell email is, akkor csak az utolsó sorokat kell kitöltened.

## Leállítás

Ha le akarod állítani:

```bash
docker compose down
```

Ha az adatbázis adatait is törölni akarod:

```bash
docker compose down -v
```

## Hasznos parancsok

Futó konténerek:

```bash
docker compose ps
```

Backend napló:

```bash
docker compose logs -f backend
```

Összes napló:

```bash
docker compose logs -f
```

## Gyakori hibák

`Nem indul el a backend`

Ellenőrizd, hogy a két projekt tényleg egymás mellett van-e:

```text
StableManager/
  istallo_kezelo/
  istallo_kezelo_frontend/
```

`A frontend nem éri el a backendet`

Nézd meg, jól van-e beírva az `APP_HOST_IP` a `.env` fájlban.

`Email nem működik`

Ilyenkor általában ezek valamelyike hibás:

- `APP_MAIL_ENABLED`
- `APP_MAIL_FROM`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`

`Port already in use`

Valami más program már használja a `4200`, `8080` vagy `5432` portot.

## Ha csak a backend image kell

```bash
cd StableManager/istallo_kezelo
docker build -t istallo-kezelo-backend .
```
