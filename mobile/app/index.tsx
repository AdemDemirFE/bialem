import AsyncStorage from "@react-native-async-storage/async-storage";
import { Ionicons } from "@expo/vector-icons";
import { Redirect, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useAuth } from "../src/lib/auth";
import { BialemHeroField } from "../src/experiences/BialemHeroField";
import { colors } from "../src/theme/colors";
import { imageSources } from "../src/theme/images";
import { useTheme } from "../src/theme/theme";

type AuthMode = "signin" | "signup";
const ONBOARDING_KEY = "bialem:onboarding-v2-complete";
const authPalettes = {
  light: {
    page: "#f6f8ff",
    surface: "#ffffff",
    ink: "#081a44",
    muted: "#42527d",
    border: "#cfddfb",
    accent: "#6f2cff",
    accentSoft: "#eee4ff"
  },
  dark: {
    page: "#070b18",
    surface: "#11182a",
    ink: "#ffffff",
    muted: "#c7d2e8",
    border: "#35415f",
    accent: "#b28cff",
    accentSoft: "#2b1d48"
  }
} as const;

export default function HomeScreen() {
  const router = useRouter();
  const { resolvedTheme } = useTheme();
  const authPalette = authPalettes[resolvedTheme];
  const {
    user,
    profile,
    loading,
    error,
    notice,
    signIn,
    signUp,
    resendSignUpEmail,
    saveProfile,
    clearError,
    clearNotice
  } = useAuth();
  const [mode, setMode] = useState<AuthMode>("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [displayName, setDisplayName] = useState("");
  const [username, setUsername] = useState("");
  const [city, setCity] = useState("");
  const [bio, setBio] = useState("");
  const [approvePrivacy, setApprovePrivacy] = useState(false);
  const [approveTerms, setApproveTerms] = useState(false);
  const [approveKvkk, setApproveKvkk] = useState(false);
  const [onboardingChecked, setOnboardingChecked] = useState(false);
  const [showOnboarding, setShowOnboarding] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  useEffect(() => {
    const checkOnboarding = async () => {
      try {
        const completed = await AsyncStorage.getItem(ONBOARDING_KEY);
        setShowOnboarding(!completed);
      } finally {
        setOnboardingChecked(true);
      }
    };

    void checkOnboarding();
  }, []);

  useEffect(() => {
    if (resendCooldown <= 0) return;

    const timer = setTimeout(() => {
      setResendCooldown((seconds) => Math.max(0, seconds - 1));
    }, 1000);

    return () => clearTimeout(timer);
  }, [resendCooldown]);

  const profileComplete = !!profile?.display_name && !!profile?.username;

  const handleAuthSubmit = async () => {
    clearError();
    clearNotice();

    if (mode === "signin") {
      await signIn(email, password);
      return;
    }

    if (!approvePrivacy || !approveTerms || !approveKvkk) {
      return;
    }

    const signedUp = await signUp({
      email,
      password,
      displayName,
      username
    });

    if (signedUp) {
      setResendCooldown(60);
    }
  };

  const handleResendEmail = async () => {
    if (resendCooldown > 0) return;

    const sent = await resendSignUpEmail(email);
    if (sent) {
      setResendCooldown(60);
    }
  };

  const handleProfileSave = async () => {
    clearError();
    clearNotice();
    await saveProfile({
      displayName,
      username,
      city,
      bio,
      birthDate: ""
    });
  };

  if (loading || !onboardingChecked) {
    return (
      <View style={styles.centeredPage}>
        <ActivityIndicator size="large" color={colors.accent} />
        <Text style={styles.loadingText}>Bialem hazırlanıyor...</Text>
      </View>
    );
  }

  if (user && profileComplete) {
    return <Redirect href="/(tabs)/feed" />;
  }

  if (!user && showOnboarding) {
    return (
      <WelcomeScreen
        onSelectMode={async (nextMode) => {
          await AsyncStorage.setItem(ONBOARDING_KEY, "true");
          setMode(nextMode);
          setShowOnboarding(false);
        }}
      />
    );
  }

  if (!user) {
    return (
      <KeyboardAvoidingView
        style={[styles.keyboardPage, { backgroundColor: authPalette.page }]}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
      <ScrollView
        contentContainerStyle={[styles.page, { backgroundColor: authPalette.page }]}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="none"
      >
        <View style={[styles.hero, { backgroundColor: authPalette.surface, borderColor: authPalette.border, shadowColor: authPalette.ink }]}>
          <View style={styles.authBrandRow}>
            <View style={styles.authLogoFrame}>
              <Image source={imageSources.logo} style={styles.authLogo} resizeMode="cover" />
            </View>
            <View>
              <Text style={styles.kicker}>Bialem</Text>
              <Text style={[styles.brandPromise, { color: authPalette.muted }]}>Birlikte daha fazlası</Text>
            </View>
          </View>
          <Text style={[styles.title, { color: authPalette.ink }]}>Topluluğunu kur, üyelerini bul, etkinliklerini güvenle yönet.</Text>
          <Text style={[styles.description, { color: authPalette.muted }]}>
            İlgi alanlarına uygun etkinlikleri keşfet, güvenilir topluluklara katıl ve birlikte güzel anılar biriktir.
          </Text>
        </View>

        <View style={[styles.authSwitch, { backgroundColor: authPalette.accentSoft }]}>
          <Pressable
            style={[
              styles.switchButton,
              mode === "signin" && styles.switchButtonActive,
              mode === "signin" && { backgroundColor: authPalette.surface, shadowColor: authPalette.accent }
            ]}
            onPress={() => setMode("signin")}
          >
            <Text style={[styles.switchText, { color: mode === "signin" ? authPalette.ink : authPalette.muted }]}>Giriş Yap</Text>
          </Pressable>
          <Pressable
            style={[
              styles.switchButton,
              mode === "signup" && styles.switchButtonActive,
              mode === "signup" && { backgroundColor: authPalette.surface, shadowColor: authPalette.accent }
            ]}
            onPress={() => setMode("signup")}
          >
            <Text style={[styles.switchText, { color: mode === "signup" ? authPalette.ink : authPalette.muted }]}>Kayıt Ol</Text>
          </Pressable>
        </View>

        <View style={[styles.card, { backgroundColor: authPalette.surface, borderColor: authPalette.border, shadowColor: authPalette.ink }]}>
          <Text style={[styles.cardTitle, { color: authPalette.ink }]}>{mode === "signin" ? "Hoş geldiniz" : "Yeni hesap oluştur"}</Text>

          {error ? <Text style={styles.errorText}>{error}</Text> : null}
          {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}

          {mode === "signup" && notice ? (
            <Pressable
              style={[styles.resendButton, resendCooldown > 0 && styles.resendButtonDisabled]}
              onPress={() => void handleResendEmail()}
              disabled={resendCooldown > 0}
            >
              <Text style={[styles.resendButtonText, resendCooldown > 0 && styles.resendButtonTextDisabled]}>
                {resendCooldown > 0
                  ? `Tekrar gönder (${resendCooldown} sn)`
                  : "Doğrulama e-postasını tekrar gönder"}
              </Text>
            </Pressable>
          ) : null}

          <Field label="E-posta" value={email} onChangeText={setEmail} placeholder="ornek@eposta.com" />
          <Field
            label="Şifre"
            value={password}
            onChangeText={setPassword}
            placeholder={mode === "signup" ? "Min. 8, büyük/küçük harf + rakam" : "En az 8 karakter"}
            secureTextEntry={!showPassword}
            showPasswordToggle
            passwordVisible={showPassword}
            onTogglePasswordVisibility={() => setShowPassword((value) => !value)}
          />

          {mode === "signin" ? (
            <Pressable style={styles.forgotPasswordButton} onPress={() => router.push("/forgot-password")}>
              <Text style={[styles.forgotPasswordText, { color: authPalette.accent }]}>Şifremi Unuttum</Text>
            </Pressable>
          ) : null}

          {mode === "signup" ? (
            <>
              <Field label="Görünen ad" value={displayName} onChangeText={setDisplayName} placeholder="Adınız Soyadınız" />
              <Field label="Kullanıcı adı" value={username} onChangeText={setUsername} placeholder="örnek_kullanıcı" />
              <View style={[styles.legalBox, { backgroundColor: authPalette.page, borderColor: authPalette.border }]}>
                <Text style={[styles.legalTitle, { color: authPalette.ink }]}>Yasal onaylar</Text>
                <ConsentRow
                  checked={approvePrivacy}
                  label="Gizlilik Politikası metnini okudum ve kabul ediyorum."
                  onPress={() => setApprovePrivacy((value) => !value)}
                />
                <ConsentRow
                  checked={approveTerms}
                  label="Kullanım Şartları metnini okudum ve kabul ediyorum."
                  onPress={() => setApproveTerms((value) => !value)}
                />
                <ConsentRow
                  checked={approveKvkk}
                  label="KVKK kapsamında aydınlatma metnini okudum ve veri işleme sürecini onaylıyorum."
                  onPress={() => setApproveKvkk((value) => !value)}
                />
                <Text style={[styles.legalHint, { color: authPalette.muted }]}>
                  KVKK Aydınlatma Metni, Gizlilik Politikası ve Kullanım Şartları bağlantılarını inceleyebilirsiniz.
                </Text>
              </View>
            </>
          ) : null}

          {mode === "signup" && (!approvePrivacy || !approveTerms || !approveKvkk) ? (
            <Text style={styles.warningText}>Kaydı tamamlamak için üç onayı da işaretlemeniz gerekir.</Text>
          ) : null}

          <Pressable style={styles.primaryButton} onPress={() => void handleAuthSubmit()}>
            <Text style={styles.primaryButtonText}>{mode === "signin" ? "Giriş Yap" : "Kaydı Tamamla"}</Text>
          </Pressable>
        </View>
      </ScrollView>
      </KeyboardAvoidingView>
    );
  }

  return (
    <KeyboardAvoidingView
      style={styles.keyboardPage}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
    <ScrollView
      contentContainerStyle={styles.page}
      keyboardShouldPersistTaps="handled"
      keyboardDismissMode="none"
    >
      <View style={styles.hero}>
        <Text style={styles.kicker}>Profil Kurulumu</Text>
        <Text style={styles.title}>Topluluğa katılmadan önce profilinizi tamamlayın.</Text>
        <Text style={styles.description}>
          Kullanıcı adı ve görünen ad uygulamanın yorum, puanlama ve topluluk tarafında kullanılacak.
        </Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Profil bilgileri</Text>
        {error ? <Text style={styles.errorText}>{error}</Text> : null}
        {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}
        <Field
          label="Görünen ad"
          value={displayName || profile?.display_name || ""}
          onChangeText={setDisplayName}
          placeholder="Adınız Soyadınız"
        />
        <Field
          label="Kullanıcı adı"
          value={username || profile?.username || ""}
          onChangeText={setUsername}
          placeholder="örnek_kullanıcı"
        />
        <Field label="Şehir" value={city} onChangeText={setCity} placeholder="Ankara" />
        <Field label="Kısa biyografi" value={bio} onChangeText={setBio} placeholder="Topluluk sever, organizatör..." multiline />

        <Pressable style={styles.primaryButton} onPress={() => void handleProfileSave()}>
          <Text style={styles.primaryButtonText}>Profili Kaydet</Text>
        </Pressable>
        <Pressable style={styles.skipButton} onPress={() => router.replace("/(tabs)/feed")}>
          <Text style={styles.skipButtonText}>Daha sonra</Text>
        </Pressable>
      </View>
    </ScrollView>
    </KeyboardAvoidingView>
  );
}

function WelcomeScreen({ onSelectMode }: { onSelectMode: (mode: AuthMode) => Promise<void> }) {
  const [continuing, setContinuing] = useState(false);

  const continueToAuth = async (mode: AuthMode) => {
    setContinuing(true);
    await onSelectMode(mode);
  };

  return (
    <ScrollView contentContainerStyle={styles.welcomePage}>
      <View style={styles.welcomeBrand}>
        <View style={styles.welcomeLogoFrame}>
          <Image source={imageSources.logo} style={styles.welcomeLogo} resizeMode="cover" />
        </View>
        <View style={styles.welcomeBrandText}>
          <Text style={styles.welcomeBrandName}>BİALEM</Text>
          <Text style={styles.welcomeEyebrow}>Senin alemin, senin topluluğun</Text>
        </View>
      </View>

      <View style={styles.welcomeHero}>
        <Text style={styles.welcomeTitle}>İlgi alanını bul. İnsanlarla buluş. Anılarını paylaş.</Text>
        <Text style={styles.welcomeDescription}>
          Yakınındaki toplulukları ve etkinlikleri keşfet; sevdiğin şeyleri birlikte yapacağın insanlarla tanış.
        </Text>
      </View>

      <View style={styles.worldVisualCard}>
        <Image source={imageSources.onboardingWorlds} style={styles.worldVisual} resizeMode="cover" />
        <BialemHeroField intensity={0.85} />
        <View style={styles.worldLabels}>
          <CategoryPill label="Doğa" tone="orange" />
          <CategoryPill label="Spor" tone="cyan" />
          <CategoryPill label="Sanat" tone="violet" />
        </View>
      </View>

      <View style={styles.whySection}>
        <View style={styles.whyHeading}>
          <Text style={styles.whyKicker}>NEDEN BİALEM?</Text>
          <Text style={styles.whyTitle}>Hayatı ekrandan izlemek yerine birlikte yaşa.</Text>
          <Text style={styles.whyDescription}>
            Üyeliğin sana yalnızca bir profil değil, ilgi alanlarının etrafında gelişen gerçek bir sosyal çevre kazandırır.
          </Text>
        </View>

        <View style={styles.benefitList}>
          <BenefitCard
            number="01"
            title="Sana uygun olanı keşfet"
            text="Takip ettiğin insanlar ve ilgi alanlarına göre etkinlikleri öncelikli gör."
            tone="orange"
          />
          <BenefitCard
            number="02"
            title="Güvenle katıl"
            text="Katılımcı yorumları, puanlar ve yönetici onaylarıyla toplulukları daha yakından tanı."
            tone="cyan"
          />
          <BenefitCard
            number="03"
            title="Çevreni ve alemini büyüt"
            text="Yeni insanlarla tanış, kendi etkinlik fikrini paylaş ve güzel anlarını topluluğunla yaşat."
            tone="violet"
          />
        </View>
      </View>

      <View style={styles.welcomeFooter}>
        <View style={styles.welcomeProofRow}>
          <Text style={styles.welcomeProof}>Topluluklar</Text>
          <View style={styles.proofDot} />
          <Text style={styles.welcomeProof}>Etkinlikler</Text>
          <View style={styles.proofDot} />
          <Text style={styles.welcomeProof}>Yeni insanlar</Text>
        </View>
        <Pressable style={styles.welcomeButton} onPress={() => void continueToAuth("signup")} disabled={continuing}>
          <Text style={styles.welcomeButtonText}>{continuing ? "Hazırlanıyor..." : "Ücretsiz katıl"}</Text>
        </Pressable>
        <Pressable style={styles.signInLink} onPress={() => void continueToAuth("signin")} disabled={continuing}>
          <Text style={styles.signInLinkText}>Zaten hesabım var</Text>
        </Pressable>
        <Text style={styles.welcomeFinePrint}>Bir hesap oluşturarak topluluk kurallarını ve güvenlik ilkelerini kabul edersin.</Text>
      </View>
    </ScrollView>
  );
}

function BenefitCard({
  number,
  title,
  text,
  tone
}: {
  number: string;
  title: string;
  text: string;
  tone: "orange" | "cyan" | "violet";
}) {
  return (
    <View style={styles.benefitCard}>
      <View style={[styles.benefitNumber, styles[`benefitNumber_${tone}`]]}>
        <Text style={styles.benefitNumberText}>{number}</Text>
      </View>
      <View style={styles.benefitCopy}>
        <Text style={styles.benefitTitle}>{title}</Text>
        <Text style={styles.benefitText}>{text}</Text>
      </View>
    </View>
  );
}

function CategoryPill({ label, tone }: { label: string; tone: "orange" | "cyan" | "violet" }) {
  return (
    <View style={[styles.categoryPill, styles[`categoryPill_${tone}`]]}>
      <Text style={styles.categoryPillText}>{label}</Text>
    </View>
  );
}

type FieldProps = {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  placeholder: string;
  secureTextEntry?: boolean;
  multiline?: boolean;
  showPasswordToggle?: boolean;
  passwordVisible?: boolean;
  onTogglePasswordVisibility?: () => void;
};

function Field({
  label,
  multiline = false,
  showPasswordToggle = false,
  passwordVisible = false,
  onTogglePasswordVisibility,
  secureTextEntry,
  ...props
}: FieldProps) {
  const { resolvedTheme } = useTheme();
  const palette = authPalettes[resolvedTheme];

  return (
    <View style={styles.fieldGroup}>
      <Text style={[styles.fieldLabel, { color: palette.ink }]}>{label}</Text>
      <View style={styles.inputWrap}>
        <TextInput
          {...props}
          secureTextEntry={secureTextEntry}
          style={[
            styles.input,
            showPasswordToggle && styles.inputWithToggle,
            { backgroundColor: palette.surface, borderColor: palette.border, color: palette.ink },
            multiline && styles.textArea
          ]}
          autoCapitalize="none"
          multiline={multiline}
          placeholderTextColor={palette.muted}
        />
        {showPasswordToggle ? (
          <Pressable
            style={styles.eyeButton}
            onPress={onTogglePasswordVisibility}
            accessibilityLabel={passwordVisible ? "Şifreyi gizle" : "Şifreyi göster"}
          >
            <Ionicons
              name={passwordVisible ? "eye-outline" : "eye-off-outline"}
              size={22}
              color={palette.muted}
            />
          </Pressable>
        ) : null}
      </View>
    </View>
  );
}

type ConsentRowProps = {
  checked: boolean;
  label: string;
  onPress: () => void;
};

function ConsentRow({ checked, label, onPress }: ConsentRowProps) {
  const { resolvedTheme } = useTheme();
  const palette = authPalettes[resolvedTheme];

  return (
    <Pressable style={styles.consentRow} onPress={onPress}>
      <View
        style={[
          styles.checkbox,
          { backgroundColor: palette.surface, borderColor: palette.border },
          checked && styles.checkboxChecked,
          checked && { backgroundColor: palette.accent, borderColor: palette.accent }
        ]}
      >
        {checked ? <Text style={styles.checkboxMark}>X</Text> : null}
      </View>
      <Text style={[styles.consentText, { color: palette.ink }]}>{label}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  keyboardPage: {
    flex: 1,
    backgroundColor: colors.page
  },
  welcomePage: {
    flexGrow: 1,
    minHeight: "100%",
    backgroundColor: colors.page,
    paddingHorizontal: 16,
    paddingTop: 24,
    paddingBottom: 22,
    gap: 16
  },
  welcomeBrand: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12
  },
  welcomeLogoFrame: {
    width: 56,
    height: 56,
    borderRadius: 18,
    overflow: "hidden",
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  welcomeLogo: {
    width: "100%",
    height: "100%"
  },
  welcomeBrandText: {
    flex: 1,
    gap: 3
  },
  welcomeBrandName: {
    color: colors.ink,
    fontSize: 21,
    fontWeight: "900",
    letterSpacing: 2.4
  },
  welcomeEyebrow: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "800"
  },
  welcomeHero: {
    gap: 10
  },
  welcomeTitle: {
    color: colors.ink,
    fontSize: 28,
    lineHeight: 34,
    fontWeight: "900",
    letterSpacing: -0.8
  },
  welcomeDescription: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  worldVisualCard: {
    position: "relative",
    height: 238,
    overflow: "hidden",
    borderRadius: 22,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    shadowColor: colors.ink,
    shadowOpacity: 0.16,
    shadowRadius: 24,
    shadowOffset: { width: 0, height: 14 },
    elevation: 7
  },
  worldVisual: {
    width: "100%",
    height: "100%"
  },
  worldLabels: {
    position: "absolute",
    left: 14,
    right: 14,
    bottom: 14,
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 8
  },
  whySection: {
    gap: 13,
    padding: 16,
    borderRadius: 20,
    backgroundColor: colors.brandInk
  },
  whyHeading: {
    gap: 7
  },
  whyKicker: {
    color: colors.action,
    fontSize: 11,
    fontWeight: "900",
    letterSpacing: 1.5
  },
  whyTitle: {
    color: colors.onBrand,
    fontSize: 25,
    lineHeight: 30,
    fontWeight: "900",
    letterSpacing: -0.4
  },
  whyDescription: {
    color: "#b9c6e8",
    fontSize: 14,
    lineHeight: 21
  },
  benefitList: {
    gap: 10
  },
  benefitCard: {
    flexDirection: "row",
    gap: 12,
    padding: 13,
    borderRadius: 20,
    backgroundColor: "rgba(255,255,255,0.08)",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.12)"
  },
  benefitNumber: {
    width: 42,
    height: 42,
    borderRadius: 15,
    alignItems: "center",
    justifyContent: "center"
  },
  benefitNumber_orange: {
    backgroundColor: colors.action
  },
  benefitNumber_cyan: {
    backgroundColor: colors.aqua
  },
  benefitNumber_violet: {
    backgroundColor: colors.accent
  },
  benefitNumberText: {
    color: colors.ink,
    fontSize: 12,
    fontWeight: "900"
  },
  benefitCopy: {
    flex: 1,
    gap: 4
  },
  benefitTitle: {
    color: colors.onBrand,
    fontSize: 15,
    lineHeight: 20,
    fontWeight: "900"
  },
  benefitText: {
    color: "#b9c6e8",
    fontSize: 13,
    lineHeight: 19
  },
  categoryPill: {
    flex: 1,
    paddingVertical: 10,
    paddingHorizontal: 12,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.72)"
  },
  categoryPill_orange: {
    backgroundColor: "rgba(255,162,15,0.94)"
  },
  categoryPill_cyan: {
    backgroundColor: "rgba(25,200,238,0.94)"
  },
  categoryPill_violet: {
    backgroundColor: "rgba(123,53,255,0.94)"
  },
  categoryPillText: {
    color: colors.ink,
    textAlign: "center",
    fontSize: 13,
    fontWeight: "900"
  },
  welcomeFooter: {
    gap: 13
  },
  welcomeProofRow: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    flexWrap: "wrap",
    gap: 8
  },
  welcomeProof: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "800"
  },
  proofDot: {
    width: 5,
    height: 5,
    borderRadius: 3,
    backgroundColor: colors.action
  },
  welcomeButton: {
    backgroundColor: colors.action,
    borderRadius: 999,
    minHeight: 46,
    paddingVertical: 12,
    paddingHorizontal: 22,
    shadowColor: colors.action,
    shadowOpacity: 0.26,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: 8 },
    elevation: 5
  },
  welcomeButtonText: {
    color: colors.actionText,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "900"
  },
  signInLink: {
    alignSelf: "center",
    paddingHorizontal: 18,
    paddingVertical: 8
  },
  signInLinkText: {
    color: colors.accent,
    fontSize: 14,
    fontWeight: "900"
  },
  welcomeFinePrint: {
    color: colors.muted,
    textAlign: "center",
    fontSize: 12,
    lineHeight: 18
  },
  page: {
    flexGrow: 1,
    backgroundColor: colors.page,
    padding: 16,
    gap: 16
  },
  centeredPage: {
    flex: 1,
    backgroundColor: colors.page,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    padding: 24
  },
  loadingText: {
    color: colors.muted,
    fontSize: 16
  },
  hero: {
    marginTop: 18,
    gap: 10,
    padding: 18,
    borderRadius: 22,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
    shadowColor: colors.ink,
    shadowOpacity: 0.08,
    shadowRadius: 22,
    shadowOffset: { width: 0, height: 14 },
    elevation: 4
  },
  authBrandRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12
  },
  authLogoFrame: {
    width: 58,
    height: 58,
    borderRadius: 20,
    overflow: "hidden",
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  authLogo: {
    width: "100%",
    height: "100%"
  },
  brandPromise: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700",
    marginTop: 3
  },
  kicker: {
    color: colors.action,
    fontSize: 14,
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: 1.8
  },
  title: {
    color: colors.ink,
    fontSize: 28,
    lineHeight: 34,
    fontWeight: "900",
    letterSpacing: -0.6
  },
  description: {
    color: colors.muted,
    fontSize: 14,
    lineHeight: 21
  },
  authSwitch: {
    flexDirection: "row",
    backgroundColor: colors.accentSoft,
    borderRadius: 999,
    padding: 6,
    gap: 6
  },
  switchButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 999
  },
  switchButtonActive: {
    backgroundColor: colors.surface,
    shadowColor: colors.accent,
    shadowOpacity: 0.08,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2
  },
  switchText: {
    textAlign: "center",
    color: colors.muted,
    fontWeight: "700"
  },
  switchTextActive: {
    color: colors.ink
  },
  card: {
    backgroundColor: colors.surface,
    borderRadius: 20,
    padding: 16,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border,
    shadowColor: colors.ink,
    shadowOpacity: 0.05,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 12 },
    elevation: 3
  },
  cardTitle: {
    color: colors.ink,
    fontSize: 24,
    fontWeight: "900",
    letterSpacing: -0.3
  },
  errorText: {
    color: colors.danger,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600"
  },
  noticeText: {
    color: colors.accent,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600",
    backgroundColor: colors.accentSoft,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 10
  },
  resendButton: {
    alignSelf: "flex-start",
    borderBottomWidth: 1,
    borderBottomColor: colors.accent,
    paddingVertical: 4
  },
  resendButtonText: {
    color: colors.accent,
    fontSize: 14,
    fontWeight: "800"
  },
  resendButtonDisabled: {
    borderBottomColor: colors.muted
  },
  resendButtonTextDisabled: {
    color: colors.muted
  },
  warningText: {
    color: colors.danger,
    fontSize: 13,
    lineHeight: 19,
    fontWeight: "600"
  },
  forgotPasswordButton: {
    alignSelf: "flex-end",
    paddingVertical: 4
  },
  forgotPasswordText: {
    color: colors.accent,
    fontSize: 14,
    fontWeight: "800"
  },
  fieldGroup: {
    gap: 8
  },
  fieldLabel: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "700"
  },
  inputWrap: {
    position: "relative",
    justifyContent: "center"
  },
  input: {
    minHeight: 46,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    paddingHorizontal: 14,
    paddingVertical: 12,
    color: colors.ink,
    fontSize: 15
  },
  inputWithToggle: {
    paddingRight: 48
  },
  eyeButton: {
    position: "absolute",
    right: 12,
    height: 46,
    width: 36,
    alignItems: "center",
    justifyContent: "center"
  },
  textArea: {
    minHeight: 110,
    textAlignVertical: "top"
  },
  legalBox: {
    gap: 10,
    backgroundColor: colors.surfaceStrong,
    borderRadius: 18,
    padding: 14,
    borderWidth: 1,
    borderColor: colors.border
  },
  legalTitle: {
    color: colors.ink,
    fontSize: 15,
    fontWeight: "800"
  },
  legalHint: {
    color: colors.muted,
    fontSize: 13,
    lineHeight: 19
  },
  consentRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 10
  },
  checkbox: {
    width: 22,
    height: 22,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 1
  },
  checkboxChecked: {
    backgroundColor: colors.accent,
    borderColor: colors.accent
  },
  checkboxMark: {
    color: colors.actionText,
    fontSize: 12,
    fontWeight: "800"
  },
  consentText: {
    flex: 1,
    color: colors.ink,
    fontSize: 14,
    lineHeight: 20
  },
  primaryButton: {
    marginTop: 4,
    backgroundColor: colors.action,
    borderRadius: 999,
    minHeight: 46,
    paddingVertical: 11,
    paddingHorizontal: 18
  },
  primaryButtonText: {
    color: colors.actionText,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "900",
    letterSpacing: 0.2
  },
  skipButton: {
    marginTop: 6,
    minHeight: 44,
    paddingVertical: 11,
    paddingHorizontal: 18,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  skipButtonText: {
    color: colors.ink,
    textAlign: "center",
    fontSize: 14,
    fontWeight: "800"
  }
});
