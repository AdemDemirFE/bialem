-- ============================================================================
-- Bialem DEV seed verisi (2026-09)
-- ----------------------------------------------------------------------------
-- Hedef:  localhost:15432/bialem  (container: bialem-postgresql-1)
-- Kullanim (Windows):
--   Get-Content scripts/seed-dev.sql -Raw -Encoding UTF8 |
--     docker exec -i bialem-postgresql-1 psql -U bialem -d bialem -v ON_ERROR_STOP=1
--
-- Notlar:
--   * Tablolarda id otomatik (sequence default) degildir; ID'ler acikca verilir.
--   * Son adimda sequence_generator, kullanilan en yuksek ID'nin uzerine cekilir.
--   * Yalnizca BOS tablolara yazar; dolu alanlari (store_katalog, notification_template)
--     tekrar yazmaz.
-- ============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1) ETKINLIKLER (event)
-- ---------------------------------------------------------------------------
INSERT INTO event (id, title, description, starts_at, ends_at, location_name, address_text,
                   latitude, longitude, cover_image_url, capacity, status, rejection_reason,
                   published_at, published_to_discovery, group_moderation_status,
                   platform_moderation_status, cancelled_at, cancellation_reason,
                   created_at, updated_at, community_id, category_id, created_by_id, cancelled_by_id)
