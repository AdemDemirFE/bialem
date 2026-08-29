-- Store modulu ornek/seed verileri.
-- Bu dosya sadece bos store tablolarinda calistirilmalidir.

-- Markalar
INSERT INTO store_brand (id, name, slug, description, is_active, created_at, created_by)
VALUES
  (7001, 'Bialem', 'bialem', 'Bialem resmi marka ürünleri', true, ${now}, 'system'),
  (7002, 'OutdoorX', 'outdoorx', 'Outdoor ve kamp ekipmanları', true, ${now}, 'system'),
  (7003, 'CampMaster', 'campmaster', 'Profesyonel kamp malzemeleri', true, ${now}, 'system'),
  (7004, 'SportLine', 'sportline', 'Spor ekipmanları', true, ${now}, 'system');

-- Ana kategoriler
INSERT INTO store_category (id, name, slug, description, image_url, sort_order, is_active, created_at, created_by)
VALUES
  (7101, 'Kamp & Outdoor', 'kamp-outdoor', 'Çadır, uyku tulumu ve kamp ekipmanları', 'https://placehold.co/400x400?text=Kamp', 1, true, ${now}, 'system'),
  (7102, 'Giyim', 'giyim', 'Kadın, erkek ve çocuk giyim ürünleri', 'https://placehold.co/400x400?text=Giyim', 2, true, ${now}, 'system'),
  (7103, 'Aksesuar', 'aksesuar', 'Çanta, kupa ve günlük aksesuarlar', 'https://placehold.co/400x400?text=Aksesuar', 3, true, ${now}, 'system'),
  (7104, 'Spor & Outdoor', 'spor-outdoor', 'Spor ekipmanları ve outdoor aktivite malzemeleri', 'https://placehold.co/400x400?text=Spor', 4, true, ${now}, 'system'),
  (7105, 'Oyun & Eğlence', 'oyun-eglence', 'Masa oyunları ve outdoor oyunlar', 'https://placehold.co/400x400?text=Oyun', 5, true, ${now}, 'system');

-- Alt kategoriler
INSERT INTO store_category (id, name, slug, description, parent_id, image_url, sort_order, is_active, created_at, created_by)
VALUES
  (7111, 'Çadır', 'cadir', '2-6 kişilik kamp çadırları', 7101, 'https://placehold.co/400x400?text=Cadir', 1, true, ${now}, 'system'),
  (7112, 'Uyku Tulumu & Mat', 'uyku-tulumu-mat', 'Uyku tulumları ve şişme matlar', 7101, 'https://placehold.co/400x400?text=Uyku', 2, true, ${now}, 'system'),
  (7113, 'Kamp Mutfak', 'kamp-mutfak', 'Kamp ocağı, termos ve mutfak ekipmanları', 7101, 'https://placehold.co/400x400?text=Mutfak', 3, true, ${now}, 'system'),
  (7114, 'Kamp Mobilyası', 'kamp-mobilyasi', 'Kamp sandalyesi, masa ve mobilyalar', 7101, 'https://placehold.co/400x400?text=Mobilya', 4, true, ${now}, 'system'),
  (7121, 'Kadın', 'kadin', 'Kadın giyim ürünleri', 7102, 'https://placehold.co/400x400?text=Kadin', 1, true, ${now}, 'system'),
  (7122, 'Erkek', 'erkek', 'Erkek giyim ürünleri', 7102, 'https://placehold.co/400x400?text=Erkek', 2, true, ${now}, 'system'),
  (7123, 'Çocuk', 'cocuk', 'Çocuk giyim ürünleri', 7102, 'https://placehold.co/400x400?text=Cocuk', 3, true, ${now}, 'system'),
  (7131, 'Çanta', 'canta', 'Spor çantası, sırt çantası ve bez çanta', 7103, 'https://placehold.co/400x400?text=Canta', 1, true, ${now}, 'system'),
  (7132, 'Kupa & Termos', 'kupa-termos', 'Kupa, termos ve su matarası', 7103, 'https://placehold.co/400x400?text=Kupa', 2, true, ${now}, 'system'),
  (7141, 'Spor Ekipmanları', 'spor-ekipmanlari', 'Yoga matı, trekking batonu ve spor malzemeleri', 7104, 'https://placehold.co/400x400?text=Ekipman', 1, true, ${now}, 'system'),
  (7151, 'Masa Oyunları', 'masa-oyunlari', 'Kutu oyunları ve masa oyunu setleri', 7105, 'https://placehold.co/400x400?text=MasaOyunu', 1, true, ${now}, 'system'),
  (7152, 'Outdoor Oyunlar', 'outdoor-oyunlar', 'Frisbee, badminton ve outdoor oyunlar', 7105, 'https://placehold.co/400x400?text=OutdoorOyun', 2, true, ${now}, 'system');

