package com.hcmut.admin.utrafficsystem.tbt.algorithm;


import android.annotation.SuppressLint;
import android.util.Pair;

import com.hcmut.admin.utrafficsystem.tbt.geometry.LineStrip;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.TriangleStrip;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.buffer.OffsetCurveBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("NewApi")
public class StrokeGenerator {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public enum StrokeLineJoin {
        MITER, BEVEL, ROUND
    }

    public enum StrokeLineCap {
        BUTT, ROUND, SQUARE
    }

    private static class PolygonalBrush {
        private List<Point> points;
        private Point center;
        private int curActivePointIdx = 0;
        private int prevTangentCoef = 1;
        private final StrokeLineCap strokeLineCap;
        private final float r;

        public PolygonalBrush(int n, float r, Point center, StrokeLineCap strokeLineCap) {
            assert n % 2 == 0 && n >= 4 : "n must be even and >= 4";
            points = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                float angle = (float) -(2 * Math.PI * i / n);
                points.add(new Point(center.x + r * (float) Math.cos(angle), center.y + r * (float) Math.sin(angle)));
            }
            this.r = r;
            this.center = center;
            this.strokeLineCap = strokeLineCap;
        }

        public void translate(Vector v) {
            List<Point> newPoints = new ArrayList<>();
            for (Point p : points) {
                newPoints.add(p.add(v));
            }
            points = newPoints;
            center = center.add(v);
        }

        public void translate(float dx, float dy) {
            translate(new Vector(dx, dy));
        }

        public void relocate(Point p) {
            translate(new Vector(center, p));
        }

        public Point[] getCurrentAndOppositeVertices(int i) {
            int n = points.size();
            return new Point[]{points.get(i), points.get((i + n / 2) % n)};
        }

        private int isInTangentRange(int i, Vector v) {
            int n = points.size();
            Point prev = points.get((i - 1 + n) % n);
            Point curr = points.get(i);
            Point next = points.get((i + 1) % n);

            Vector currToPrev = new Vector(curr, prev);
            Vector nextToCurr = new Vector(next, curr);

            Vector prevToCurr = new Vector(prev, curr);
            Vector currToNext = new Vector(curr, next);

            if (v.isInBetween(currToPrev, nextToCurr)) {
                return -1;
            } else if (v.isInBetween(prevToCurr, currToNext)) {
                return 1;
            } else {
                return 0;
            }
        }

        private void initCurActivePointIdx(Vector v) {
            int n = points.size();
            for (int i = 0; i < n; i++) {
                int inTangentRange = isInTangentRange(i, v);
                if (inTangentRange != 0) {
                    if (inTangentRange == -1) {
                        curActivePointIdx = (i + n / 2) % n;
                    } else {
                        curActivePointIdx = i;
                    }
                }
            }
        }

        public List<Point> genRoundCapVertices(Vector v) {
            List<Point> rv = new ArrayList<>();
            int n = points.size();
            for (int i = 0; i < n; i++) {
                int inTangentRange = isInTangentRange(i, v);
                if (inTangentRange != 0) {

                    if (inTangentRange == -1) {
                        for (int j = i; j != (i + n / 2) % n; j = (j + 1) % n) {
                            rv.add(points.get(j));
                        }
                        rv.add(points.get((i + n / 2) % n));
                        curActivePointIdx = (i + n / 2) % n;
                    } else {
                        for (int j = (i + n / 2) % n; j != i; j = (j + 1) % n) {
                            rv.add(points.get(j));
                        }
                        rv.add(points.get(i));
                        curActivePointIdx = i;
                    }
                    return rv;
                }
            }

            return rv;
        }

        public List<Point> genButtCapVertices(Vector v) {
            initCurActivePointIdx(v);

            Vector v1 = v.orthogonal2d().normalize().mul(r);
            Vector v2 = v1.negate();
            return new ArrayList<>() {{
                add(center.add(v2));
                add(center.add(v1));
            }};
        }

        public List<Point> genSquareCapVertices(Vector v) {
            Vector dir = v.negate().normalize().mul(r);

            return new ArrayList<>() {{
                for (Point p : genButtCapVertices(v)) {
                    add(p.add(dir));
                }
            }};
        }

        public List<Point> genEndCapTriangleStrip(Vector v, boolean isStart) {
            List<Point> points;
            switch (strokeLineCap) {
                case BUTT:
                    points = genButtCapVertices(v);
                    break;
                case ROUND:
                    points = genRoundCapVertices(v);
                    break;
                case SQUARE:
                    points = genSquareCapVertices(v);
                    break;
                default:
                    throw new RuntimeException("Unknown strokeLineCap: " + strokeLineCap);
            }

            List<Point> rv = new ArrayList<>();
            int n = points.size();
            for (int i = 0; i <= n / 2; i++) {
                rv.add(points.get(i));
                rv.add(points.get(n - 1 - i));
            }

            if (isStart) {
                Collections.reverse(rv);
            }

            return rv;
        }