VALUES
  -- Gecmis tamamlanmis etkinlikler
  (900001, 'Ağustos Buluşması: Kamp Ateşi Gecesi',
   'Hafta sonu Berç Yaylasında buluşuyoruz. Çadır, uyku tulumu ve keyif getirmeyi unutmayın. Yakacak ve çay bizden.',
   '2026-08-15 17:00:00', '2026-08-16 10:00:00', 'Berç Yaylası', 'Berç Yaylası, Ankara',
   40.6834, 32.2920, 'https://placehold.co/800x400?text=Kamp+Ateşi', 40, 'COMPLETED', NULL,
   '2026-07-20 09:00:00', true, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-07-15 10:00:00', '2026-08-16 10:30:00', 8140000016, 8140000016, 8110000008, NULL),

  (900002, 'Masa Oyunları Akşamı: Catan Turnuvası',
   'Kafe iş birliğiyle düzenlenen Catan turnuvası. Katılım ücretsiz, kazanan hediye kazanacak.',
   '2026-08-22 19:00:00', '2026-08-22 23:00:00', 'Oyun Atölyesi Kafe', 'Kızılay, Ankara',
   39.9208, 32.8541, 'https://placehold.co/800x400?text=Catan', 24, 'COMPLETED', NULL,
   '2026-08-01 12:00:00', true, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-07-28 15:00:00', '2026-08-22 23:30:00', 8140000003, 8140000003, 8110000008, NULL),

  -- Yayinda / gelecek etkinlikler
  (900003, 'Doğa Yürüyüşü: Çubuk Barajı Rotası',
   'Orta zorlukta 12 km parkur. Yanınıza en az 2 litre su ve atıştırmalık alın. Rehber eşliğinde.',
   '2026-09-12 08:00:00', '2026-09-12 16:00:00', 'Çubuk Barajı', 'Çubuk, Ankara',
   40.4000, 33.0333, 'https://placehold.co/800x400?text=Doğa+Yürüyüşü', 25, 'PUBLISHED', NULL,
   '2026-09-01 09:00:00', true, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-08-25 11:00:00', '2026-09-03 14:00:00', 8140000001, 8140000016, 8110000008, NULL),

  (900004, 'Gastronomi Atölyesi: Ekmek ve Hamur İşleri',
   'Usta aşçı ile geleneksel ekmek yapımı. Katılımcı limiti 12 kişidir, erken rezervasyon önerilir.',
   '2026-09-20 14:00:00', '2026-09-20 18:00:00', 'Gastronomi Atölyesi', 'Çankaya, Ankara',
   39.9000, 32.8500, 'https://placehold.co/800x400?text=Ekmek+Atölyesi', 12, 'PUBLISHED', NULL,
   '2026-09-02 10:00:00', true, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-08-30 16:00:00', '2026-09-03 14:30:00', 8140000013, 8140000013, 8110000006, NULL),

  (900005, 'Kitap Kulübü: Sabahattin Ali Okumaları',
   'Bu ay "Kürk Mantolu Madonna" üzerine konuşuyoruz. Yeni katılımcılara açıktır.',
   '2026-09-25 19:30:00', '2026-09-25 21:30:00', 'Kitapçı Kafe', 'Bahçelievler, Ankara',
   39.9167, 32.8333, 'https://placehold.co/800x400?text=Kitap+Kulübü', 15, 'PUBLISHED', NULL,
   '2026-09-01 09:30:00', false, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-08-28 13:00:00', '2026-09-03 15:00:00', 8140000014, 8140000019, 8110000003, NULL),

  (900006, 'Konser: Akustik Caz Gecesi',
   'Yerel sanatçılarla akustik caz dinletisi. Giriş ücretsiz, kafeden sipariş beklenir.',
   '2026-09-18 20:00:00', '2026-09-18 23:00:00', 'Jazz Bar', 'Tunalı Hilmi, Ankara',
   39.9100, 32.8600, 'https://placehold.co/800x400?text=Caz+Gecesi', 80, 'PUBLISHED', NULL,
   '2026-09-02 11:00:00', true, 'APPROVED', 'APPROVED', NULL, NULL,
   '2026-08-26 18:00:00', '2026-09-03 16:00:00', 8140000027, 8140000019, 8110000001, NULL),

  -- Onay bekleyen / taslak
  (900007, 'Fotoğraf Yürüyüşü: Ankara Eski Kent',
   'Şehir fotoğrafçılığı üzerine temel ipuçlarıyla başlayıp Ulus-Ankara Kalesi rotasında uygulamalı gezi.',
   '2026-10-03 09:00:00', '2026-10-03 14:00:00', 'Ulus Meydanı', 'Ulus, Ankara',
   39.9410, 32.8560, 'https://placehold.co/800x400?text=Fotoğraf', 18, 'PENDING_APPROVAL', NULL,
   NULL, false, 'PENDING', 'PENDING', NULL, NULL,
   '2026-09-03 10:00:00', '2026-09-03 10:00:00', 8140000005, 8140000019, 8110000002, NULL),

  (900008, 'Kış Hazırlığı: Ekipman Bakım Atölyesi',
   'Taslak etkinlik: kış kampı öncesi çadır ve uyku tulumu bakımı.',
   '2026-10-18 13:00:00', NULL, 'OutdoorX Mağazası', 'Çankaya, Ankara',
   NULL, NULL, NULL, 20, 'DRAFT', NULL, NULL, false, 'NOT_REQUIRED', 'NOT_REQUIRED', NULL, NULL,
   '2026-09-03 12:00:00', '2026-09-03 12:00:00', 8140000001, 8140000016, 8110000008, NULL),

  -- Iptal edilmis ornek
  (900009, 'Yıldız Gözlemi: Elmadağ',
   'Hava koşulları nedeniyle iptal edildi.',
   '2026-09-05 20:00:00', '2026-09-05 23:59:00', 'Elmadağ Yaylası', 'Elmadağ, Ankara',
   39.9167, 33.2167, 'https://placehold.co/800x400?text=Yıldız', 30, 'CANCELLED', NULL,
   '2026-08-15 09:00:00', true, 'APPROVED', 'APPROVED',
   '2026-09-02 20:30:00', 'Olumsuz hava tahmini nedeniyle iptal edildi.',
   '2026-08-01 10:00:00', '2026-09-02 20:31:00', 8140000016, 8140000016, 8110000008, 8110000008);

-- ---------------------------------------------------------------------------
-- 2) ETKINLIK KATILIMCILARI (event_participant)
-- ---------------------------------------------------------------------------
INSERT INTO event_participant (id, status, note, created_at, updated_at, event_id, user_id)
VALUES
  -- Kamp Ateşi (900001) - tamamlandi
  (901001, 'CHECKED_IN', NULL, '2026-07-16 09:00:00', '2026-08-15 17:30:00', 900001, 8110000008),
  (901002, 'CHECKED_IN', 'Çadırımla geliyorum', '2026-07-16 10:00:00', '2026-08-15 17:45:00', 900001, 8110000001),
  (901003, 'CHECKED_IN', NULL, '2026-07-16 11:00:00', '2026-08-15 18:00:00', 900001, 8110000003),
  (901004, 'NO_SHOW', NULL, '2026-07-17 09:00:00', '2026-08-16 10:30:00', 900001, 8110000006),
  (901005, 'APPROVED', NULL, '2026-07-17 10:00:00', '2026-07-20 09:00:00', 900001, 8110000002),
  (901006, 'CANCELLED', 'İş çıktı', '2026-07-18 09:00:00', '2026-08-10 19:00:00', 900001, 8110000007),

  -- Catan (900002) - tamamlandi
  (901007, 'CHECKED_IN', NULL, '2026-08-02 10:00:00', '2026-08-22 19:05:00', 900002, 8110000008),
  (901008, 'CHECKED_IN', NULL, '2026-08-02 11:00:00', '2026-08-22 19:10:00', 900002, 8110000003),
  (901009, 'CHECKED_IN', NULL, '2026-08-03 12:00:00', '2026-08-22 19:15:00', 900002, 8110000001),
  (901010, 'NO_SHOW', NULL, '2026-08-03 13:00:00', '2026-08-22 23:30:00', 900002, 8110000006),

  -- Doğa Yürüyüşü (900003) - yayinda
  (901011, 'APPROVED', 'İlk yürüyüşüm, heyecanlıyım', '2026-09-01 09:30:00', '2026-09-01 10:00:00', 900003, 8110000001),
  (901012, 'APPROVED', NULL, '2026-09-01 10:00:00', '2026-09-01 10:10:00', 900003, 8110000003),
  (901013, 'APPROVED', NULL, '2026-09-01 11:00:00', '2026-09-01 11:15:00', 900003, 8110000006),
  (901014, 'APPROVED', NULL, '2026-09-02 09:00:00', '2026-09-02 09:20:00', 900003, 8110000002),
  (901015, 'PENDING', NULL, '2026-09-03 08:00:00', '2026-09-03 08:00:00', 900003, 8110000007),
  (901016, 'PENDING', NULL, '2026-09-03 09:00:00', '2026-09-03 09:00:00', 900003, 8110000005),

  -- Gastronomi (900004)
  (901017, 'APPROVED', NULL, '2026-09-02 11:00:00', '2026-09-02 11:00:00', 900004, 8110000008),
  (901018, 'APPROVED', NULL, '2026-09-02 12:00:00', '2026-09-02 12:00:00', 900004, 8110000001),
  (901019, 'WAITLISTED', NULL, '2026-09-02 13:00:00', '2026-09-02 13:00:00', 900004, 8110000003),

  -- Caz Gecesi (900006)
  (901020, 'APPROVED', NULL, '2026-09-02 12:00:00', '2026-09-02 12:00:00', 900006, 8110000002),
  (901021, 'APPROVED', NULL, '2026-09-03 09:00:00', '2026-09-03 09:00:00', 900006, 8110000008);

-- ---------------------------------------------------------------------------
-- 3) ETKINLIK MESAJLARI (event_message)
-- ---------------------------------------------------------------------------
INSERT INTO event_message (id, body, moderation_status, created_at, updated_at, event_id, author_id)
VALUES
  (902001, 'Herkes malzemesini kontrol etsin, gece serin olacak.', 'VISIBLE', '2026-08-14 20:00:00', '2026-08-14 20:00:00', 900001, 8110000008),
  (902002, 'Çay ve çikolata ben getiriyorum :)', 'VISIBLE', '2026-08-14 20:30:00', '2026-08-14 20:30:00', 900001, 8110000001),
  (902003, 'Buluşma noktası yayla girişi, aracı olanlar müsaitse yazsın.', 'VISIBLE', '2026-08-14 21:00:00', '2026-08-14 21:00:00', 900001, 8110000003),
  (902004, 'Turnuva başlangıcı 19:30, gruplar orada açıklanacak.', 'VISIBLE', '2026-08-22 15:00:00', '2026-08-22 15:00:00', 900002, 8110000008),
  (902005, 'Yürüyüş için sabah 07:45''te istasyonda buluşalım.', 'VISIBLE', '2026-09-11 18:00:00', '2026-09-11 18:00:00', 900003, 8110000008),
  (902006, 'Rezervasyon için son gün cumartesi, yer sınırlı!', 'VISIBLE', '2026-09-02 15:00:00', '2026-09-02 15:00:00', 900004, 8110000006);

-- ---------------------------------------------------------------------------
-- 4) ETKINLIK DEGERLENDIRMELERI (event_rating)
-- ---------------------------------------------------------------------------
INSERT INTO event_rating (id, rating, review_text, created_at, updated_at, event_id, user_id)
VALUES
  (903001, 5, 'Muhteşem bir geceydi, kesinlikle tekrarı gelmeli.', '2026-08-17 09:00:00', '2026-08-17 09:00:00', 900001, 8110000001),
  (903002, 4, 'Organizasyon iyiydi ama ulaşım biraz zorluydu.', '2026-08-17 10:00:00', '2026-08-17 10:00:00', 900001, 8110000003),
  (903003, 5, 'Çok eğlendik, kazanan arkadaşımızı tebrik ederim!', '2026-08-23 10:00:00', '2026-08-23 10:00:00', 900002, 8110000008),
  (903004, 5, 'Hakikaten keyifli bir turnuvaydı.', '2026-08-23 11:00:00', '2026-08-23 11:00:00', 900002, 8110000003);

-- ---------------------------------------------------------------------------
-- 5) ETKINLIK BILETLERI (event_ticket) + ticket_order + order_item + payment + ticket
-- ---------------------------------------------------------------------------
INSERT INTO event_ticket (id, name, description, price, currency, quantity, sold_quantity,
                          sale_start_date, sale_end_date, status, event_id)
VALUES
  (904001, 'Standart Katılım', 'Kamp alanı ve aktiviteler dahildir.', 150.00, 'TRY', 40, 38,
   '2026-07-20 09:00:00', '2026-08-14 23:59:00', 'SOLD_OUT', 900001),
  (904002, 'Erken Kuş', 'İlk 10 katılımcıya özel indirimli bilet.', 100.00, 'TRY', 10, 10,
   '2026-07-20 09:00:00', '2026-07-25 23:59:00', 'SOLD_OUT', 900001),
  (904003, 'Genel Giriş', 'Turnuva katılım bileti.', 75.00, 'TRY', 24, 22,
   '2026-08-01 12:00:00', '2026-08-20 23:59:00', 'ACTIVE', 900002),
  (904004, 'Standart', 'Atölye katılımı, malzemeler dahildir.', 250.00, 'TRY', 12, 7,
   '2026-09-02 10:00:00', '2026-09-19 23:59:00', 'ACTIVE', 900004),
  (904005, 'Giriş', 'Konser giriş bileti.', 0.00, 'TRY', 80, 30,
   '2026-09-02 11:00:00', '2026-09-18 19:00:00', 'ACTIVE', 900006);

-- ticket_order (basari + beklemede + iptal ornekleri)
INSERT INTO ticket_order (id, order_number, total_amount, currency, status, created_at, paid_at, user_id)
VALUES
  (905001, 'TKT-2026-0810-ABCD1', 150.00, 'TRY', 'PAID', '2026-08-10 10:00:00', '2026-08-10 10:05:00', 8110000001),
  (905002, 'TKT-2026-0810-ABCD2', 150.00, 'TRY', 'PAID', '2026-08-10 11:00:00', '2026-08-10 11:07:00', 8110000003),
  (905003, 'TKT-2026-0811-ABCD3', 250.00, 'TRY', 'PAID', '2026-09-02 12:30:00', '2026-09-02 12:35:00', 8110000008),
  (905004, 'TKT-2026-0812-ABCD4', 250.00, 'TRY', 'PENDING', '2026-09-03 09:00:00', NULL, 8110000001);

INSERT INTO order_item (id, quantity, unit_price, total_price, order_id, ticket_id)
VALUES
  (906001, 1, 150.00, 150.00, 905001, 904001),
  (906002, 1, 150.00, 150.00, 905002, 904001),
  (906003, 1, 250.00, 250.00, 905003, 904004),
  (906004, 2, 250.00, 500.00, 905004, 904004);

INSERT INTO payment (id, provider, provider_transaction_id, amount, currency, status, payment_date,
                     failure_reason, provider_response, idempotency_key, callback_payload, order_id)
VALUES
  (907001, 'IYZICO', 'TXN-0810-1001', 150.00, 'TRY', 'COMPLETED', '2026-08-10 10:05:00', NULL,
   '{"status":"success"}', 'tkt-905001-iyzico-1001', NULL, 905001),
  (907002, 'IYZICO', 'TXN-0810-1002', 150.00, 'TRY', 'COMPLETED', '2026-08-10 11:07:00', NULL,
   '{"status":"success"}', 'tkt-905002-iyzico-1002', NULL, 905002),
  (907003, 'IYZICO', 'TXN-0902-1003', 250.00, 'TRY', 'COMPLETED', '2026-09-02 12:35:00', NULL,
   '{"status":"success"}', 'tkt-905003-iyzico-1003', NULL, 905003),
  (907004, 'IYZICO', NULL, 500.00, 'TRY', 'PENDING', NULL, NULL,
   NULL, 'tkt-905004-iyzico-1004', NULL, 905004);

-- ticket (uretilmis biletler)
INSERT INTO ticket (id, ticket_code, qr_code, status, used_at, order_item_id, user_id, event_id)
VALUES
  (908001, 'TKT-001-20260815', 'QR-001-20260815', 'USED', '2026-08-15 17:00:00', 906001, 8110000001, 900001),
  (908002, 'TKT-002-20260815', 'QR-002-20260815', 'USED', '2026-08-15 17:00:00', 906002, 8110000003, 900001),
  (908003, 'TKT-003-20260920', 'QR-003-20260920', 'ACTIVE', NULL, 906003, 8110000008, 900004);

-- ---------------------------------------------------------------------------
-- 6) GONDERILER (post) + post_media + comment
-- ---------------------------------------------------------------------------
INSERT INTO post (id, body, visibility, moderation_status, created_at, updated_at, community_id, event_id, author_id)
VALUES
  (910001, 'Herkese merhaba! Kamp ateşi gecesinden kareler. Bir sonrakinde mutlaka gelin! 🏕️',
   'PUBLIC', 'VISIBLE', '2026-08-16 11:00:00', '2026-08-16 11:00:00', 8140000016, 900001, 8110000008),
  (910002, 'Catan turnuvası sonuçları: Şampiyonumuz Emre! Tebrikler 🏆',
   'COMMUNITY_ONLY', 'VISIBLE', '2026-08-22 23:30:00', '2026-08-22 23:30:00', 8140000003, 900002, 8110000008),
  (910003, '12 Eylül yürüyüşü kontenjanı dolmak üzere, katılım için erken davranın.',
   'PUBLIC', 'VISIBLE', '2026-09-02 09:00:00', '2026-09-02 09:00:00', 8140000001, 900003, 8110000008),
  (910004, 'Ekmek atölyesinden güzel haber: Hamur işi ustamız katılımcılara özel tarif defteri hediye edecek!',
   'COMMUNITY_ONLY', 'VISIBLE', '2026-09-02 14:00:00', '2026-09-02 14:00:00', 8140000013, 900004, 8110000006),
  (910005, 'Bu ayki kitabımız Kürk Mantolu Madonna. Okumayanlar için acele edin, toplantı 25 Eylül''de.',
   'PUBLIC', 'VISIBLE', '2026-09-01 09:00:00', '2026-09-01 09:00:00', 8140000014, 900005, 8110000003),
  (910006, 'Konsere davetlisiniz! Akustik caz gecesinde yerler sınırlı.',
   'PUBLIC', 'VISIBLE', '2026-09-03 10:00:00', '2026-09-03 10:00:00', 8140000027, 900006, 8110000001),
  (910007, 'Yeni üyelerimizle tanışma etkinliği planlıyoruz, fikirlerinizi bekliyoruz.',
   'COMMUNITY_ONLY', 'VISIBLE', '2026-09-03 12:00:00', '2026-09-03 12:00:00', 8140000016, NULL, 8110000002);

