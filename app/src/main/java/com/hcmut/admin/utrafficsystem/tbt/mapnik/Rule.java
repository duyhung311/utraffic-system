package com.hcmut.admin.utrafficsystem.tbt.mapnik;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.CombinedSymMeta;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.SymMeta;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.Symbolizer;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.TextSymbolizer;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class Rule {
    private Float maxScaleDenominator;
    private Float minScaleDenominator;
    private Function<HashMap<String, String>, Boolean> filter;
    private final List<Symbolizer> symbolizers = new ArrayList<>(4);
    private final Config config;
    private String filterString;

    public Rule(Config config, Float maxScaleDenominator, Float minScaleDenominator, String filter) {
        this.config = config;
        this.maxScaleDenominator = maxScaleDenominator;
        this.minScaleDenominator = minScaleDenominator;
        this.filter = createFilterFunction(filter);
        this.filterString = filter;
    }

    public Rule(Config config) {
        this(config, null, null, null);
    }

    public void setMaxScaleDenominator(Float maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    public void setMinScaleDenominator(Float minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public void setFilter(String filter) {
        this.filter = createFilterFunction(filter);
        this.filterString = filter;
    }

    public void addSymbolizer(Symbolizer symbolizer) {
        this.symbolizers.add(symbolizer);
    }

    public boolean compareScaleDenominator(float scaleDenominator) {
        if (this.maxScaleDenominator != null && scaleDenominator > this.maxScaleDenominator) {
            return false;
        }
        if (this.minScaleDenominator != null && scaleDenominator < this.minScaleDenominator) {
            return false;
        }
        return true;
    }

    public CombinedSymMeta toDrawable(Way way, String layerName) {
        float scaleDenominator = config.getScaleDenominator();
        if (!compareScaleDenominator(scaleDenominator)) {
            return null;
        }

        HashMap<String, String> tags = new HashMap<>(Objects.requireNonNull(way.tags.get(layerName)));
        boolean isMatched = this.filter == null || this.filter.apply(tags);
        if (isMatched) {
            List<SymMeta> symMetas = new ArrayList<>(symbolizers.size());
            for (Symbolizer symbolizer : symbolizers) {
                try {
                    symMetas.add(symbolizer.toDrawable(way, layerName));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error drawing way " + way.id);
                    System.err.println("With symbolizer " + symbolizer);
                }
            }
            return new CombinedSymMeta(symMetas);
        }
        return null;
    }

    public static Function<HashMap<String, String>, Boolean> createFilterFunction(String filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }
        // Pattern to match conditions in the filter string
        Pattern conditionPattern = Pattern.compile("\\[(\\w+)]\\s*([!=<>]+)\\s*('[^']+'|\\d+)");
        Matcher conditionMatcher = conditionPattern.matcher(filter);

        // Extract conditions and operators
        List<String> keys = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        List<String> values = new ArrayList<>();

        while (conditionMatcher.find()) {
            keys.add(conditionMatcher.group(1));
            operators.add(conditionMatcher.group(2));
            values.add(conditionMatcher.group(3));
        }

        // Return a function that checks if the input HashMap satisfies the filter
        return (HashMap<String, String> tags) -> {
            if (tags == null || keys.size() != operators.size() || keys.size() != values.size()) {
                return false;
            }

            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String operator = operators.get(i);
                String value = values.get(i).replace("'", "");

                String keyVal = tags.get(key);

                if (keyVal == null) {
                    keyVal = "null";
                }

                switch (operator) {
                    case "=":
                        if (!keyVal.equals(value)) {
                            return false;
                        }
                        break;
                    case "!=":
                        if (keyVal.equals(value)) {
                            return false;
                        }
                        break;
                    case ">":
                        if (keyVal.equals("null") || Float.parseFloat(keyVal) <= Float.parseFloat(value)) {
                            return false;
                        }
                        break;
                    case "<":
                        if (keyVal.equals("null") || Float.parseFloat(keyVal) >= Float.parseFloat(value)) {
                            return false;
                        }
                        break;
                    case ">=":
                        if (keyVal.equals("null") || Float.parseFloat(keyVal) < Float.parseFloat(value)) {
                            return false;
                        }
                        break;
                    case "<=":
                        if (keyVal.equals("null") || Float.parseFloat(keyVal) > Float.parseFloat(value)) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
            }
            return true;
        };
    }

    public boolean hasTextSymbolizer() {
        for (Symbolizer symbolizer : symbolizers) {
            if (symbolizer instanceof TextSymbolizer) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rule{").append('\n');
        sb.append("\tfilter=`").append(filterString).append('`').append('\n');
        sb.append("\tmaxScaleDenominator=`").append(maxScaleDenominator).append('`').append('\n');
        sb.append("\tminScaleDenominator=`").append(minScaleDenominator).append('`').append('\n');
        for (Symbolizer symbolizer : symbolizers) {
            sb.append("\t").append(symbolizer.toString()).append('\n');
        }
        sb.append('}');
        return sb.toString();
    }
}
