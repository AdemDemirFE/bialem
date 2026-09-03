# Bialem VPS Paketi

Bu klasör projenin tüm çalışır taraflarını barındırır. Sunucuya bu klasörün **tamamını** kopyalayıp aşağıdaki adımlarla ayağa kaldırırsınız. Build'ler **önceden yapılmıştır**; sunucuda derleme gerekmez.

```
vps/
├── backend/
│   └── bialem-backend.jar     # Spring Boot prod JAR (Java 17 uyumlu, arkadan bağımlılıklı)
├── frontend/
│   ├── dist/                  # Vite + React Native Web production build (nginx ile servis)
│   └── nginx.conf
├── admin/
│   └── admin/server.js        # Next.js standalone (node server) + .next + node_modules
├── docker-compose.yml         # bialem-db + bialem-backend + bialem-frontend + bialem-admin
├── nginx/
│   └── bialem.conf            # Host nginx (domain + SSL öncesi)
├── systemd/                   # (opsiyonel) bare-metal alternatifi
├── scripts/                   # deploy / status / logs / restart / stop / update / backup / restore
├── .env.prod.example
└── .env.prod                  # (oluşturuldu, gizlidir - git'e girmez)
```

## 1. Sunucuya yükleme

```bash
# örn. /opt/bialem/vps  (sudo mkdir -p /opt/bialem && sudo chown $USER /opt/bialem)
scp -r vps user@sunucu:/opt/bialem/
ssh user@sunucu
cd /opt/bialem/vps
```

## 2. Ortam değişkenleri

`.env.prod` pakette oluşturulmuş halde gelir (üretilmiş rastgele DB/JWT anahtarları). Domain, SMTP, OpenAI, Firebase değerlerini ihtiyacınıza göre düzenleyin:

```bash
nano .env.prod
```

Önemli değerler:

| Değişken | Açıklama |
|---|---|
| `POSTGRES_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` | DB şifresi (aynı olmalı) |
| `JWT_SECRET` / `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | Aynı değer |
| `BACKEND_BIND` / `BACKEND_PORT` | API host bağlantısı (varsayılan `127.0.0.1:8080`) |
| `NEXT_PUBLIC_API_BASE_URL` | Admin panelin kullandığı API |
| `FIREBASE_CREDENTIALS` | FCM JSON yolunu sunucuda `secrets/` altına koyun |

## 3. Deploy

```bash
bash scripts/deploy.sh        # compose up -d + sağlık kontrolü
bash scripts/status.sh
bash scripts/logs.sh backend  # backend|frontend|admin|db
```

Servisler ve adresler:

| Servis | Görünür adres |
|---|---|
| Frontend (web) | `http://127.0.0.1:4174` |
| Backend (API) | `http://127.0.0.1:8080` (container 8080) |
| Admin | `http://127.0.0.1:3000` |
| PostgreSQL | `bialem-db:5432` (host portu yok) |

> API'yi domain'siz dışa açmak isterseniz `.env.prod` içinde `BACKEND_BIND=0.0.0.0` yapın → `http://<sunucu-ip>:8080`.

## 4. Domain + SSL (host nginx)

```bash
sudo cp nginx/bialem.conf /etc/nginx/sites-available/bialem.conf
sudo ln -sf /etc/nginx/sites-available/bialem.conf /etc/nginx/sites-enabled/bialem.conf
sudo nginx -t && sudo systemctl reload nginx

sudo certbot --nginx -d bialem.app -d www.bialem.app -d api.bialem.app
```

## 5. Güncelleme (yeni paket)

Yeni `bialem-backend.jar`, `frontend/dist/` veya `admin/` dosyalarını bu klasöre kopyalayın, sonra:

```bash
bash scripts/update.sh        # backend+frontend+admin yeniden oluşturulur, DB volume korunur
```

## 6. Yedek / geri yükleme

```bash
bash scripts/backup-db.sh                                  # backups/bialem-<tarih>.sql.gz
bash scripts/restore-db.sh backups/bialem-20260101-120000.sql.gz
```

## 7. (Opsiyonel) bare-metal systemd alternatifi

Docker-compose yerine jar'ı doğrudan `systemd` ile çalıştırmak isterseniz:

```bash
# DB çalışsın (docker):  docker compose --env-file .env.prod up -d db
sudo cp systemd/bialem-backend.service systemd/bialem-admin.service /etc/systemd/system/
sudo cp systemd/backend.env.example /opt/bialem/vps/backend/backend.env   # şifreleri doldurun
sudo systemctl daemon-reload && sudo systemctl enable --now bialem-backend bialem-admin
```

## Güvenlik notları

- `.env.prod` ve `secrets/` sunucuda **bile** paylaşılmaz; repo'ya girmez.
- Servisler varsayılan olarak yalnızca `127.0.0.1`'e bağlanır; dışa açılan tek yol nginx (SSL) veya bilinçli `BACKEND_BIND=0.0.0.0` değişikliğidir.
- Firewall: 80/443 (nginx) ve ihtiyaç halinde dışarı açılan portu açın.

## Üretim giriş bilgileri

- Admin: `admin` / JHipster varsayılan `admin` (ilk kurulumdan sonra değiştirin).
- Uygulama: `mobile/` web sürümü üzerinden kayıt+giriş.