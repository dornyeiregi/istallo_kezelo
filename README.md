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

### 3. Nyisd meg a `.env` fájlt

A frontend projektben már van egy kész `.env` fájl mintaadatokkal. Ezt a fájlt nyisd meg:

```text
StableManager/istallo_kezelo_frontend/.env
```

### 4. Írd át a `.env` fájlt

A legfontosabb sorok:

```env
APP_HOST_IP=192.168.0.61
JWT_SECRET=ide-egy-hosszu-sajat-titok
APP_MAIL_ENABLED=true
APP_MAIL_FROM=sajat@email.hu
APP_MAIL_TO=
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=sajat@email.hu
SPRING_MAIL_PASSWORD=sajat-app-jelszo
```

Mit jelentenek?

- `APP_HOST_IP`: annak a gépnek a helyi IP-címe, amelyen a Docker fut
- `JWT_SECRET`: saját titkos kulcs, ezt mindig töltsd ki egy hosszú egyedi értékkel
- `APP_MAIL_ENABLED`: email küldés be legyen-e kapcsolva
- `APP_MAIL_FROM`: a feladó email cím
- `APP_MAIL_TO`: maradhat üresen
- `SPRING_MAIL_HOST` és `SPRING_MAIL_PORT`: Gmail esetén maradhat `smtp.gmail.com` és `587`
- `SPRING_MAIL_USERNAME`: ugyanaz legyen, mint az `APP_MAIL_FROM`
- `SPRING_MAIL_PASSWORD`: az email fiók app jelszava vagy SMTP jelszava

Fontos:

- minden telepítő a saját email címét és saját jelszavát adja meg
- az `APP_MAIL_FROM` és a `SPRING_MAIL_USERNAME` legyen ugyanaz az email cím
- ha nem Gmailt használsz, akkor a `SPRING_MAIL_HOST` és `SPRING_MAIL_PORT` értékét is írd át

Az érzékeny adatok, például a `JWT_SECRET`, a mail felhasználónév és a mail jelszó nem a backend forráskódban vannak, hanem ebben a `.env` fájlban. A GitHub repóban csak mintaértékek vannak.

### 5. Indítsd el a rendszert

Terminálból:

```bash
docker compose up --build
```

Ez első indításkor több perc is lehet.

Docker Desktopból:

1. Nyisd meg a Docker Desktop alkalmazást.
2. Menj a `Containers` vagy `Containers / Apps` részre.
3. Ha a projekt már egyszer el lett indítva, keresd meg az `istallo_kezelo_frontend` compose appot vagy a hozzá tartozó konténereket.
4. Kattints a `Start` vagy `Run` gombra.

Ha a projekt még nincs betöltve a Docker Desktopba, akkor az első indítást általában egyszerűbb terminálból elvégezni a `docker compose up --build` paranccsal. Utána a következő indítások és leállítások már kényelmesen kezelhetők a Docker Desktop felületéről.

### 6. Nyisd meg a böngészőben

Normál használatnál a frontendet kell megnyitni:

```text
http://APP_HOST_IP:4200
```

A backendet általában nem kell külön megnyitni böngészőben. Az a frontend mögött fut API-ként. Csak hibakereséshez vagy teszteléshez lehet hasznos ez a cím:

```text
http://localhost:8080
```

### 7. Alapértelmezett admin belépés

- Felhasználónév: `admin`
- Jelszó: `admin123`

## Leállítás

Terminálból:

```bash
docker compose down
```

Docker Desktopból:

1. Nyisd meg a Docker Desktop alkalmazást.
2. Menj a `Containers` vagy `Containers / Apps` részre.
3. Keresd meg az alkalmazás konténereit vagy a compose appot.
4. Kattints a `Stop` gombra.

Ha az adatbázis adatait is törölni akarod, azt terminálból tudod a legegyszerűbben megtenni:

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