-- Ürünler
INSERT INTO store_product (
  id, name, slug, short_description, description, sku, barcode, price, discounted_price,
  currency, stock_quantity, low_stock_threshold, status, is_featured, is_active,
  weight, width, height, length, rating_average, review_count, sales_count,
  category_id, brand_id, seller_id, created_at, created_by
)
VALUES
  (7201, 'Bialem Logolu Tişört - Erkek', 'bialem-logolu-tisort-erkek', 'Erkekler için Bialem logolu pamuklu tişört', 'Rahat kalıp, %100 pamuk, Bialem logolu erkek tişörtü.', 'BIA-TSH-E001', '8680000000001', 349.00, 299.00, 'TRY', 120, 10, 'ACTIVE', true, true, 0.25, 30.00, 40.00, 2.00, 4.50, 12, 45, 7122, 7001, 1, ${now}, 'system'),
  (7202, 'Bialem Logolu Tişört - Kadın', 'bialem-logolu-tisort-kadin', 'Kadınlar için Bialem logolu pamuklu tişört', 'Rahat kalıp, %100 pamuk, Bialem logolu kadın tişörtü.', 'BIA-TSH-K001', '8680000000002', 349.00, 299.00, 'TRY', 95, 10, 'ACTIVE', true, true, 0.22, 28.00, 38.00, 2.00, 4.70, 8, 32, 7121, 7001, 1, ${now}, 'system'),
  (7203, 'Bialem Logolu Tişört - Çocuk', 'bialem-logolu-tisort-cocuk', 'Çocuklar için Bialem logolu pamuklu tişört', 'Yumuşak kumaş, Bialem logolu çocuk tişörtü.', 'BIA-TSH-C001', '8680000000003', 249.00, 199.00, 'TRY', 60, 10, 'ACTIVE', false, true, 0.18, 26.00, 34.00, 2.00, 4.80, 5, 18, 7123, 7001, 1, ${now}, 'system'),
  (7204, 'Bialem Logolu Kupa', 'bialem-logolu-kupa', 'Seramik Bialem logolu kupa', '350 ml seramik kupa, Bialem baskılı.', 'BIA-KUP-001', '8680000000004', 129.00, 99.00, 'TRY', 200, 20, 'ACTIVE', true, true, 0.40, 12.00, 10.00, 12.00, 4.60, 22, 78, 7132, 7001, 1, ${now}, 'system'),
  (7205, 'Bialem Logolu Bez Çanta', 'bialem-logolu-bez-canta', 'Bialem logolu doğal bez çanta', 'Dayanıklı kumaş, omuz askılı bez çanta.', 'BIA-CNT-001', '8680000000005', 89.00, 69.00, 'TRY', 150, 15, 'ACTIVE', false, true, 0.15, 35.00, 40.00, 5.00, 4.40, 14, 56, 7131, 7001, 1, ${now}, 'system'),
  (7206, 'Bialem Logolu Spor Çantası', 'bialem-logolu-spor-cantasi', 'Bialem logolu orta boy spor çantası', 'Su geçirmez kumaş, ayakkabı bölmesi.', 'BIA-SPC-001', '8680000000006', 449.00, 399.00, 'TRY', 80, 10, 'ACTIVE', true, true, 0.70, 50.00, 25.00, 25.00, 4.80, 18, 41, 7131, 7001, 1, ${now}, 'system'),
  (7207, 'Bialem Logolu Şapka', 'bialem-logolu-sapka', 'Bialem logolu beyzbol şapkası', 'Nefes alan kumaş, ayarlanabilir tokalı şapka.', 'BIA-SAP-001', '8680000000007', 149.00, 119.00, 'TRY', 110, 15, 'ACTIVE', false, true, 0.10, 25.00, 15.00, 20.00, 4.50, 9, 29, 7131, 7001, 1, ${now}, 'system'),
  (7208, '2 Kişilik Kamp Çadırı', '2-kisilik-kamp-cadiri', 'Hafif ve dayanıklı 2 kişilik kamp çadırı', 'Su geçirmez, çift katmanlı, kolay kurulum.', 'CAM-CAD-002', '8680000000008', 2499.00, 1999.00, 'TRY', 35, 5, 'ACTIVE', true, true, 2.50, 120.00, 210.00, 100.00, 4.30, 31, 67, 7111, 7002, 1, ${now}, 'system'),
  (7209, '4 Kişilik Aile Çadırı', '4-kisilik-aile-cadiri', 'Geniş 4 kişilik aile kamp çadırı', 'İki odalı, su geçirmez, aile çadırı.', 'CAM-CAD-004', '8680000000009', 4499.00, 3799.00, 'TRY', 20, 3, 'ACTIVE', true, true, 5.20, 240.00, 210.00, 180.00, 4.60, 15, 28, 7111, 7002, 1, ${now}, 'system'),
  (7210, 'Mevsimlik Uyku Tulumu', 'mevsimlik-uyku-tulumu', '-5°C konfor sıcaklıklı uyku tulumu', 'Hafif, sıkıştırma torbası dahil.', 'CAM-UYK-001', '8680000000010', 899.00, 749.00, 'TRY', 50, 8, 'ACTIVE', false, true, 1.20, 40.00, 210.00, 40.00, 4.50, 11, 34, 7112, 7003, 1, ${now}, 'system'),
  (7211, 'Şişme Kamp Matı', 'sisme-kamp-mati', 'Hafif şişme kamp matı', '5 cm kalınlık, yastıklı başlık.', 'CAM-MAT-001', '8680000000011', 599.00, 499.00, 'TRY', 45, 8, 'ACTIVE', false, true, 0.80, 60.00, 200.00, 15.00, 4.40, 7, 21, 7112, 7003, 1, ${now}, 'system'),
  (7212, 'Kamp Ocağı Seti', 'kamp-ocagi-seti', 'Taşınabilir gazlı kamp ocağı seti', '2 gözlü oca, çakmaklı, taşıma çantası dahil.', 'CAM-OCA-001', '8680000000012', 799.00, 699.00, 'TRY', 40, 6, 'ACTIVE', true, true, 1.50, 35.00, 30.00, 20.00, 4.20, 19, 43, 7113, 7003, 1, ${now}, 'system'),
  (7213, 'Paslanmaz Termos 750ml', 'paslanmaz-termos-750ml', 'Çift katmanlı paslanmaz çelik termos', '24 saat sıcak/soğuk tutan termos.', 'CAM-TER-001', '8680000000013', 349.00, 299.00, 'TRY', 75, 12, 'ACTIVE', false, true, 0.45, 10.00, 30.00, 10.00, 4.70, 25, 62, 7113, 7003, 1, ${now}, 'system'),
  (7214, 'Su Matarası 1L', 'su-matara-1l', 'BPA içermeyen 1 litre su matarası', 'Sızıntı yapmayan kapak, taşıma halkası.', 'CAM-MAT-001L', '8680000000014', 179.00, 149.00, 'TRY', 100, 15, 'ACTIVE', false, true, 0.20, 10.00, 28.00, 10.00, 4.50, 13, 37, 7113, 7002, 1, ${now}, 'system'),
  (7215, 'Trekking Batonu', 'trekking-batonu', 'Ayarlanabilir alüminyum trekking batonu', 'Kilit mekanizmalı, hafif, anti-şok.', 'SP-BAT-001', '8680000000015', 599.00, 499.00, 'TRY', 55, 8, 'ACTIVE', true, true, 0.50, 7.00, 70.00, 7.00, 4.60, 28, 54, 7141, 7004, 1, ${now}, 'system'),
  (7216, 'Kamp Sandalyesi', 'kamp-sandalyesi', 'Katlanır hafif kamp sandalyesi', 'Taşıma çantası dahil, maksimum 120 kg.', 'CAM-SND-001', '8680000000016', 449.00, 379.00, 'TRY', 60, 8, 'ACTIVE', false, true, 1.10, 50.00, 80.00, 50.00, 4.30, 16, 38, 7114, 7003, 1, ${now}, 'system'),
  (7217, 'Katlanır Kamp Masası', 'katlanir-kamp-masasi', 'Alüminyum katlanır kamp masası', 'Hafif, taşıma saplı, kolay kurulum.', 'CAM-MAS-001', '8680000000017', 699.00, 599.00, 'TRY', 30, 5, 'ACTIVE', false, true, 2.00, 70.00, 110.00, 70.00, 4.40, 10, 22, 7114, 7003, 1, ${now}, 'system'),
  (7218, 'Masa Oyunu Seti', 'masa-oyunu-seti', '5 oyunlu masa oyunu seti', 'Satranç, tavla, okey, domino ve kağıt oyunu.', 'OYU-SET-001', '8680000000018', 299.00, 249.00, 'TRY', 85, 10, 'ACTIVE', true, true, 1.80, 30.00, 30.00, 10.00, 4.80, 33, 71, 7151, 7001, 1, ${now}, 'system'),
  (7219, 'Frisbee', 'frisbee', 'Dayanıklı outdoor frisbee', '180 gr, uzun mesafe uçuş.', 'OYU-FRI-001', '8680000000019', 99.00, 79.00, 'TRY', 120, 15, 'ACTIVE', false, true, 0.18, 28.00, 28.00, 3.00, 4.50, 8, 26, 7152, 7004, 1, ${now}, 'system'),
  (7220, 'Yoga Matı', 'yoga-mati', 'Kaymaz 6mm yoga matı', 'Taşıma askılı, ergonomik yoga matı.', 'SP-YOG-001', '8680000000020', 349.00, 279.00, 'TRY', 70, 10, 'ACTIVE', true, true, 0.90, 61.00, 183.00, 6.00, 4.70, 20, 48, 7141, 7004, 1, ${now}, 'system');

