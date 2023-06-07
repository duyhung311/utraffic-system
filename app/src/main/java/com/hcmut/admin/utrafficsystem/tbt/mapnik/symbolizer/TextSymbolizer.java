package com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.algorithm.StrokeGenerator;
import com.hcmut.admin.utrafficsystem.tbt.data.VertexArray;
import com.hcmut.admin.utrafficsystem.tbt.geometry.LineStrip;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.PointList;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Polygon;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Triangle;
import com.hcmut.admin.utrafficsystem.tbt.geometry.TriangleStrip;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.programs.TextSymbShaderProgram;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressLint("NewApi")
public class TextSymbolizer extends Symbolizer {
    static class TextDrawable {
        public static final float DEFAULT_FONT_SIZE = 10;
        private static final Paint PAINT = new Paint();
        private static Typeface TYPEFACE = null;
        private static Point TEXT_CENTER = null;

        static {
            PAINT.setTextSize(DEFAULT_FONT_SIZE);
        }

        public static Point getTextCenter() {
            return TEXT_CENTER;
        }

        private final TextSymbolizer textSymbolizer;
        public final List<Polygon> polygons;
        public final float textWidth;
        public final String character;

        public TextDrawable(TextSymbolizer textSymbolizer, String character) {
            float lengthPerPixel = textSymbolizer.config.getLengthPerPixel();
            if (TYPEFACE == null) {
                TYPEFACE = Typeface.createFromAsset(textSymbolizer.config.context.getAssets(), "NotoSans-Regular.ttf");
                PAINT.setTypeface(TYPEFACE);
                TEXT_CENTER = new Point(0, (PAINT.ascent() + PAINT.descent()) / 2).scale(lengthPerPixel);
            }
            float textWidth = PAINT.measureText(character) * lengthPerPixel;
            Path charPath = new Path();
            PAINT.getTextPath(character, 0, character.length(), 0, 0, charPath);
            this.polygons = textPathToPolygons(charPath, lengthPerPixel);
            this.textWidth = textWidth;
            this.textSymbolizer = textSymbolizer;
            this.character = character;
        }

        private static List<Polygon> textPathToPolygons(Path path, float scale) {
            List<Polygon> polygons = new ArrayList<>();
            PathMeasure pathMeasure = new PathMeasure(path, false);

            float[] pos = new float[2];
            float[] tan = new float[2];

            Path prevPath = null;
            Path curPath;

            boolean done = false;
            while (!done) {
                List<Point> points = new ArrayList<>();
                float length = pathMeasure.getLength();
                curPath = new Path();
                pathMeasure.getSegment(0, length, curPath, true);
                float dist = length / (float) TextSymbolizer.NUM_POINTS_PER_TEXT_PATH;
                for (float i = 0; i < length; i += dist) {
                    pathMeasure.getPosTan(i, pos, tan);
                    points.add(new Point(pos[0], -pos[1]).scale(scale));
                }

                if (points.size() < 3) {
                    done = !pathMeasure.nextContour();
                    continue;
                }

                if (points.get(points.size() - 1) != points.get(0)) {
                    points.add(points.get(0));
                }

                if (prevPath != null) {
                    // check if prevPath is inside curPath
                    RectF prevRect = new RectF();
                    prevPath.computeBounds(prevRect, true);
                    RectF curRect = new RectF();
                    curPath.computeBounds(curRect, true);
                    if (prevRect.contains(curRect)) {
                        polygons.get(polygons.size() - 1).addHole(points);
                    } else {
                        polygons.add(new Polygon(points));
                        prevPath = curPath;
                    }
                } else {
                    polygons.add(new Polygon(points));
                    prevPath = curPath;
                }

                done = !pathMeasure.nextContour();
            }


            return polygons;
        }
    }

    private static final int NUM_POINTS_PER_TEXT_PATH = 40;
    private static Typeface tf = null;
    private static Typeface tf1 = null;
    private static final HashMap<String, TextDrawable> textCache = new HashMap<>();


