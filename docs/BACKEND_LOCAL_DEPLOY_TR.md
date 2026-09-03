# Bialem Backend'i Lokalden Derleyip Sunucuya Atma

Bu rehber, backend kodunu kendi bilgisayarınızda (lokal) derleyip, hazır JAR veya Docker imajı olarak uzak sunucuya aktarma adımlarını anlatır. Sunucuda Maven veya kaynak kod olmadan sadece paketi çalıştırmak istediğinizde kullanın.

> Not: Normal geliştirme akışı için `git push` sonrası sunucuda `./deploy/scripts/update.sh` çalıştırmak yeterlidir. Bu rehber, sunucu üzerinde `git pull` yapmadan veya bağımsız bir paket ile deploy etmek istenen durumlar içindir.

---

## Gereksinimler

### Lokal makine

- Java 17 JDK
- Maven 3.9+ veya proje kökündeki `./mvnw` wrapper
- Docker (isteğe bağlı, imaj olarak aktaracaksanız)
- `scp`, `rsync` veya başka bir dosya aktarım aracı
- Sunucuya SSH erişimi

### Sunucu

- Docker ve Docker Compose plugin kurulu
- `.env.prod` hazır ve doğru şekilde doldurulmuş
- `deploy/docker-compose.prod.yml` mevcut
- `deploy/scripts/` altındaki yardımcı betikler mevcut

`.env.prod` örneği için `docs/SETUP_GUIDE_TR.md` ve proje kökündeki `.env.prod.example` dosyasına bakın.

---

## Yol 1: Lokalde JAR Derleyip Sunucuya Atmak (Önerilen)

Bu yöntemde sadece `bialem-0.0.1-SNAPSHOT.jar` dosyası aktarılır. Sunucuda mevcut `Dockerfile` ve `docker-compose.prod.yml` kullanılarak imaj oluşturulur.

### 1.1. Lokalde production JAR'ı derle

Proje kökünden:

```bash
cd backend
./mvnw -Pprod -DskipTests package
```

Windows'ta PowerShell veya CMD kullanıyorsanız:

```powershell
cd backend
.\mvnw.cmd -Pprod -DskipTests package
```

Derleme sonunda şu dosya oluşur:

```text
backend/target/bialem-0.0.1-SNAPSHOT.jar
```

> `-DskipTests` testleri atlar. Testleri çalıştırmak için `-DskipTests` kısmını kaldırabilirsiniz, ancak bu uzun sürer.

### 1.2. JAR'ı sunucuya kopyala

`rsync` ile (önerilen):

```bash
rsync -avz --progress \
  backend/target/bialem-0.0.1-SNAPSHOT.jar \
  kullanici@sunucu-ip:/opt/bialem/backend/target/
```

`scp` ile:

```bash
scp backend/target/bialem-0.0.1-SNAPSHOT.jar \
  kullanici@sunucu-ip:/opt/bialem/backend/target/
```

Sunucudaki hedef dizin projenin gerçek yoluna göre ayarlanmalıdır. Örneğin `/home/bialem/bialem/backend/target/`.

### 1.3. Sunucuda container'ları yeniden oluştur

Sunucuya SSH ile bağlanın:

```bash
ssh kullanici@sunucu-ip
```

Proje dizinine gidin:

```bash
cd /opt/bialem
```

Mevcut container'ları durdurun (veritabanı volume'ü korunur):

```bash
docker compose -p bialem --env-file .env.prod -f deploy/docker-compose.prod.yml down
```

Yeni JAR ile imajı yeniden oluşturup çalıştırın:

```bash
docker compose -p bialem --env-file .env.prod -f deploy/docker-compose.prod.yml up -d --build
```

Veya mevcut betikleri kullanın:

```bash
./deploy/scripts/health-check.sh
```

Durum kontrolü:

```bash
docker compose -p bialem --env-file .env.prod -f deploy/docker-compose.prod.yml ps
```

---

## Yol 2: Lokalde Docker İmajı Derleyip Sunucuya Aktarmak