INSERT INTO post_media (id, media_type, storage_path, sort_order, created_at, post_id)
VALUES
  (911001, 'IMAGE', 'posts/2026/08/16/kamp-atesi-1.jpg', 0, '2026-08-16 11:00:00', 910001),
  (911002, 'IMAGE', 'posts/2026/08/16/kamp-atesi-2.jpg', 1, '2026-08-16 11:00:00', 910001),
  (911003, 'IMAGE', 'posts/2026/08/22/catan-turnuvasi.jpg', 0, '2026-08-22 23:30:00', 910002);

INSERT INTO comment (id, target_type, target_id, body, moderation_status, created_at, updated_at, author_id)
VALUES
  (912001, 'POST', '910001', 'Harika görünüyor, bir dahakine mutlaka katılacağım!', 'VISIBLE',
   '2026-08-16 12:00:00', '2026-08-16 12:00:00', 8110000001),
  (912002, 'POST', '910001', 'Fotoğraflar çok güzel olmuş 📸', 'VISIBLE',
   '2026-08-16 13:00:00', '2026-08-16 13:00:00', 8110000003),
  (912003, 'POST', '910002', 'Emeğine sağlık, çok keyifliydi!', 'VISIBLE',
   '2026-08-23 09:00:00', '2026-08-23 09:00:00', 8110000006),
  (912004, 'EVENT', '900003', 'Yürüyüş rotası haritada paylaşılabilir mi?', 'VISIBLE',
   '2026-09-02 16:00:00', '2026-09-02 16:00:00', 8110000007),
  (912005, 'EVENT', '900004', 'Hamur işlerine alerjisi olanlar için alternatif var mı?', 'VISIBLE',
   '2026-09-03 09:30:00', '2026-09-03 09:30:00', 8110000005);

