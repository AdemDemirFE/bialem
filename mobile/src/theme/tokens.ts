export const spacing = { xxs:2, xs:4, sm:8, md:12, lg:16, xl:20, xxl:24, xxxl:32, huge:40, giant:48, section:64 } as const;
export const radius = { sm:8, md:12, lg:16, xl:24, full:999 } as const;
export const fontFamily = { base: '"Gilroy", Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif' } as const;
export const fontWeight = { light:"300" as const, regular:"400" as const, medium:"500" as const, bold:"700" as const, heavy:"900" as const };
export const typography = {
  display:{fontFamily:fontFamily.base,fontSize:30,lineHeight:36,fontWeight:"700" as const}, pageTitle:{fontFamily:fontFamily.base,fontSize:23,lineHeight:29,fontWeight:"700" as const},
  sectionTitle:{fontFamily:fontFamily.base,fontSize:19,lineHeight:25,fontWeight:"600" as const}, cardTitle:{fontFamily:fontFamily.base,fontSize:17,lineHeight:23,fontWeight:"600" as const},
  body:{fontFamily:fontFamily.base,fontSize:16,lineHeight:24,fontWeight:"400" as const}, secondary:{fontFamily:fontFamily.base,fontSize:14,lineHeight:20,fontWeight:"400" as const},
  caption:{fontFamily:fontFamily.base,fontSize:12,lineHeight:17,fontWeight:"500" as const}
} as const;
export const sizes = { touch:44, input:48, header:56, avatarXs:24, avatarSm:32, avatarMd:40, avatarLg:48, avatarXl:64, avatarProfile:96 } as const;
export const motion = { fast:160, normal:220, page:260, slow:320, stagger:45 } as const;
export const layers = { base:0, sticky:10, dropdown:30, overlay:100, modal:200, toast:300 } as const;
export const shadow = {
  card: "0 8px 24px rgba(10, 24, 51, 0.08)",
  cardStrong: "0 14px 34px rgba(10, 24, 51, 0.14)",
  button: "0 8px 18px rgba(246, 165, 28, 0.32)",
  buttonAccent: "0 8px 18px rgba(112, 71, 215, 0.30)",
  tab: "0 -8px 24px rgba(10, 24, 51, 0.10)",
} as const;
export const gradient = {
  brand: "linear-gradient(135deg, #1A1048 0%, #2A1878 45%, #12375a 100%)",
  action: "linear-gradient(135deg, #f6a51c 0%, #ff8a3d 100%)",
  accent: "linear-gradient(135deg, #7047d7 0%, #1699b8 100%)",
  heroOverlay: "linear-gradient(180deg, rgba(7,11,24,0) 30%, rgba(7,11,24,0.55) 100%)",
} as const;
export const blur = { card: 12, header: 16, sheet: 8 } as const;
