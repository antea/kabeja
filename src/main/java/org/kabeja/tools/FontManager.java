/*
Copyright 2005 Simon Mieth

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.kabeja.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.kabeja.dxf.DXFStyle;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class FontManager {

    private static FontManager instance = new FontManager();
    private String fontDescription = "conf/font.properties";
    private Map<String, String> fontProperties = new HashMap<String, String>();
    private Map<String, String> replaceTable = new HashMap<String, String>();

    private FontManager() {
        loadFontDescription();
    }

    public void setFontDescription(String file) {
        this.fontDescription = file;
        loadFontDescription();
    }

    private void loadFontDescription() {
        fontProperties.clear();

        try {
            InputStream stream = this.getClass().getResourceAsStream(this.fontDescription);

            if (stream == null) {
                try {
                    stream = new FileInputStream(this.fontDescription);
                } catch (FileNotFoundException e1) {
                }
            }

            if (stream != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        stream));
                String line = null;

                while ((line = in.readLine()) != null) {
                    int index = line.indexOf("=");

                    if (index >= 0) {
                        String font = line.substring(0, index).trim().toLowerCase();
                        String svgFont = line.substring(index + 1).trim();
                        fontProperties.put(font, svgFont);
                    }
                }
            } else {
                // System.out.println("no font.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FontManager getInstance() {
        return instance;
    }

    /**
     * Query if a SVG font description exists for the given shx/ttf font.
     *
     * @param fontName
     *            The font or font
     * @return svg font name (may differ from fontName) of null if none matches
     */
    public String hasFontDescription(String fontName) {
        if (StringUtils.isEmpty(fontName)) {
            return null;
        }

        if (fontProperties.containsKey(fontName)) {
            return fontName;
        }

        String alternativeFont = doEducatedGuess(fontName);
        if (alternativeFont != null) {
            return alternativeFont;
        }

        alternativeFont = doReplacement(fontName);
        if (alternativeFont != null) {
            return alternativeFont;
        }

        return null;
    }

    public String getFontDescription(String svgFont) {
        return fontProperties.get(svgFont);
    }

    /**
     * Tries to match font name by:
     *  stripping off .ttf & .shx suffix,
     *  lowercase the name,
     *  remore _ trailing chars
     * 
     * @param font
     * @return 
     */
    private String doEducatedGuess(String font) {
        font = font.toLowerCase();
        if (fontProperties.containsKey(font)) {
            return font;
        }

        if (font.endsWith(".shx") || font.endsWith(".ttf")) {
            font = font.substring(0, font.length() - 4);
            if (fontProperties.containsKey(font)) {
                return font;
            }
        }

        Matcher matcher = UNDERSCORE_ENDING_PATTERN.matcher(font);
        if (matcher.matches()) {
            font = matcher.group(1);
            if (fontProperties.containsKey(font)) {
                return font;
            }
        }
        return null;
    }
    private Pattern UNDERSCORE_ENDING_PATTERN = Pattern.compile("([^_]*)(_*)",
            Pattern.CASE_INSENSITIVE);

    private String doReplacement(String font) {
        String replacement = null;
        if (replaceTable.containsKey(font)) {
            replacement = replaceTable.get(font);
            // maybe null
            if (fontProperties.get(replacement) != null) {
                System.out.println("Trovato sostituto font '" + font + "' -> '" + replacement + "'");
            } else {
                System.err.println("Il sostituto del font '" + font + "' -> '" + replacement + "' non è presente");
            }
        }
        return replacement;
    }

    public synchronized void addReplacement(String from, String to) {
        replaceTable.put(from, to);
    }

    public synchronized void clearReplacements() {
        replaceTable.clear();
    }

    public String getFontDescriptionFromStyle(DXFStyle style) {
        if (StringUtils.isBlank(style.getBigFontFile()) || StringUtils.isBlank(style.getFontFile())) {
            return null;
        }
        String fontID = null;
        if ((fontID = hasFontDescription(style.getBigFontFile())) != null) {
            return fontID;
        } else if ((fontID = hasFontDescription(style.getFontFile())) != null) {
            return fontID;
        } else {
            System.err.println("Font non trovato: big -> '" + style.getBigFontFile() + "' normal -> '" + style.getFontFile() + "'");
        }
        return null;
    }
}