-- ---------------------------------------------------------------------------
-- 7) HIKAYELER (story_group, story, story_view, story_reaction, hashtag, story_hashtag, story_element)
-- ---------------------------------------------------------------------------
INSERT INTO story_group (id, location_name, latitude, longitude, created_at, expires_at, author_id, community_id, event_id)
VALUES
  (913001, 'Berç Yaylası', 40.6834, 32.2920, '2026-08-15 17:00:00', '2026-08-16 23:59:00', 8110000008, 8140000016, 900001),
  (913002, 'Ankara', 39.9208, 32.8541, '2026-08-22 19:00:00', '2026-08-23 23:59:00', 8110000008, 8140000003, 900002);

INSERT INTO story (id, content_type, body, media_url, is_public, share_with_followers, created_at, expires_at,
                   author_id, story_group_id, event_id, location_name, latitude, longitude)
VALUES
  (914001, 'IMAGE', NULL, 'stories/2026/08/15/kamp-manzara.jpg', true, true,
   '2026-08-15 18:00:00', '2026-08-16 23:59:00', 8110000008, 913001, 900001, 'Berç Yaylası', 40.6834, 32.2920),
  (914002, 'TEXT', 'Gün batımı buradan harika! 🌅', NULL, true, true,
   '2026-08-15 18:30:00', '2026-08-16 23:59:00', 8110000008, 913001, 900001, 'Berç Yaylası', 40.6834, 32.2920),
  (914003, 'IMAGE', NULL, 'stories/2026/08/22/catan-masa.jpg', true, true,
   '2026-08-22 19:30:00', '2026-08-23 23:59:00', 8110000008, 913002, 900002, 'Oyun Atölyesi Kafe', 39.9208, 32.8541),
  (914004, 'TEXT', 'Turnuva başladı, herkese başarılar! 🎲', NULL, true, true,
   '2026-08-22 19:35:00', '2026-08-23 23:59:00', 8110000008, 913002, 900002, 'Oyun Atölyesi Kafe', 39.9208, 32.8541);

INSERT INTO story_view (id, viewed_at, story_id, viewer_id)
VALUES
  (915001, '2026-08-15 18:05:00', 914001, 8110000001),
  (915002, '2026-08-15 18:07:00', 914001, 8110000003),
  (915003, '2026-08-15 18:10:00', 914002, 8110000001),
  (915004, '2026-08-22 19:40:00', 914003, 8110000001),
  (915005, '2026-08-22 19:42:00', 914003, 8110000006),
  (915006, '2026-08-22 19:45:00', 914004, 8110000003);

INSERT INTO story_reaction (id, reaction_type, created_at, story_id, user_id)
VALUES
  (916001, 'HEART', '2026-08-15 18:06:00', 914001, 8110000001),
  (916002, 'FIRE', '2026-08-15 18:08:00', 914001, 8110000003),
  (916003, 'LAUGH', '2026-08-22 19:41:00', 914003, 8110000001),
  (916004, 'CLAP', '2026-08-22 19:50:00', 914003, 8110000006);

INSERT INTO hashtag (id, name, normalized_name, usage_count, created_at, updated_at, is_active)
VALUES
  (917001, '#Kamp', 'kamp', 12, '2026-07-01 10:00:00', '2026-09-03 10:00:00', true),
  (917002, '#Doğa', 'doga', 9, '2026-07-01 10:00:00', '2026-09-03 10:00:00', true),
  (917003, '#MasaOyunları', 'masaoyunlari', 7, '2026-07-01 10:00:00', '2026-09-03 10:00:00', true),
  (917004, '#Ankara', 'ankara', 24, '2026-07-01 10:00:00', '2026-09-03 10:00:00', true),
  (917005, '#Gastronomi', 'gastronomi', 5, '2026-07-01 10:00:00', '2026-09-03 10:00:00', true);