Bu yöntem, sunucuda tekrar derleme yapmadan hazır imajı çalıştırmak içindir. Özellikle sunucu kaynakları kısıtlıysa veya Maven kurulu değilse tercih edilebilir.

### 2.1. Lokalde JAR derle

Yol 1'deki gibi:

```bash
cd backend
./mvnw -Pprod -DskipTests package
```

### 2.2. Lokalde Docker imajı oluştur

Proje kökünden:

```bash
docker build -t bialem-backend:local ./backend
```

### 2.3. İmajı arşivle

```bash
docker save bialem-backend:local | gzip > bialem-backend-local.tar.gz
```

### 2.4. Arşivi sunucuya kopyala

```bash
rsync -avz --progress bialem-backend-local.tar.gz \
  kullanici@sunucu-ip:/opt/bialem/
```

### 2.5. Sunucuda imajı yükle ve çalıştır

```bash
ssh kullanici@sunucu-ip
cd /opt/bialem
docker load < bialem-backend-local.tar.gz
```

`deploy/docker-compose.prod.yml` dosyasındaki backend servisinde imaj adını geçici olarak değiştirin:

```yaml
services:
  backend:
    image: bialem-backend:local
    # build: ... satırını geçici olarak yoruma alın
```

Container'ları yeniden başlatın:

```bash
docker compose -p bialem --env-file .env.prod -f deploy/docker-compose.prod.yml up -d
```

> Bu yöntemde `docker-compose.prod.yml` içindeki `build` kısmı yerine doğrudan `image` kullanılmalıdır. İşlem bitince eski haline döndürmeyi unutmayın.

---

## Geri Alma (Rollback)

### JAR yöntemi ile

Eski JAR'ı yedeklediyseniz sunucuya geri kopyalayın ve adım 1.3'ü tekrar uygulayın.

### Docker imaj yöntemi ile

Eski imaj hâlâ sunucuda varsa:

```bash
docker images | grep bialem-backend
```

Eski imaj tag'ini belirleyin ve `docker-compose.prod.yml`'deki `image` alanını eski tag ile değiştirip yeniden başlatın.

---

## Güvenlik Uyarıları

- `backend/target/bialem-0.0.1-SNAPSHOT.jar` dosyası içinde **hiçbir zaman** üretim gizli anahtarı (JWT secret, DB şifresi vb.) bulunmaz. Bu değerler sadece sunucudaki `.env.prod` dosyasından okunur.
- `.env.prod` dosyasını lokal makinenize kopyalamayın veya versiyon kontrolüne eklemeyin.
- Paket aktarımı için `rsync` veya `scp` kullanın; FTP gibi şifresiz protokoller kullanmayın.
- Sunucuda `BIALEM_ENV=prod` ve `SPRING_PROFILES_ACTIVE=prod` olduğundan emin olun.

---

## Sorun Giderme

### `Connection refused` veya `No route to host`

Sunucu güvenlik grubunda veya firewall'da backend portu (`8080`) açık olmalıdır.

### `docker compose` komutu bulunamadı

Docker Compose plugin kurulu olduğundan emin olun. Eski `docker-compose` binary'si varsa aynı komutu onunla deneyin.

### JAR derlenirken hata

- Java sürümünün 17 olduğunu doğrulayın: `java -version`
- Maven cache'i temizlemek için: `./mvnw clean -Pprod -DskipTests package`

### Container sağlık kontrolünden geçmiyor

```bash
docker logs -f bialem-backend
```

Logları inceleyin. Veritabanı bağlantısı veya `.env.prod` değerleri ile ilgili sorun olabilir.

---

## İlgili Dokümanlar

- `docs/SETUP_GUIDE_TR.md` — İlk kurulum ve `.env.prod` hazırlama
- `docs/OPERATIONS_RUNBOOK_TR.md` — Günlük operasyon komutları
- `deploy/scripts/build-backend-jar.sh` — Sunucuda JAR derleme betiği
- `deploy/scripts/update.sh` — Git tabanlı güncelleme akışı
