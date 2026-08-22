export const spacing = { xxs:2, xs:4, sm:8, md:12, lg:16, xl:20, xxl:24, xxxl:32, huge:40, giant:48, section:64 } as const;
export const radius = { sm:8, md:12, lg:16, xl:24, full:999 } as const;
export const typography = {
  display:{fontSize:30,lineHeight:36,fontWeight:"700" as const}, pageTitle:{fontSize:23,lineHeight:29,fontWeight:"700" as const},
  sectionTitle:{fontSize:19,lineHeight:25,fontWeight:"600" as const}, cardTitle:{fontSize:17,lineHeight:23,fontWeight:"600" as const},
  body:{fontSize:16,lineHeight:24,fontWeight:"400" as const}, secondary:{fontSize:14,lineHeight:20,fontWeight:"400" as const},
  caption:{fontSize:12,lineHeight:17,fontWeight:"500" as const}
} as const;
export const sizes = { touch:44, input:48, header:56, avatarXs:24, avatarSm:32, avatarMd:40, avatarLg:48, avatarXl:64, avatarProfile:96 } as const;
export const motion = { fast:160, normal:220, page:260 } as const;
export const layers = { base:0, sticky:10, dropdown:30, overlay:100, modal:200, toast:300 } as const;
