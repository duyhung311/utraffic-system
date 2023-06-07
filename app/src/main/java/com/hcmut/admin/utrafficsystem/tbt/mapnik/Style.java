package com.hcmut.admin.utrafficsystem.tbt.mapnik;

import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.CombinedSymMeta;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;

import java.util.ArrayList;
import java.util.List;

public class Style {
    public final String name;
    private String layerName;
    private final List<Rule> rules = new ArrayList<>();
    private boolean hasText = false;

    public Style(String name) {
        this.name = name;
    }

    public CombinedSymMeta toDrawable(Way way) {
        CombinedSymMeta combinedSymMeta = new CombinedSymMeta();
        for (Rule rule : rules) {
            combinedSymMeta = (CombinedSymMeta) combinedSymMeta.append(rule.toDrawable(way, layerName));
        }
        return combinedSymMeta;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        if (rule.hasTextSymbolizer()) {
            hasText = true;
        }
    }

    public boolean hasTextSymbolizer() {
        return hasText;
    }
}