-- Ürün görselleri (her ürüne birincil görsel)
INSERT INTO store_product_image (id, image_url, thumbnail_url, is_primary, sort_order, alt_text, product_id, created_at)
VALUES
  (7301, 'https://placehold.co/600x600?text=Bialem+Tisort+Erkek', 'https://placehold.co/300x300?text=Bialem+Tisort+Erkek', true, 0, 'Bialem Logolu Tişört Erkek', 7201, ${now}),
  (7302, 'https://placehold.co/600x600?text=Bialem+Tisort+Kadin', 'https://placehold.co/300x300?text=Bialem+Tisort+Kadin', true, 0, 'Bialem Logolu Tişört Kadın', 7202, ${now}),
  (7303, 'https://placehold.co/600x600?text=Bialem+Tisort+Cocuk', 'https://placehold.co/300x300?text=Bialem+Tisort+Cocuk', true, 0, 'Bialem Logolu Tişört Çocuk', 7203, ${now}),
  (7304, 'https://placehold.co/600x600?text=Bialem+Kupa', 'https://placehold.co/300x300?text=Bialem+Kupa', true, 0, 'Bialem Logolu Kupa', 7204, ${now}),
  (7305, 'https://placehold.co/600x600?text=Bialem+Bez+Canta', 'https://placehold.co/300x300?text=Bialem+Bez+Canta', true, 0, 'Bialem Logolu Bez Çanta', 7205, ${now}),
  (7306, 'https://placehold.co/600x600?text=Bialem+Spor+Canta', 'https://placehold.co/300x300?text=Bialem+Spor+Canta', true, 0, 'Bialem Logolu Spor Çantası', 7206, ${now}),
  (7307, 'https://placehold.co/600x600?text=Bialem+Sapka', 'https://placehold.co/300x300?text=Bialem+Sapka', true, 0, 'Bialem Logolu Şapka', 7207, ${now}),
  (7308, 'https://placehold.co/600x600?text=2+Kisilik+Cadir', 'https://placehold.co/300x300?text=2+Kisilik+Cadir', true, 0, '2 Kişilik Kamp Çadırı', 7208, ${now}),
  (7309, 'https://placehold.co/600x600?text=4+Kisilik+Cadir', 'https://placehold.co/300x300?text=4+Kisilik+Cadir', true, 0, '4 Kişilik Aile Çadırı', 7209, ${now}),
  (7310, 'https://placehold.co/600x600?text=Uyku+Tulumu', 'https://placehold.co/300x300?text=Uyku+Tulumu', true, 0, 'Mevsimlik Uyku Tulumu', 7210, ${now}),
  (7311, 'https://placehold.co/600x600?text=Sisme+Mat', 'https://placehold.co/300x300?text=Sisme+Mat', true, 0, 'Şişme Kamp Matı', 7211, ${now}),
  (7312, 'https://placehold.co/600x600?text=Kamp+Ocagi', 'https://placehold.co/300x300?text=Kamp+Ocagi', true, 0, 'Kamp Ocağı Seti', 7212, ${now}),
  (7313, 'https://placehold.co/600x600?text=Termos', 'https://placehold.co/300x300?text=Termos', true, 0, 'Paslanmaz Termos', 7213, ${now}),
  (7314, 'https://placehold.co/600x600?text=Su+Matara', 'https://placehold.co/300x300?text=Su+Matara', true, 0, 'Su Matarası 1L', 7214, ${now}),
  (7315, 'https://placehold.co/600x600?text=Trekking+Batonu', 'https://placehold.co/300x300?text=Trekking+Batonu', true, 0, 'Trekking Batonu', 7215, ${now}),
  (7316, 'https://placehold.co/600x600?text=Kamp+Sandalyesi', 'https://placehold.co/300x300?text=Kamp+Sandalyesi', true, 0, 'Kamp Sandalyesi', 7216, ${now}),
  (7317, 'https://placehold.co/600x600?text=Kamp+Masasi', 'https://placehold.co/300x300?text=Kamp+Masasi', true, 0, 'Katlanır Kamp Masası', 7217, ${now}),
  (7318, 'https://placehold.co/600x600?text=Masa+Oyunu+Seti', 'https://placehold.co/300x300?text=Masa+Oyunu+Seti', true, 0, 'Masa Oyunu Seti', 7218, ${now}),
  (7319, 'https://placehold.co/600x600?text=Frisbee', 'https://placehold.co/300x300?text=Frisbee', true, 0, 'Frisbee', 7219, ${now}),
  (7320, 'https://placehold.co/600x600?text=Yoga+Mati', 'https://placehold.co/300x300?text=Yoga+Mati', true, 0, 'Yoga Matı', 7220, ${now});