INSERT INTO story_hashtag (id, created_at, story_id, hashtag_id)
VALUES
  (918001, '2026-08-15 18:00:00', 914001, 917001),
  (918002, '2026-08-15 18:00:00', 914001, 917002),
  (918003, '2026-08-22 19:30:00', 914003, 917003),
  (918004, '2026-08-22 19:30:00', 914003, 917004);

INSERT INTO story_element (id, element_type, content, position_x, position_y, scale, rotation, color,
                           background_color, font_size, width, height, metadata_json, sort_order, created_at, story_id)
VALUES
  (919001, 'TEXT', 'Bugün harika bir gün!', 50, 120, 1.0, 0, '#FFFFFF', 'rgba(0,0,0,0.4)', 28, 300, 80, NULL, 0,
   '2026-08-15 18:00:00', 914001),
  (919002, 'STICKER', '🔥', 300, 200, 1.2, 0.1, NULL, NULL, NULL, 80, 80, NULL, 1,
   '2026-08-15 18:00:00', 914001);

-- ---------------------------------------------------------------------------
-- 8) TAKIP (follow) + follow_request + user_review
-- ---------------------------------------------------------------------------
INSERT INTO follow (id, created_at, follower_id, followed_id)
VALUES
  (920001, '2026-07-10 10:00:00', 8110000001, 8110000008),
  (920002, '2026-07-11 10:00:00', 8110000003, 8110000008),
  (920003, '2026-07-12 10:00:00', 8110000008, 8110000001),
  (920004, '2026-07-13 10:00:00', 8110000006, 8110000008),
  (920005, '2026-07-14 10:00:00', 8110000002, 8110000008),
  (920006, '2026-08-01 10:00:00', 8110000008, 8110000003),
  (920007, '2026-08-02 10:00:00', 8110000001, 8110000003),
  (920008, '2026-08-03 10:00:00', 8110000003, 8110000001);

INSERT INTO follow_request (id, created_at, requester_id, target_user_id)
VALUES
  (921001, '2026-09-01 10:00:00', 8110000005, 8110000008),
  (921002, '2026-09-02 10:00:00', 8110000007, 8110000001);

INSERT INTO user_review (id, rating, review_text, created_at, updated_at, reviewer_id, reviewed_user_id, event_id)
VALUES
  (922001, 5, 'Harika bir ev sahibi, organizasyonu çok iyiydi.', '2026-08-17 12:00:00', '2026-08-17 12:00:00', 8110000001, 8110000008, 900001),
  (922002, 5, 'Kamp sırasında çok yardımcı oldu, teşekkürler!', '2026-08-17 13:00:00', '2026-08-17 13:00:00', 8110000003, 8110000008, 900001),
  (922003, 4, 'Güvenilir katılımcı, zamanında geldi.', '2026-08-18 09:00:00', '2026-08-18 09:00:00', 8110000008, 8110000001, 900001);

-- ---------------------------------------------------------------------------
-- 9) RAPORLAR (report)
-- ---------------------------------------------------------------------------
INSERT INTO report (id, target_type, target_id, reason, details, status, resolved_at, created_at, updated_at, reporter_id, resolved_by_id)
VALUES
  (923001, 'COMMENT', '912004', 'Yanıltıcı bilgi', 'Rota bilgisi güncel değil.', 'DISMISSED', '2026-09-02 18:00:00',
   '2026-09-02 17:00:00', '2026-09-02 18:00:00', 8110000008, 8110000008),
  (923002, 'POST', '910001', 'Reklam içeriği', 'Spam olabilir.', 'OPEN', NULL,
   '2026-09-03 13:00:00', '2026-09-03 13:00:00', 8110000007, NULL);

-- ---------------------------------------------------------------------------
-- 10) BILDIRIMLER (notification)
-- ---------------------------------------------------------------------------
INSERT INTO notification (id, type, title, body, payload, is_read, created_at, user_id)
VALUES
  (924001, 'EVENT_REMINDER', 'Doğa Yürüyüşü yaklaşıyor!', 'Çubuk Barajı rotası için yarın 08:00''de buluşuyoruz.',
   '{"eventId":900003,"type":"REMINDER"}', false, '2026-09-11 08:00:00', 8110000001),
  (924002, 'FOLLOW', 'Yeni takipçin var', 'can seni takip etmeye başladı.',
   '{"profileId":8110000008}', true, '2026-08-03 10:00:00', 8110000003),
  (924003, 'EVENT_UPDATE', 'Etkinlik güncellendi', 'Konser bilgileri güncellendi.',
   '{"eventId":900006}', false, '2026-09-03 11:00:00', 8110000002),
  (924004, 'COMMUNITY', 'Kültür & Sanat grubuna hoş geldin', 'Topluluk kurallarını okumayı unutma.',
   '{"communityId":8140000019}', true, '2026-09-01 09:00:00', 8110000005);

-- ---------------------------------------------------------------------------
-- 11) SEHIR ETKINLIKLERI (city_event + interest + ticket_offer)
-- ---------------------------------------------------------------------------
INSERT INTO city_event (id, title, description, category, city, venue_name, address_text, starts_at, ends_at,
                        cover_image_url, price_label, source_name, source_url, ticket_url, status, provider_code,
                        external_id, last_synced_at, raw_payload, created_at, updated_at)
VALUES
  (925001, 'Ankara Tiyatro Festivali', 'Yerli ve yabancı tiyatro topluluklarının katılımıyla düzenlenen festival.',
   'tiyatro', 'Ankara', 'Devlet Tiyatrosu', 'Kızılay, Ankara', '2026-10-01 19:00:00', '2026-10-10 23:00:00',
   'https://placehold.co/800x400?text=Tiyatro+Festivali', '50-150 TL', 'biletix', 'https://example.com/tiyatro-festivali',
   'https://example.com/tiyatro-festivali/bilet', 'PUBLISHED', 'BILETIX', 'EVT-ANK-001', '2026-09-03 06:00:00',
   '{"venue":"Devlet Tiyatrosu"}', '2026-09-01 06:00:00', '2026-09-03 06:00:00'),
  (925002, 'Başkent Kitap Fuarı', 'Yüzlerce yayınevi ve imza günleriyle Ankara Kitap Fuarı.',
   'kitap', 'Ankara', 'ATO Congresium', 'Söğütözü, Ankara', '2026-09-25 10:00:00', '2026-10-04 20:00:00',
   'https://placehold.co/800x400?text=Kitap+Fuarı', 'Ücretsiz', 'etkinlikankara', 'https://example.com/kitap-fuari',
   NULL, 'PUBLISHED', 'ETKINLIK_ANKARA', 'EVT-ANK-002', '2026-09-03 06:00:00',
   '{"organizer":"ATO"}', '2026-09-01 06:30:00', '2026-09-03 06:00:00'),
  (925003, 'Caz Festivali İstanbul Ayağı', 'Uluslararası sanatçılarla caz festivali.',
   'muzik', 'İstanbul', 'BJK Süleyman Seba', 'Beşiktaş, İstanbul', '2026-10-15 20:00:00', '2026-10-20 23:00:00',
   'https://placehold.co/800x400?text=Caz+Festivali', '100-400 TL', 'biletix', 'https://example.com/caz-festivali',
   'https://example.com/caz-festivali/bilet', 'PUBLISHED', 'BILETIX', 'EVT-IST-001', '2026-09-03 06:00:00',
   '{"venue":"Süleyman Seba"}', '2026-09-02 06:00:00', '2026-09-03 06:00:00');