        public Pair<List<Point>, List<Point>> genTangentVertices(Vector v) {
            List<Point> rv1 = new ArrayList<>();
            List<Point> rv2 = new ArrayList<>();

            int n = points.size();
            for (int i = curActivePointIdx; i != (curActivePointIdx + n / 2) % n; i = (i + 1) % n) {
                int inTangentRange = isInTangentRange(i, v);
                if (inTangentRange == prevTangentCoef) {
                    for (int j = curActivePointIdx; j != i; j = (j + 1) % n) {
                        Point[] currAndOpposite = getCurrentAndOppositeVertices(j);
                        rv1.add(currAndOpposite[0]);
                        rv2.add(currAndOpposite[1]);
                    }
                    Point[] currAndOpposite = getCurrentAndOppositeVertices(i);
                    rv1.add(currAndOpposite[0]);
                    rv2.add(currAndOpposite[1]);
                    curActivePointIdx = i;
                    prevTangentCoef = inTangentRange;
                    return new Pair<>(rv1, rv2);
                }

                if (inTangentRange == -prevTangentCoef) {
                    int oppositeI = (i + n / 2) % n;
                    for (int j = curActivePointIdx; j != oppositeI; j = (j - 1 + n) % n) {
                        Point[] currAndOpposite = getCurrentAndOppositeVertices(j);
                        rv1.add(currAndOpposite[0]);
                        rv2.add(currAndOpposite[1]);
                    }
                    Point[] currAndOpposite = getCurrentAndOppositeVertices(oppositeI);
                    rv1.add(currAndOpposite[0]);
                    rv2.add(currAndOpposite[1]);
                    curActivePointIdx = oppositeI;
                    prevTangentCoef = -inTangentRange;
                    return new Pair<>(rv1, rv2);
                }
            }

            Point[] currAndOpposite = getCurrentAndOppositeVertices(curActivePointIdx);
            return new Pair<>(List.of(currAndOpposite[0]), List.of(currAndOpposite[1]));
        }
    }

    public static class Stroke {
        protected final List<Point> points;

        public Stroke() {
            this(new ArrayList<>());
        }

        public Stroke(List<Point> points) {
            this.points = points;
        }

        public TriangleStrip toTriangleStrip() {
            return new TriangleStrip(new ArrayList<>(this.points));
        }
    }

    private static Stroke generateStroke(LineStrip line, PolygonalBrush brush) {
        Stroke rv = new Stroke();
        List<Point> head = brush.genEndCapTriangleStrip(new Vector(line.points.get(0), line.points.get(1)), true);
        rv.points.addAll(head);

        for (int i = 1; i < line.points.size() - 1; i++) {
            brush.relocate(line.points.get(i));
            Vector v = new Vector(line.points.get(i), line.points.get(i + 1));
            Pair<List<Point>, List<Point>> tangentVertices = brush.genTangentVertices(v);
            for (int j = 0; j < tangentVertices.first.size(); j++) {
                rv.points.add(tangentVertices.first.get(j));
                rv.points.add(tangentVertices.second.get(j));
            }
        }

        brush.relocate(line.points.get(line.points.size() - 1));
        List<Point> tail = brush.genEndCapTriangleStrip(new Vector(line.points.get(line.points.size() - 1), line.points.get(line.points.size() - 2)), false);
        rv.points.addAll(tail);

        return rv;
    }

    public static Stroke generateStroke(LineStrip line, int n, float r, StrokeLineCap cap) {
        return generateStroke(line, new PolygonalBrush(n, r, line.points.get(0), cap));
    }


    public static LineStrip offsetLineStrip(LineStrip lineStrip, float offset, StrokeLineJoin lineJoin) {
        if (offset == 0) return lineStrip;

        // Create a JTS LineString from the input points
        Coordinate[] coordinates = lineStrip.points.stream()
                .map(p -> new Coordinate(p.x, p.y))
                .toArray(Coordinate[]::new);

        BufferParameters bufferParameters = new BufferParameters();
        switch (lineJoin) {
            case MITER:
                bufferParameters.setJoinStyle(BufferParameters.JOIN_MITRE);
                break;
            case BEVEL:
                bufferParameters.setJoinStyle(BufferParameters.JOIN_BEVEL);
                break;
            case ROUND:
                bufferParameters.setJoinStyle(BufferParameters.JOIN_ROUND);
                break;
        }

        OffsetCurveBuilder offsetCurveBuilder = new OffsetCurveBuilder(geometryFactory.getPrecisionModel(), bufferParameters);

        // Offset the line using JTS OffsetCurveBuilder
        Coordinate[] offsetCoordinates = offsetCurveBuilder.getOffsetCurve(coordinates, offset);

        // Convert the offset coordinates back to a list of Point objects
        List<Point> offsetPoints = new ArrayList<>();
        for (Coordinate offsetCoordinate : offsetCoordinates) {
            offsetPoints.add(new Point((float) offsetCoordinate.x, (float) offsetCoordinate.y));
        }

        return new LineStrip(offsetPoints);
    }
}
