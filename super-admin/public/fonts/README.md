# Gilroy Font Files

Place the following `.woff2` files in this directory:

```
fonts/
├── Gilroy-Regular.woff2      (weight: 400)
├── Gilroy-Medium.woff2       (weight: 500)
├── Gilroy-SemiBold.woff2     (weight: 600)
├── Gilroy-Bold.woff2         (weight: 700)
└── Gilroy-ExtraBold.woff2    (weight: 800)
```

## How to Get Gilroy

1. **Free (limited):** https://www.cufonfonts.com/en/font-family/gilroy-83422
2. **Premium (full family):** https://creativemarket.com/Roundfont/2009458-Gilroy-Font-Family
3. **License:** Gilroy is a commercial font. You need a valid license for production use.

## Conversion to WOFF2

If you have TTF/OTF files, convert them to WOFF2:

```bash
# Using fonttools (pip install fonttools brotli)
python -c "
from fontTools.ttLib import TTFont
from fontTools.subset import Subsetter, Options
import brotli

for weight, src in [
    ('Regular', 'Gilroy-Regular.ttf'),
    ('Medium', 'Gilroy-Medium.ttf'),
    ('SemiBold', 'Gilroy-SemiBold.ttf'),
    ('Bold', 'Gilroy-Bold.ttf'),
    ('ExtraBold', 'Gilroy-ExtraBold.ttf'),
]:
    font = TTFont(src)
    font.flavor = 'woff2'
    font.save(f'Gilroy-{weight}.woff2')
    print(f'✓ Gilroy-{weight}.woff2')
"
```

Or use an online converter: https://transfonter.org/

## Fallback

If Gilroy fonts are not present, the app gracefully falls back to **Inter** (loaded from Google Fonts) which provides excellent readability for admin UI.