INSERT INTO city_event_interest (id, looking_for_company, created_at, updated_at, city_event_id, user_id)
VALUES
  (926001, true, '2026-09-02 14:00:00', '2026-09-02 14:00:00', 925001, 8110000001),
  (926002, false, '2026-09-03 09:00:00', '2026-09-03 09:00:00', 925002, 8110000003),
  (926003, true, '2026-09-03 10:00:00', '2026-09-03 10:00:00', 925001, 8110000008);

INSERT INTO city_event_ticket_offer (id, provider_code, external_offer_id, seller_name, purchase_url, currency,
                                     min_price, max_price, price_label, availability, fees_included, is_official,
                                     last_checked_at, raw_payload, created_at, updated_at, city_event_id)
VALUES
  (927001, 'BILETIX', 'OFF-ANK-001', 'Biletix', 'https://example.com/tiyatro-festivali/bilet', 'TRY',
   50.00, 150.00, '50-150 TL', 'AVAILABLE', true, true, '2026-09-03 06:00:00', NULL, '2026-09-01 06:00:00', '2026-09-03 06:00:00', 925001),
  (927002, 'BILETIX', 'OFF-IST-001', 'Biletix', 'https://example.com/caz-festivali/bilet', 'TRY',
   100.00, 400.00, '100-400 TL', 'AVAILABLE', true, true, '2026-09-03 06:00:00', NULL, '2026-09-02 06:00:00', '2026-09-03 06:00:00', 925003);

-- ---------------------------------------------------------------------------
-- 12) PARTNER MEKANLAR (partner_venue, partner_offer, partner_venue_staff, partner_offer_redemption, honor_badge, user_honor_badge)
-- ---------------------------------------------------------------------------
INSERT INTO partner_venue (id, name, slug, description, category, logo_url, cover_image_url, address, city,
                           latitude, longitude, phone, website_url, instagram_url, is_featured, is_active, created_at, updated_at)
VALUES
  (930001, 'Kampateşi06 Kamp Alanı', 'kampatesi06', 'Ankara çevresinde kamp ve açık hava etkinlikleri düzenleyen partner alan.',
   'OUTDOOR', 'https://placehold.co/200x200?text=Kampateşi06', 'https://placehold.co/800x400?text=Kampateşi06',
   'Ankara-Konya yolu 25. km', 'Ankara', 39.8500, 32.4500, '0532 000 00 00',
   'https://kampatesi06.example', 'https://instagram.com/kampatesi06', true, true,
   '2026-07-01 10:00:00', '2026-09-03 10:00:00'),
  (930002, 'Oyun Atölyesi Kafe', 'oyun-atolyesi-kafe', 'Masa oyunları kafesi; turnuva ve oyun geceleri düzenler.',
   'ENTERTAINMENT', 'https://placehold.co/200x200?text=Oyun+Atölyesi', 'https://placehold.co/800x400?text=Oyun+Atölyesi',
   'Kızılay Mah. Oyun Sk. No:5', 'Ankara', 39.9208, 32.8541, '0312 000 00 00',
   'https://oyunatolyesi.example', 'https://instagram.com/oyunatolyesi', true, true,
   '2026-07-05 10:00:00', '2026-09-03 10:00:00'),
  (930003, 'Gastronomi Atölyesi', 'gastronomi-atolyesi', 'Yemek atölyeleri ve tadım etkinlikleri.',
   'RESTAURANT', 'https://placehold.co/200x200?text=Gastronomi', 'https://placehold.co/800x400?text=Gastronomi+Atölyesi',
   'Çankaya Mah. Lezzet Sk. No:12', 'Ankara', 39.9000, 32.8500, '0312 000 00 01',
   'https://gastronomiatolyesi.example', 'https://instagram.com/gastronomiatolyesi', false, true,
   '2026-07-08 10:00:00', '2026-09-03 10:00:00');

INSERT INTO partner_offer (id, title, description, discount_percent, minimum_spend, maximum_discount,
                           valid_from, valid_until, per_user_limit, terms, is_active, created_at, updated_at,
                           venue_id, valid_days, daily_start_time, daily_end_time)
VALUES
  (931001, 'Üyelere Özel Kamp İndirimi', 'Bialem üyeleri kamp alanı ücretinde %15 indirim.', 15.00, 500.00, 200.00,
   '2026-09-01 00:00:00', '2026-12-31 23:59:00', 2,
   'Rezervasyon ile geçerlidir. Diğer indirimlerle birleştirilemez.', true,
   '2026-09-01 09:00:00', '2026-09-03 09:00:00', 930001, 'MON,TUE,WED,THU,FRI,SAT,SUN', NULL, NULL),
  (931002, 'Hafta İçi Oyun Gecesi', 'Hafta içi 19:00 sonrası masalar %20 indirimli.', 20.00, 100.00, 150.00,
   '2026-09-01 00:00:00', '2026-11-30 23:59:00', 5,
   'Sadece hafta içi geçerlidir.', true,
   '2026-09-01 09:00:00', '2026-09-03 09:00:00', 930002, 'MON,TUE,WED,THU,FRI', '19:00:00', '23:59:00'),
  (931003, 'Atölye %10 İndirim', 'Tüm atölyelerde %10 indirim.', 10.00, 250.00, 100.00,
   '2026-09-01 00:00:00', NULL, 1,
   'Kontenjanla sınırlıdır.', true,
   '2026-09-01 09:00:00', '2026-09-03 09:00:00', 930003, 'SAT,SUN', '10:00:00', '18:00:00');

INSERT INTO partner_venue_staff (id, is_active, created_at, venue_id, user_id)
VALUES
  (932001, true, '2026-07-10 10:00:00', 930001, 8110000008),
  (932002, true, '2026-07-10 10:00:00', 930002, 8110000002);