    static class TextSymMeta extends SymMeta {
        final List<float[]> polygonDrawables;
        final List<float[]> lineDrawables;
        int firstHalfCount = 0;
        private VertexArray vertexArray = null;

        public TextSymMeta() {
            this.polygonDrawables = new ArrayList<>(0);
            this.lineDrawables = new ArrayList<>(0);
        }

        public TextSymMeta(float[] triDrawable, float[] triStripDrawable) {
            this.polygonDrawables = new ArrayList<>(1) {{
                add(triDrawable);
            }};
            this.lineDrawables = new ArrayList<>(1) {{
                add(triStripDrawable);
            }};
        }

        public TextSymMeta(List<float[]> polygonDrawables, List<float[]> lineDrawables) {
            this.polygonDrawables = polygonDrawables;
            this.lineDrawables = lineDrawables;
        }

        @Override
        public boolean isEmpty() {
            return vertexArray == null && polygonDrawables.isEmpty() && lineDrawables.isEmpty();
        }

        @Override
        public SymMeta append(SymMeta other) {
            if (!(other instanceof TextSymMeta)) return this;
            TextSymMeta otherTextSymMeta = (TextSymMeta) other;
            List<float[]> result = new ArrayList<>(polygonDrawables);
            result.addAll(otherTextSymMeta.polygonDrawables);
            List<float[]> result2 = new ArrayList<>(lineDrawables);
            result2.addAll(otherTextSymMeta.lineDrawables);
            return new TextSymMeta(result, result2);
        }

        private float[] getDrawable() {
            float[] triDrawable = appendRegular(polygonDrawables);
            float[] triStripDrawable = appendTriangleStrip(lineDrawables, TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
            float[] drawable = appendRegular(triStripDrawable, triDrawable);
            firstHalfCount = triStripDrawable.length / TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT;
            return drawable;
        }

        private void draw(TextSymbShaderProgram shaderProgram) {
            if (isEmpty()) return;
            if (vertexArray == null) {
                vertexArray = new VertexArray(shaderProgram, getDrawable());
            }
            shaderProgram.useProgram();
            Point textCenter = TextDrawable.getTextCenter();
            GLES20.glUniform2f(shaderProgram.getUniformLocation(TextSymbShaderProgram.U_TEXT_CENTER), textCenter.x, textCenter.y);
            vertexArray.setDataFromVertexData();
            int pointCount = vertexArray.getVertexCount();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, firstHalfCount);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, firstHalfCount, pointCount - firstHalfCount);
        }


        @Override
        public void save(Config config) {
        }
        
