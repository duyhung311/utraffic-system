package com.hcmut.admin.utrafficsystem.tbt.mapnik;

import android.annotation.SuppressLint;

import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.CombinedSymMeta;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

@SuppressLint("NewApi")
public class Layer {
    public final String name;
    private final Config config;
    private final List<String> stylesNames = new ArrayList<>();
    private final List<Style> styles = new ArrayList<>();
    private boolean hasText = false;
    private final Map<Long, CombinedSymMeta> symMetaMap = new HashMap<>();
    private Map<Long, CombinedSymMeta> drawingSymMetasMap = null;
    private final HashMap<Long, ExistedWay> existedWays = new HashMap<>();
    private static class ExistedWay {
        public final Way way;
        public final List<Long> tileIds = new ArrayList<>();

        public ExistedWay(Way way, long tileId) {
            this.way = way;
            this.tileIds.add(tileId);
        }
    }

    public Layer(Config config, String name) {
        this.name = name;
        this.config = config;
    }

    public void addStyleName(String name) {
        stylesNames.add(name);
    }

    public void validateStyles(HashMap<String, Style> stylesMap) {
        for (String name : stylesNames) {
            Style style = stylesMap.get(name);
            assert style != null : "Style " + name + " not found";
            style.setLayerName(this.name);
            if (style.hasTextSymbolizer()) {
                hasText = true;
            }
            styles.add(style);
        }
    }

    public void addWays(List<Way> ways, long tileId) {
        if (ways.isEmpty()) {
            return;
        }

        List<Way> newWays = new ArrayList<>(ways.size());
        for (Way way : ways) {
            ExistedWay existedWay = existedWays.get(way.id);
            if (existedWay != null) {
                existedWay.tileIds.add(tileId);
            } else {
                existedWays.put(way.id, new ExistedWay(way, tileId));
                newWays.add(way);
            }
        }

        CombinedSymMeta combinedSymMeta = new CombinedSymMeta();
        HashMap<Long, CombinedSymMeta> waysSymMetasMap = new HashMap<>(newWays.size());
        TreeMap<Integer, Long> waysOrder = new TreeMap<>();
        for (Style style : styles) {
            for (Way way : newWays) {
                CombinedSymMeta curCombinedSymMeta = style.toDrawable(way);
                waysSymMetasMap.put(way.id, curCombinedSymMeta);
                int order = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(way.tags.get(name)).get("postgisOrder")));
                waysOrder.put(order, way.id);
            }

            for (long wayId : waysOrder.values()) {
                combinedSymMeta = (CombinedSymMeta) combinedSymMeta.append(waysSymMetasMap.get(wayId));
            }
        }
        combinedSymMeta.save(config);
        symMetaMap.put(tileId, combinedSymMeta);
    }

    public void removeWays(Set<Long> tileIds) {
        if (tileIds.isEmpty() || symMetaMap.isEmpty()) {
            return;
        }

        for (Long tileId : tileIds) {
            symMetaMap.remove(tileId);
            List<Long> removedWays = new ArrayList<>();
            for (Map.Entry<Long, ExistedWay> entry : existedWays.entrySet()) {
                ExistedWay existedWay = entry.getValue();
                if (existedWay.tileIds.contains(tileId)) {
                    removedWays.add(entry.getKey());
                }
            }
            for (Long wayId : removedWays) {
                existedWays.remove(wayId);
            }
        }
    }

    public void save() {
        drawingSymMetasMap = new HashMap<>(symMetaMap);
    }

    public void draw() {
        if (drawingSymMetasMap == null) return;
        Map<Long, CombinedSymMeta> drawingSymMetasMap = this.drawingSymMetasMap;

        for (CombinedSymMeta combinedSymMeta : drawingSymMetasMap.values()) {
            combinedSymMeta.draw();
        }
    }

    public boolean hasTextSymbolizer() {
        return hasText;
    }
}