INSERT INTO partner_offer_redemption (id, token, redemption_code, status, issued_at, expires_at, redeemed_at,
                                      order_amount, discount_amount, offer_id, venue_id, user_id, redeemed_by_id)
VALUES
  (933001, '11111111-1111-1111-1111-111111111111', 'KAMP15-0001', 'ISSUED',
   '2026-09-02 10:00:00', '2026-10-02 23:59:00', NULL, NULL, NULL, 931001, 930001, 8110000001, NULL),
  (933002, '22222222-2222-2222-2222-222222222222', 'OYUN20-0001', 'REDEMED',
   '2026-09-01 12:00:00', '2026-10-01 23:59:00', '2026-09-03 19:30:00',
   300.00, 60.00, 931002, 930002, 8110000003, 8110000002);

INSERT INTO honor_badge (id, code, name_template, description, badge_type, minimum_check_ins, is_active, created_at, community_id)
VALUES
  (934001, 'CITY_ANKARA', 'Ankara Kaşifi', 'Ankara''da 5 etkinliğe katılanlara verilir.', 'CITY', 5, true,
   '2026-07-01 10:00:00', NULL),
  (934002, 'CAMP_MASTER', 'Kamp Ustası', 'Kamp etkinliklerine 3 kez katılanlara verilir.', 'COMMUNITY', 3, true,
   '2026-07-01 10:00:00', 8140000016),
  (934003, 'GAME_NIGHT', 'Oyun Gecesi Şampiyonu', 'Masa oyunu turnuvalarına katılanlara verilir.', 'COMMUNITY', 2, true,
   '2026-07-01 10:00:00', 8140000003);

INSERT INTO user_honor_badge (id, reason, awarded_at, user_id, badge_id, awarded_by_id)
VALUES
  (935001, '5 kamp etkinliği katılımı', '2026-09-01 10:00:00', 8110000008, 934002, 8110000008),
  (935002, 'Turnuva katılımı', '2026-08-23 10:00:00', 8110000003, 934003, 8110000008);

-- ---------------------------------------------------------------------------
-- 13) MESAJLASMA (direct_conversation, direct_message)
-- ---------------------------------------------------------------------------
INSERT INTO direct_conversation (id, created_at, updated_at, participant_one_id, participant_two_id)
VALUES
  (936001, '2026-08-15 20:00:00', '2026-09-03 14:00:00', 8110000001, 8110000008),
  (936002, '2026-09-01 09:00:00', '2026-09-03 14:00:00', 8110000003, 8110000008);

INSERT INTO direct_message (id, body, created_at, read_at, conversation_id, sender_id)
VALUES
  (937001, 'Merhaba! Yürüyüş rotasını paylaşabilir misin?', '2026-09-01 10:00:00', '2026-09-01 10:05:00', 936001, 8110000001),
  (937002, 'Tabii, haritayı gönderiyorum.', '2026-09-01 10:06:00', '2026-09-01 10:10:00', 936001, 8110000008),
  (937003, 'Teşekkürler, hafta sonu görüşürüz!', '2026-09-01 10:11:00', '2026-09-01 10:12:00', 936001, 8110000001),
  (937004, 'Turnuvada yer ayırttınız mı?', '2026-09-02 14:00:00', '2026-09-02 14:05:00', 936002, 8110000003),
  (937005, 'Evet, onaylandı. Görüşürüz!', '2026-09-02 14:06:00', NULL, 936002, 8110000008);

-- ---------------------------------------------------------------------------
-- 14) PUSH TOKEN (push_token, push_device_token)
-- ---------------------------------------------------------------------------
INSERT INTO push_token (id, device_token, platform, device_name, is_active, last_seen_at, created_at, updated_at, user_id)
VALUES
  (938001, 'android-device-token-adem-001', 'ANDROID', 'Samsung A55', true,
   '2026-09-03 14:00:00', '2026-08-01 10:00:00', '2026-09-03 14:00:00', 8110000001);

-- ---------------------------------------------------------------------------
-- 15) STORE: kupon, adres, wishlist, review, odeme
-- ---------------------------------------------------------------------------
INSERT INTO store_coupon (id, code, discount_type, discount_value, minimum_cart_amount, maximum_discount,
                          start_date, end_date, usage_limit, per_user_limit, is_active, usage_count,
                          created_at, updated_at)
VALUES
  (939001, 'BIALEM10', 'PERCENT', 10.00, 200.00, 100.00, '2026-09-01 00:00:00', '2026-12-31 23:59:00', 500, 2, true, 37,
   '2026-09-01 09:00:00', '2026-09-03 14:00:00'),
  (939002, 'KAMP25', 'FIXED', 25.00, 150.00, NULL, '2026-09-01 00:00:00', '2026-10-31 23:59:00', 200, 1, true, 12,
   '2026-09-01 09:00:00', '2026-09-03 14:00:00');

INSERT INTO store_address (id, title, first_name, last_name, phone, country, city, district, neighborhood,
                           address_line, postal_code, note, is_default, user_id, created_at, updated_at)
VALUES
  (940001, 'Ev', 'Adem', 'Demir', '0532 111 22 33', 'Türkiye', 'Ankara', 'Çankaya', 'Kızılay',
   'Atatürk Bulvarı No:12 D:5', '06420', 'Kapıcıya haber verin', true, 1051,
   '2026-08-10 10:00:00', '2026-08-10 10:00:00');

INSERT INTO store_wishlist (id, user_id, product_id, created_at)
VALUES
  (941001, 1051, 7208, '2026-08-15 10:00:00'),
  (941002, 1051, 7219, '2026-08-16 10:00:00');

-- Mevcut store_order kayitlarinin payment_status'lerini gercekci hale getir
UPDATE store_order SET payment_status = 'PAID', order_status = 'PROCESSING', updated_at = '2026-08-30 10:00:00'
 WHERE id = 6401 AND payment_status = 'WAITING_ADMIN_APPROVAL';

-- ---------------------------------------------------------------------------
-- 16) PLATFORM EKIBI (platform_team_member) - ek kayit
-- ---------------------------------------------------------------------------
INSERT INTO platform_team_member (id, role_code, created_at, updated_at, user_id, assigned_by_id)
VALUES
  (942001, 'SUPPORT', '2026-07-01 10:00:00', '2026-07-01 10:00:00', 8110000003, 8110000008),
  (942002, 'EDITOR', '2026-07-01 10:00:00', '2026-07-01 10:00:00', 8110000001, 8110000008);

-- ---------------------------------------------------------------------------
-- 17) AI USAGE LOG (ai_usage_log)
-- ---------------------------------------------------------------------------
INSERT INTO ai_usage_log (id, created_at, user_id)
VALUES
  (943001, '2026-09-01 10:00:00', 8110000001),
  (943002, '2026-09-02 12:00:00', 8110000008);