        @Override
        public void draw() {
        }
    }

    /*
     * textName
     * dx
     * dy
     * margin
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
    private final Function<HashMap<String, String>, String> textExprEvaluator;
    private final String textExprString;
    private final float dx;
    private final float dy;
    private final float margin;
    private final float spacing;
    private final float repeatDistance;
    private final float maxCharAngleDelta;
    private final float[] fillColor;
    private final String placement;
    private final String verticalAlignment;
    private final String horizontalAlignment;
    private final String justifyAlignment;
    private final float wrapWidth;
    private final Typeface font;
    private final float size;
    private final float[] haloFill;
    private final float haloRadius;

    public TextSymbolizer(Config config, @Nullable String textExpr, @Nullable String dx, @Nullable String dy, @Nullable String margin, @Nullable String spacing, @Nullable String repeatDistance, @Nullable String maxCharAngleDelta, @Nullable String fill, @Nullable String opacity, @Nullable String placement, @Nullable String verticalAlignment, @Nullable String horizontalAlignment, @Nullable String justifyAlignment, @Nullable String wrapWidth, @Nullable String fontsetName, @Nullable String size, @Nullable String haloFill, @Nullable String haloRadius) {
        super(config);
        if (tf == null && tf1 == null) {
            tf = Typeface.createFromAsset(config.context.getAssets(), "NotoSans-Regular.ttf");
            tf1 = Typeface.createFromAsset(config.context.getAssets(), "NotoSans-Italic.ttf");
        }
        this.textExprString = textExpr;
        this.textExprEvaluator = createTextExprEvaluator(textExpr);
        this.dx = dx == null ? 0 : Float.parseFloat(dx);
        this.dy = dy == null ? 0 : Float.parseFloat(dy);
        this.margin = margin == null ? 0 : Float.parseFloat(margin);
        this.spacing = spacing == null ? 0 : Float.parseFloat(spacing);
        this.repeatDistance = repeatDistance == null ? 0 : Float.parseFloat(repeatDistance);
        this.maxCharAngleDelta = maxCharAngleDelta == null ? 22.5f : Float.parseFloat(maxCharAngleDelta);
        this.fillColor = parseColorString(fill, opacity == null ? 1 : Float.parseFloat(opacity));
        this.placement = placement == null ? "point" : placement;
        this.verticalAlignment = parseVerticalAlignment(verticalAlignment);
        this.horizontalAlignment = parseHorizontalAlignment(horizontalAlignment);
        this.justifyAlignment = parseJustifyAlignment(justifyAlignment);
        this.wrapWidth = wrapWidth == null ? 0 : Float.parseFloat(wrapWidth);
        this.font = fontsetName == null ? tf : fontsetName.equals("fontset-1") ? tf1 : tf;
        this.size = size == null ? 10 : Float.parseFloat(size);
        this.haloFill = haloFill == null ? new float[]{1, 1, 1, 1} : parseColorString(haloFill, 1);
        this.haloRadius = haloRadius != null
                ? Float.parseFloat(haloRadius)
                : this.placement.equals("point") ? 1 : 0;
    }

    public static Function<HashMap<String, String>, String> createTextExprEvaluator(String template) {
        if (template == null || template.isEmpty()) {
            return tags -> null;
        }

        Pattern pattern = Pattern.compile("(\\[.+?])|('.+?')");
        Matcher matcher = pattern.matcher(template);
        List<Pair<String, Boolean>> expressions = new ArrayList<>(); // <tag, isLiteral>

        while (matcher.find()) {
            String tag = matcher.group(1);
            String literal = matcher.group(2);

            if (tag != null) {
                expressions.add(new Pair<>(tag.substring(1, tag.length() - 1), false));
            } else if (literal != null) {
                expressions.add(new Pair<>(literal.substring(1, literal.length() - 1), true));
            }
        }

        return tags -> {
            StringBuilder sb = new StringBuilder();
            for (Pair<String, Boolean> expression : expressions) {
                String expr = expression.first;
                Boolean isLiteral = expression.second;
                if (isLiteral) {
                    sb.append(expr);
                } else {
                    String replacement = tags.get(expr);
                    if (replacement == null) {
                        return null;
                    }
                    sb.append(replacement);
                }
            }
            return sb.toString();
        };
    }

    private String parseVerticalAlignment(String verticalAlignment) {
        switch (verticalAlignment == null ? "auto" : verticalAlignment) {
            case "top":
                return "top";
            case "middle":
                return "middle";
            case "bottom":
                return "bottom";
            default:
                if (dy > 0) {
                    return "bottom";
                } else if (dy < 0) {
                    return "top";
                } else {
                    return "middle";
                }
        }
    }

    private String parseHorizontalAlignment(String horizontalAlignment) {
        switch (horizontalAlignment == null ? "auto" : horizontalAlignment) {
            case "left":
                return "left";
            case "middle":
                return "middle";
            case "right":
                return "right";
            default:
                if (placement.equals("line") || placement.equals("point")) {
                    return "middle";
                } else {
                    if (dx > 0) {
                        return "left";
                    } else if (dx < 0) {
                        return "right";
                    } else {
                        return "middle";
                    }
                }
        }
    }

    private String parseJustifyAlignment(String justifyAlignment) {
        switch (justifyAlignment == null ? "auto" : justifyAlignment) {
            case "left":
                return "left";
            case "right":
                return "right";
            default:
                return "center";
        }
    }

    private String getName(HashMap<String, String> tags) {
        return textExprEvaluator.apply(tags);
    }

    @Override
    public SymMeta toDrawable(Way way, String layerName) {
        Paint paint = new Paint();
        paint.setTypeface(tf);
        paint.setTextSize(size);

        float originX = config.getOriginX();
        float originY = config.getOriginY();
        float scale = CoordinateTransform.getScalePixel(config.getScaleDenominator()) * config.getLengthPerPixel();
        PointList shape = way.isClosed()
                ? way.toPolygon(originX, originY, scale)
                : way.toPointList(originX, originY, scale);

        switch (placement) {
            case "line":
                return drawLinePlacement(way, layerName, shape, paint);
            case "point":
                return drawPointPlacement(way, layerName, shape, paint);
        }

        return new TextSymMeta();
    }

    private TextDrawable getText(String c) {
        TextDrawable textDrawable = textCache.get(c);
        if (textDrawable == null) {
            textDrawable = new TextDrawable(this, c);
            textCache.put(c, textDrawable);
        }
        return textDrawable;
    }

    private float getTextWidth(String c, float fontSize) {
        float textWidth = 0;
        for (int i = 0; i < c.length(); i++) {
            TextDrawable textDrawable = getText(c.substring(i, i + 1));
            textWidth += textDrawable.textWidth * fontSize / TextDrawable.DEFAULT_FONT_SIZE;
        }
        return textWidth;
    }

    private SymMeta drawPointPlacement(Way way, String layerName, PointList shape, Paint paint) {
        TextSymMeta rv = new TextSymMeta();
        if (way.hasAlreadyDrawnTextPoint()) {
            return rv;
        }
        way.setAlreadyDrawnTextPoint(true);

        String name = getName(way.tags.get(layerName));
        if (name == null) {
            return rv;
        }
        float lengthPerPixel = config.getLengthPerPixel();
        Point center;
        if (shape instanceof Polygon) {
            center = ((Polygon) shape).getCentroid();
        } else {
            center = shape.points.get(0).equals(shape.points.get(shape.points.size() - 1)) ? new Polygon(shape.points).getCentroid() : shape.points.stream().reduce(new Point(0, 0), (a, b) -> new Point(a.x + b.x, a.y + b.y)).scale(1f / shape.points.size());
        }

        float textWidth = getTextWidth(name, size);
        float width = wrapWidth == 0 ? (textWidth - 2 * margin * lengthPerPixel) : wrapWidth * lengthPerPixel;
        float currentWidth = 0;
        float currentHeight = 0;
        float addOffsetY = paint.ascent() * lengthPerPixel;
        float heightIncrement = paint.getFontSpacing() * lengthPerPixel;

        String[] lines = name.split("\\\\n");
        List<Polygon> polygons = new ArrayList<>();
        List<Point> offsets = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                TextDrawable textDrawable = getText(line.substring(i, i + 1));
                float charWidth = textDrawable.textWidth * size / TextDrawable.DEFAULT_FONT_SIZE;
                if (currentWidth + charWidth > width) {
                    currentWidth = 0;
                    currentHeight -= heightIncrement;
                }
                if (textDrawable.polygons.size() > 0) {
                    offsets.add(new Point(currentWidth, currentHeight + addOffsetY));
                    indices.add(polygons.size());
                    polygons.addAll(textDrawable.polygons);
                }
                currentWidth += charWidth;
            }
            currentWidth = 0;
            currentHeight -= heightIncrement;
        }

        offsets = alignText(offsets, center, width, currentHeight);

        int offsetIndex = 0;
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            if (offsetIndex + 1 < indices.size() && i == indices.get(offsetIndex + 1)) {
                offsetIndex++;
            }
            Point offset = offsets.get(offsetIndex);
            if (polygon != null) {
                List<Triangle> curTriangulatedTriangles = polygon.triangulate();

                float[] curTri = TextSymbShaderProgram.toPointVertexData(new ArrayList<>() {
                    {
                        for (Triangle triangle : curTriangulatedTriangles) {
                            add(triangle.p1);
                            add(triangle.p2);
                            add(triangle.p3);
                        }
                    }
                }, size, offset, center, fillColor);
                float strokeWidth = haloRadius * lengthPerPixel;
                float[] curTriStrip = drawPointTextLineStrip(new LineStrip(polygon), size, offset, center, strokeWidth, haloFill);
                for (Polygon hole : polygon.holes) {
                    curTriStrip = SymMeta.appendTriangleStrip(curTriStrip, drawPointTextLineStrip(new LineStrip(hole), size, offset, center, strokeWidth, haloFill), TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
                }

                rv = (TextSymMeta) rv.append(new TextSymMeta(curTri, curTriStrip));
            }
        }

        return rv;
    }

    private float[] drawPointTextLineStrip(LineStrip lineStrip, float fontSize, Point offset, Point center, float strokeWidth, float[] strokeColor) {
        if (lineStrip == null || strokeWidth <= 0) {
            return new float[0];
        }
        StrokeGenerator.Stroke lineStroke = StrokeGenerator.generateStroke(lineStrip
                , 8, strokeWidth / 2, StrokeGenerator.StrokeLineCap.ROUND);
        TriangleStrip triangleStrip = lineStroke.toTriangleStrip();
        return TextSymbShaderProgram.toPointVertexData(triangleStrip, fontSize, offset, center, strokeColor);
    }


    private List<Point> alignText(List<Point> offsets, Point center, float width, float height) {
        float translateX;
        float translateY;
        switch (horizontalAlignment) {
            case "middle":
                translateX = center.x - width / 2;
                break;
            case "right":
                translateX = center.x - width;
                break;
            default:
                translateX = 0;
        }

        switch (verticalAlignment) {
            case "middle":
                translateY = center.y - height / 2;
                break;
            case "bottom":
                translateY = center.y - height;
                break;
            default:
                translateY = 0;
        }

        return offsets.stream().map(offset -> new Point(offset.x + translateX, offset.y + translateY)).collect(Collectors.toList());
    }

    private SymMeta drawLinePlacement(Way way, String layerName, PointList shape, Paint paint) {
        TextSymMeta rv = new TextSymMeta();
        String name = getName(way.tags.get(layerName));
        if (name == null) {
            return rv;
        }
        List<GetLinePathResult> results = getLinePath(paint, name, shape.points);
        if (results == null) {
            return rv;
        }

        for (GetLinePathResult result : results) {
            CreateTextPathOnPathResult res = createTextPathOnPath(result.path, name, result.hOffset, result.vOffset, paint);

            if (res == null) {
                continue;
            }

            int offsetIndex = 0;
            float lengthPerPixel = config.getLengthPerPixel();
            float firstAngle = res.angles.get(0);
            for (int i = 0; i < res.polygons.size(); i++) {
                Polygon polygon = res.polygons.get(i);
                if (offsetIndex + 1 < res.indices.size() && i == res.indices.get(offsetIndex + 1)) {
                    offsetIndex++;
                }
                float angle = res.angles.get(offsetIndex);
                Point offset = res.offsets.get(offsetIndex);
                float altAngle = res.altAngles.get(offsetIndex);
                Point altOffset = res.altOffsets.get(offsetIndex);
                if (polygon != null) {
                    List<Triangle> curTriangulatedTriangles = polygon.triangulate();

                    float[] curTri = TextSymbShaderProgram.toLineVertexData(new ArrayList<>() {
                        {
                            for (Triangle triangle : curTriangulatedTriangles) {
                                add(triangle.p1);
                                add(triangle.p2);
                                add(triangle.p3);
                            }
                        }
                    }, size, angle, offset, altAngle, altOffset, firstAngle, fillColor);
                    float strokeWidth = haloRadius * lengthPerPixel;
                    float[] curTriStrip = drawLineTextLineStrip(new LineStrip(polygon), size, angle, offset, altAngle, altOffset, firstAngle, strokeWidth, haloFill);
                    for (Polygon hole : polygon.holes) {
                        curTriStrip = SymMeta.appendTriangleStrip(curTriStrip, drawLineTextLineStrip(new LineStrip(hole), size, angle, offset, altAngle, altOffset, firstAngle, strokeWidth, haloFill), TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
                    }

                    rv = (TextSymMeta) rv.append(new TextSymMeta(curTri, curTriStrip));
                }
            }
        }

        return rv;
    }

    private float[] drawLineTextLineStrip(LineStrip lineStrip, float fontSize, float angle, Point offset, float altAngle, Point altOffset, float firstAngle, float strokeWidth, float[] strokeColor) {
        if (lineStrip == null || strokeWidth <= 0) {
            return new float[0];
        }
        StrokeGenerator.Stroke lineStroke = StrokeGenerator.generateStroke(lineStrip
                , 8, strokeWidth / 2, StrokeGenerator.StrokeLineCap.ROUND);
        TriangleStrip triangleStrip = lineStroke.toTriangleStrip();
        return TextSymbShaderProgram.toLineVertexData(triangleStrip, fontSize, angle, offset, altAngle, altOffset, firstAngle, strokeColor);
    }


    private Path pointsToPath(List<Point> points) {
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            Point p = points.get(i);
            path.lineTo(p.x, p.y);
        }
        return path;
    }

    private List<GetLinePathResult> getLinePath(Paint paint, String text, List<Point> points) {
        Path path = pointsToPath(points);
        float lengthPerPixel = config.getLengthPerPixel();
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        float textWidth = paint.measureText(text) * lengthPerPixel;

        if (textWidth > pathLength) {
            return null;
        }

        float spacing = this.spacing * lengthPerPixel;
        int numRepetitions = spacing == 0 ? 1 : (int) Math.floor((pathLength + spacing) / (textWidth + spacing));
        float totalSpacing = numRepetitions * textWidth + (numRepetitions - 1) * spacing;
        float offsetEach = (pathLength - totalSpacing) / 2;
        float vOffset = ((-paint.ascent() + paint.descent()) / 2 - paint.descent()) * lengthPerPixel;
        List<Float> hOffsets = new ArrayList<>();

        for (int i = 0; i < numRepetitions; i++) {
            float hOffset = offsetEach + i * (textWidth + spacing);
            hOffsets.add(hOffset);
        }

        boolean isUpsideDown = checkUpsideDown(pathMeasure, config.getRotation());

        if (isUpsideDown) {
            List<Point> newPoints = new ArrayList<>(points);
            Collections.reverse(newPoints);
            path = pointsToPath(newPoints);
        }

        Path finalPath = path;
        return new ArrayList<>(numRepetitions) {
            {
                for (float hOffset : hOffsets) {
                    add(new GetLinePathResult(finalPath, hOffset, vOffset));
                }
            }
        };
    }

    private static class CreateTextPathOnPathResult {
        public final List<Polygon> polygons;
        public final List<Point> offsets;
        public final List<Float> angles;
        public final List<Point> altOffsets;
        public final List<Float> altAngles;
        public final List<Integer> indices;

        public CreateTextPathOnPathResult(List<Polygon> polygons, List<Point> offsets, List<Float> angles, List<Point> altOffsets, List<Float> altAngles, List<Integer> indices) {
            this.polygons = polygons;
            this.offsets = offsets;
            this.angles = angles;
            this.altOffsets = altOffsets;
            this.altAngles = altAngles;
            this.indices = indices;
        }
    }

    private CreateTextPathOnPathResult createTextPathOnPath(Path originalPath, String text, float hOffset, float vOffset, Paint paint) {
        List<Polygon> polygons = new ArrayList<>();
        List<Point> offsets = new ArrayList<>();
        List<Float> angles = new ArrayList<>();
        List<Point> altOffsets = new ArrayList<>();
        List<Float> altAngles = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        PathMeasure pathMeasure = new PathMeasure(originalPath, false);
        float lengthPerPixel = config.getLengthPerPixel();
        float textWidth = paint.measureText(text) * lengthPerPixel;
        float[] pos = new float[2];
        float[] tan = new float[2];
        float[] altPos = new float[2];
        float[] altTan = new float[2];
        Float prevAngle = null;
        Float prevAltAngle = null;
        float distance = 0;

        for (int i = 0; i < text.length(); i++) {
            String c = text.substring(i, i + 1);
            TextDrawable textDrawable = getText(c);
            float charWidth = textDrawable.textWidth * size / TextDrawable.DEFAULT_FONT_SIZE;

            pathMeasure.getPosTan(hOffset + distance, pos, tan);
            pathMeasure.getPosTan(hOffset + textWidth - distance, altPos, altTan);

            Point offset = new Point(pos[0] + tan[1] * (vOffset + dy * lengthPerPixel), pos[1] - tan[0] * (vOffset + dy * lengthPerPixel));
            Point altOffset = new Point(altPos[0] - altTan[1] * (vOffset - dy * lengthPerPixel), altPos[1] + altTan[0] * vOffset - dy * lengthPerPixel);
            float angle = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);
            float altAngle = (float) (Math.atan2(altTan[1], altTan[0]) * 180 / Math.PI);
            altAngle = (altAngle + 180) % 360;

            if (prevAngle != null) {
                float angleDelta = Math.abs(angle - prevAngle);
                float altAngleDelta = Math.abs(altAngle - prevAltAngle);

                if (angleDelta > 180) {
                    angleDelta = 360 - angleDelta;
                }

                if (altAngleDelta > 180) {
                    altAngleDelta = 360 - altAngleDelta;
                }

                if (angleDelta > maxCharAngleDelta || altAngleDelta > maxCharAngleDelta) {
                    return null;
                }
            }

            prevAngle = angle;
            prevAltAngle = altAngle;


            if (textDrawable.polygons.size() > 0) {
                offsets.add(offset);
                altOffsets.add(altOffset);
                angles.add(angle);
                altAngles.add(altAngle);
                indices.add(polygons.size());
                polygons.addAll(textDrawable.polygons);
            }

            distance += charWidth;
        }

        return new CreateTextPathOnPathResult(polygons, offsets, angles, altOffsets, altAngles, indices);
    }

    private static class GetLinePathResult {
        public Path path;
        public float hOffset;
        public float vOffset;

        public GetLinePathResult(Path path, float hOffset, float vOffset) {
            this.path = path;
            this.hOffset = hOffset;
            this.vOffset = vOffset;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "TextDrawable{" +
                "textName='" + textExprString + '\'' +
                ", size=" + size +
                ", placement='" + placement + '\'' +
                ", dx=" + dx +
                ", dy=" + dy +
                ", fillColor=" + Arrays.toString(fillColor) +
                ", haloFill=" + Arrays.toString(haloFill) +
                ", verticalAlignment='" + verticalAlignment + '\'' +
                ", horizontalAlignment='" + horizontalAlignment + '\'' +
                '}';
    }

    private static boolean checkUpsideDown(PathMeasure pathMeasure, float at, float rotation) {
        float[] pos = new float[2];
        float[] tan = new float[2];

        pathMeasure.getPosTan(at, pos, tan);
        float angle = (float) Math.toDegrees(Math.atan2(tan[1], tan[0])) + rotation;

        return angle > 90 || angle < -90;
    }

    private static boolean checkUpsideDown(PathMeasure pathMeasure, float rotation) {
        return checkUpsideDown(pathMeasure, pathMeasure.getLength() / 2, rotation);
    }
}
