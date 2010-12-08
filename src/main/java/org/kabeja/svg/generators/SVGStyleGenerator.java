/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.svg.generators;

import java.util.HashMap;
import java.util.Map;

import org.kabeja.dxf.DXFStyle;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.kabeja.tools.FontManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGStyleGenerator {
    private static Map<String, String> replaceTable = new HashMap<String, String>();

    public static void addReplacement(String from, String to) {
        replaceTable.put(from, to);
    }

    public static void clearReplacements() {
        replaceTable.clear();
    }

    /*
    * (non-Javadoc)
    *
    * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
    */
    public static void toSAX(ContentHandler handler, Map svgContext,
        DXFStyle style) throws SAXException {
        FontManager manager = FontManager.getInstance();

        if (manager.hasFontDescription(style.getBigFontFile())) {
            generateSAXFontDescription(handler, style.getBigFontFile());
        } else if (manager.hasFontDescription(style.getFontFile())) {
            generateSAXFontDescription(handler, style.getFontFile());
        } else if (replaceTable.containsKey(style.getFontFile())
                && manager.hasFontDescription(replaceTable.get(style.getFontFile()))) {
            String font = replaceTable.get(style.getFontFile());
            generateSAXFontDescription(handler, font);
            System.err.println("Rimpiazzato font " + style.getFontFile() +" con " + font);
        } else {
            System.err.println("Font non trovato: " + style.getBigFontFile() + "/" + style.getFontFile());
        }
    }

    protected static void generateSAXFontDescription(ContentHandler handler,
        String font) throws SAXException {
        font = font.toLowerCase();

        if (font.endsWith(".shx")) {
            font = font.substring(0, font.indexOf(".shx"));
        }

        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY, font);

        SVGUtils.startElement(handler, SVGConstants.SVG_FONT_FACE, attr);
        attr = new AttributesImpl();
        SVGUtils.startElement(handler, SVGConstants.SVG_FONT_FACE_SRC, attr);

        attr = new AttributesImpl();

        String url = FontManager.getInstance().getFontDescription(font) + "#" +
            font;
        attr.addAttribute("", "", SVGConstants.XMLNS_XLINK,
            SVGUtils.DEFAUL_ATTRIBUTE_TYPE, SVGConstants.XLINK_NAMESPACE);
        attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href", SVGConstants.XLINK_HREF,
            SVGUtils.DEFAUL_ATTRIBUTE_TYPE, url);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_FONT_FACE_URI, attr);
        SVGUtils.endElement(handler, SVGConstants.SVG_FONT_FACE_SRC);
        SVGUtils.endElement(handler, SVGConstants.SVG_FONT_FACE);
    }
}