-- ---------------------------------------------------------------------------
-- 18) SEQUENCE: en yuksek ID'nin uzerine cek
-- ---------------------------------------------------------------------------
SELECT setval('sequence_generator', GREATEST(
  (SELECT COALESCE(MAX(id),1) FROM event),
  (SELECT COALESCE(MAX(id),1) FROM event_participant),
  (SELECT COALESCE(MAX(id),1) FROM event_message),
  (SELECT COALESCE(MAX(id),1) FROM event_rating),
  (SELECT COALESCE(MAX(id),1) FROM event_ticket),
  (SELECT COALESCE(MAX(id),1) FROM ticket_order),
  (SELECT COALESCE(MAX(id),1) FROM order_item),
  (SELECT COALESCE(MAX(id),1) FROM payment),
  (SELECT COALESCE(MAX(id),1) FROM ticket),
  (SELECT COALESCE(MAX(id),1) FROM post),
  (SELECT COALESCE(MAX(id),1) FROM post_media),
  (SELECT COALESCE(MAX(id),1) FROM comment),
  (SELECT COALESCE(MAX(id),1) FROM story_group),
  (SELECT COALESCE(MAX(id),1) FROM story),
  (SELECT COALESCE(MAX(id),1) FROM story_view),
  (SELECT COALESCE(MAX(id),1) FROM story_reaction),
  (SELECT COALESCE(MAX(id),1) FROM story_element),
  (SELECT COALESCE(MAX(id),1) FROM hashtag),
  (SELECT COALESCE(MAX(id),1) FROM story_hashtag),
  (SELECT COALESCE(MAX(id),1) FROM follow),
  (SELECT COALESCE(MAX(id),1) FROM follow_request),
  (SELECT COALESCE(MAX(id),1) FROM user_review),
  (SELECT COALESCE(MAX(id),1) FROM report),
  (SELECT COALESCE(MAX(id),1) FROM notification),
  (SELECT COALESCE(MAX(id),1) FROM city_event),
  (SELECT COALESCE(MAX(id),1) FROM city_event_interest),
  (SELECT COALESCE(MAX(id),1) FROM city_event_ticket_offer),
  (SELECT COALESCE(MAX(id),1) FROM partner_venue),
  (SELECT COALESCE(MAX(id),1) FROM partner_offer),
  (SELECT COALESCE(MAX(id),1) FROM partner_venue_staff),
  (SELECT COALESCE(MAX(id),1) FROM partner_offer_redemption),
  (SELECT COALESCE(MAX(id),1) FROM honor_badge),
  (SELECT COALESCE(MAX(id),1) FROM user_honor_badge),
  (SELECT COALESCE(MAX(id),1) FROM direct_conversation),
  (SELECT COALESCE(MAX(id),1) FROM direct_message),
  (SELECT COALESCE(MAX(id),1) FROM push_token),
  (SELECT COALESCE(MAX(id),1) FROM store_coupon),
  (SELECT COALESCE(MAX(id),1) FROM store_address),
  (SELECT COALESCE(MAX(id),1) FROM store_wishlist),
  (SELECT COALESCE(MAX(id),1) FROM platform_team_member),
  (SELECT COALESCE(MAX(id),1) FROM ai_usage_log)
), true);

COMMIT;

-- ---------------------------------------------------------------------------
-- Dogrulama sorgulari
-- ---------------------------------------------------------------------------
SELECT 'event' AS tbl, COUNT(*) FROM event
UNION ALL SELECT 'event_participant', COUNT(*) FROM event_participant
UNION ALL SELECT 'event_message', COUNT(*) FROM event_message
UNION ALL SELECT 'event_rating', COUNT(*) FROM event_rating
UNION ALL SELECT 'event_ticket', COUNT(*) FROM event_ticket
UNION ALL SELECT 'ticket_order', COUNT(*) FROM ticket_order
UNION ALL SELECT 'order_item', COUNT(*) FROM order_item
UNION ALL SELECT 'payment', COUNT(*) FROM payment
UNION ALL SELECT 'ticket', COUNT(*) FROM ticket
UNION ALL SELECT 'post', COUNT(*) FROM post
UNION ALL SELECT 'post_media', COUNT(*) FROM post_media
UNION ALL SELECT 'comment', COUNT(*) FROM comment
UNION ALL SELECT 'story_group', COUNT(*) FROM story_group
UNION ALL SELECT 'story', COUNT(*) FROM story
UNION ALL SELECT 'story_view', COUNT(*) FROM story_view
UNION ALL SELECT 'story_reaction', COUNT(*) FROM story_reaction
UNION ALL SELECT 'story_element', COUNT(*) FROM story_element
UNION ALL SELECT 'hashtag', COUNT(*) FROM hashtag
UNION ALL SELECT 'story_hashtag', COUNT(*) FROM story_hashtag
UNION ALL SELECT 'follow', COUNT(*) FROM follow
UNION ALL SELECT 'follow_request', COUNT(*) FROM follow_request
UNION ALL SELECT 'user_review', COUNT(*) FROM user_review
UNION ALL SELECT 'report', COUNT(*) FROM report
UNION ALL SELECT 'notification', COUNT(*) FROM notification
UNION ALL SELECT 'city_event', COUNT(*) FROM city_event
UNION ALL SELECT 'city_event_interest', COUNT(*) FROM city_event_interest
UNION ALL SELECT 'city_event_ticket_offer', COUNT(*) FROM city_event_ticket_offer
UNION ALL SELECT 'partner_venue', COUNT(*) FROM partner_venue
UNION ALL SELECT 'partner_offer', COUNT(*) FROM partner_offer
UNION ALL SELECT 'partner_venue_staff', COUNT(*) FROM partner_venue_staff
UNION ALL SELECT 'partner_offer_redemption', COUNT(*) FROM partner_offer_redemption
UNION ALL SELECT 'honor_badge', COUNT(*) FROM honor_badge
UNION ALL SELECT 'user_honor_badge', COUNT(*) FROM user_honor_badge
UNION ALL SELECT 'direct_conversation', COUNT(*) FROM direct_conversation
UNION ALL SELECT 'direct_message', COUNT(*) FROM direct_message
UNION ALL SELECT 'store_wishlist', COUNT(*) FROM store_wishlist
UNION ALL SELECT 'store_address', COUNT(*) FROM store_address
UNION ALL SELECT 'store_coupon', COUNT(*) FROM store_coupon
ORDER BY tbl;