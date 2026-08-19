export type LegalDocumentKey = "privacy" | "terms" | "kvkk" | "community";

type LegalSection = {
  heading: string;
  paragraphs?: string[];
  bullets?: string[];
};

export type LegalDocument = {
  title: string;
  updatedAt: string;
  disclaimer?: string;
  sections: LegalSection[];
};

export const legalDocuments: Record<LegalDocumentKey, LegalDocument> = {
  privacy: {
    title: "Gizlilik Politikası",
    updatedAt: "13 Temmuz 2026",
    disclaimer: "Bu metin yayın öncesinde hukuk uzmanı tarafından kontrol edilmelidir.",
    sections: [
      {
        heading: "Toplanan bilgiler",
        paragraphs: ["Hesap, profil, topluluk ve etkinlik bilgileri; paylaşımlar, yorumlar, puanlar ve uygulama kullanımına ilişkin teknik veriler işlenebilir."]
      },
      {
        heading: "Verilerin kullanımı",
        bullets: [
          "Hesap ve oturum yönetimini sağlamak",
          "Topluluk, etkinlik, yorum ve puanlama özelliklerini sunmak",
          "Güvenlik, moderasyon ve kötüye kullanımı önlemek",
          "Yasal yükümlülükleri yerine getirmek"
        ]
      },
      {
        heading: "Hizmet sağlayıcılar",
        paragraphs: ["Uygulama altyapısında Supabase ve Expo hizmetleri kullanılır. Yapay zekâ asistanına yazılan mesajlar yanıt üretilmesi amacıyla OpenAI altyapısına iletilebilir."]
      },
      {
        heading: "Saklama ve silme",
        paragraphs: ["Veriler hizmet için gerekli süre boyunca veya yasal zorunluluklar kapsamında saklanır. Kullanıcı, Hesap ve yasal ekranından hesabını ve ilişkili içeriklerini silebilir."]
      }
    ]
  },
  terms: {
    title: "Kullanım Şartları",
    updatedAt: "13 Temmuz 2026",
    disclaimer: "Bu metin yayın öncesinde hukuk uzmanı tarafından kontrol edilmelidir.",
    sections: [
      {
        heading: "Hizmetin konusu",
        paragraphs: ["Bialem; topluluk kurma, etkinlik talebi oluşturma, paylaşım yapma, yorum ve puan verme özellikleri sunan bir sosyal etkinlik platformudur."]
      },
      {
        heading: "Kullanıcı sorumlulukları",
        bullets: [
          "Doğru hesap bilgileri vermek",
          "Başkalarının haklarına ve güvenliğine saygı göstermek",
          "Hukuka aykırı, tehditkâr veya zarar verici içerik paylaşmamak",
          "Topluluk ve etkinlik kurallarına uymak"
        ]
      },
      {
        heading: "Moderasyon",
        paragraphs: ["Bialem, kuralları ihlal eden içerikleri kaldırabilir, görünürlüğünü sınırlayabilir ve gerekli durumlarda hesapları askıya alabilir veya kapatabilir."]
      }
    ]
  },
  kvkk: {
    title: "KVKK Aydınlatma Metni",
    updatedAt: "13 Temmuz 2026",
    disclaimer: "Veri sorumlusu ve resmî iletişim bilgileri yayın öncesinde tamamlanmalıdır.",
    sections: [
      {
        heading: "İşlenen veri kategorileri",
        bullets: [
          "Kimlik, hesap ve iletişim bilgileri",
          "Profil, topluluk ve etkinlik katılım bilgileri",
          "Yorum, paylaşım, puan ve değerlendirmeler",
          "Teknik kullanım ve güvenlik kayıtları"
        ]
      },
      {
        heading: "Isleme amaclari",
        paragraphs: ["Veriler hesap yönetimi, hizmetlerin sunulmasi, güvenlik ve moderasyon, destek talepleri ve hukuki yükümlülükler için işlenir."]
      },
      {
        heading: "Haklariniz",
        paragraphs: ["KVKK kapsamında verilerinizin işlenip işlenmediğini ogrenme, bilgi isteme, duzeltme, silme veya yok etme ve itiraz etme haklarina sahipsiniz."]
      }
    ]
  },
  community: {
    title: "Topluluk Kurallari",
    updatedAt: "13 Temmuz 2026",
    sections: [
      {
        heading: "Birlikte güvenli bir alan kuruyoruz",
        bullets: [
          "Taciz, tehdit, nefret soylemi ve zorbalik yasaktir.",
          "Izinsiz kişisel bilgi, spam ve aldatmaya yonelik içerik paylaşılamaz.",
          "Cinsel istismar, cocuk güvenliğini ihlal eden veya yasa disi içeriklere izin verilmez.",
          "Sikayet edilen içerik ve kullanıcılar moderasyon ekibi tarafından incelenir.",
          "Güvenliği tehdit eden açıl durumlarda yerel yetkili birimlere başvurulmalıdır."
        ]
      }
    ]
  }
};

export function isLegalDocumentKey(value: string): value is LegalDocumentKey {
  return value in legalDocuments;
}
