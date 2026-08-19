import type { ImageSourcePropType } from "react-native";
import natureOutdoor from "../../assets/community-covers/nature-outdoor.jpg";
import cultureArt from "../../assets/community-covers/culture-art.jpg";
import sportsCompetition from "../../assets/community-covers/sports-competition.jpg";
import tabletopGames from "../../assets/community-covers/tabletop-games.jpg";
import eveningEntertainment from "../../assets/community-covers/evening-entertainment.png";
import gastronomy from "../../assets/community-covers/gastronomy.png";
import kizNesesi from "../../assets/community-covers/kiz-nesesi.png";

const communityCovers: Record<string, ImageSourcePropType> = {
  "doga-acik-hava": { uri: natureOutdoor },
  "kultur-sanat": { uri: cultureArt },
  "spor-rekabet": { uri: sportsCompetition },
  "masa-zeka-oyunlari": { uri: tabletopGames },
  "aksam-eglencesi": { uri: eveningEntertainment },
  gastronomi: { uri: gastronomy },
  "kiz-nesesi": { uri: kizNesesi }
};

export function getCommunityCover(slug: string, remoteUrl?: string | null): ImageSourcePropType | null {
  if (remoteUrl) return { uri: remoteUrl };
  return communityCovers[slug] ?? null;
}
