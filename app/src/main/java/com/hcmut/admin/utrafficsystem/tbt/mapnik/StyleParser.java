package com.hcmut.admin.utrafficsystem.tbt.mapnik;

import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.LineSymbolizer;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.PolygonSymbolizer;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.TextSymbolizer;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StyleParser {
    private final int resource;
    private final Config config;
    private final XmlPullParser xpp;
    public final List<Layer> layers = new ArrayList<>(78);

    public StyleParser(Config config, int resource) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp = factory.newPullParser();
        this.resource = resource;
        this.config = config;
    }

    public void read() {
        try {
            InputStream in_s = config.context.getResources().openRawResource(resource);
            xpp.setInput(in_s, null);

            int eventType = xpp.getEventType();
            HashMap<String, Style> stylesMap = new HashMap<>();
            Layer layerParent = null;
            Style styleParent = null;
            Rule ruleParent = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = xpp.getName();
                if (eventType == XmlPullParser.START_TAG) {
                    switch (name) {
                        case "Layer":
                            layerParent = new Layer(config, xpp.getAttributeValue(null, "name"));
                            break;
                        case "StyleName":
                            assert layerParent != null;
                            layerParent.addStyleName(xpp.nextText());
                            break;
                        case "Style":
                            styleParent = new Style(xpp.getAttributeValue(null, "name"));
                            break;
                        case "Rule":
                            ruleParent = new Rule(config);
                            break;
                        case "MaxScaleDenominator":
                            if (ruleParent == null) break;
                            ruleParent.setMaxScaleDenominator(Float.parseFloat(xpp.nextText()));
                            break;
                        case "MinScaleDenominator":
                            if (ruleParent == null) break;
                            ruleParent.setMinScaleDenominator(Float.parseFloat(xpp.nextText()));
                            break;
                        case "Filter":
                            if (ruleParent == null) break;
                            ruleParent.setFilter(xpp.nextText());
                            break;
                        case "LineSymbolizer":
                            if (ruleParent == null) break;
                            // LineSymbolizer keys: [stroke-opacity, offset, stroke-linejoin, stroke-dasharray, stroke-width, stroke, clip, stroke-linecap]
                            String stroke = xpp.getAttributeValue(null, "stroke");
                            String strokeWidth = xpp.getAttributeValue(null, "stroke-width");
                            String strokeOpacity = xpp.getAttributeValue(null, "stroke-opacity");
                            String strokeLinecap = xpp.getAttributeValue(null, "stroke-linecap");
                            String strokeLinejoin = xpp.getAttributeValue(null, "stroke-linejoin");
                            String strokeDasharray = xpp.getAttributeValue(null, "stroke-dasharray");
                            String offset = xpp.getAttributeValue(null, "offset");

                            ruleParent.addSymbolizer(new LineSymbolizer(
                                    config,
                                    strokeWidth,
                                    stroke,
                                    strokeDasharray,
                                    strokeLinecap,
                                    strokeLinejoin,
                                    strokeOpacity,
                                    offset
                            ));
                            break;
                        case "PolygonSymbolizer":
                            if (ruleParent == null) break;
                            // PolygonSymbolizer keys: [fill, fill-opacity, gamma, clip]
                            String fill = xpp.getAttributeValue(null, "fill");
                            String fillOpacity = xpp.getAttributeValue(null, "fill-opacity");
                            ruleParent.addSymbolizer(new PolygonSymbolizer(config, fill, fillOpacity));
                            break;
                        case "TextSymbolizer":
                            if (ruleParent == null) break;
                            /*
                             * textName
                             * dx
                             * dy
                             * spacing
                             * repeatDistance
                             * maxCharAngleDelta
                             * fill
                             * opacity
                             * placement
                             * verticalAlignment
                             * horizontalAlignment
                             * justifyAlignment
                             * wrapWidth
                             * fontsetName
                             * size
                             * haloFill
                             * haloRadius
                             * */
                            String dx = xpp.getAttributeValue(null, "dx");
                            String dy = xpp.getAttributeValue(null, "dy");
                            String margin = xpp.getAttributeValue(null, "margin");
                            String spacing = xpp.getAttributeValue(null, "spacing");
                            String repeatDistance = xpp.getAttributeValue(null, "repeatDistance");
                            String maxCharAngleDelta = xpp.getAttributeValue(null, "maxCharAngleDelta");
                            String fill1 = xpp.getAttributeValue(null, "fill");
                            String opacity = xpp.getAttributeValue(null, "opacity");
                            String placement = xpp.getAttributeValue(null, "placement");
                            String verticalAlignment = xpp.getAttributeValue(null, "verticalAlignment");
                            String horizontalAlignment = xpp.getAttributeValue(null, "horizontalAlignment");
                            String justifyAlignment = xpp.getAttributeValue(null, "justifyAlignment");
                            String wrapWidth = xpp.getAttributeValue(null, "wrapWidth");
                            String fontsetName = xpp.getAttributeValue(null, "fontsetName");
                            String size = xpp.getAttributeValue(null, "size");
                            String haloFill = xpp.getAttributeValue(null, "haloFill");
                            String haloRadius = xpp.getAttributeValue(null, "haloRadius");
                            String textExpr = xpp.nextText();
                            ruleParent.addSymbolizer(new TextSymbolizer(
                                    config,
                                    textExpr,
                                    dx,
                                    dy,
                                    margin,
                                    spacing,
                                    repeatDistance,
                                    maxCharAngleDelta,
                                    fill1,
                                    opacity,
                                    placement,
                                    verticalAlignment,
                                    horizontalAlignment,
                                    justifyAlignment,
                                    wrapWidth,
                                    fontsetName,
                                    size,
                                    haloFill,
                                    haloRadius
                            ));
                            break;
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    switch (name) {
                        case "Rule":
                            assert ruleParent != null && styleParent != null;
                            if (ruleParent.compareScaleDenominator(config.getScaleDenominator())) {
                                styleParent.addRule(ruleParent);
                            }
                            ruleParent = null;
                            break;
                        case "Style":
                            assert styleParent != null;
                            stylesMap.put(styleParent.name, styleParent);
                            styleParent = null;
                            break;
                        case "Layer":
                            assert layerParent != null;
                            layerParent.validateStyles(stylesMap);
                            layers.add(layerParent);
                            layerParent = null;
                            break;
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
