package com.hcmut.admin.utrafficsystem.model.osm.data;

import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.model.osm.geometry.Point;
import com.hcmut.admin.utrafficsystem.model.osm.data.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Way {
    private ArrayList<Node> nodes;
    private HashMap<String, String> tags;

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    public Way() {
        nodes = new ArrayList<>();
    }

    public Way(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public Way(float[] vertices) {
        nodes = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 3) {
            nodes.add(new Node(vertices[i], vertices[i + 1]));
        }
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public boolean isClosed() {
        return nodes.get(0).equals(nodes.get(nodes.size() - 1));
    }

    public List<Point> toPoints() {
        List<Point> points = new ArrayList<>();
        for (Node node : nodes) {
            points.add(new Point(node.getLon(), node.getLat()));
        }
        return points;
    }

    public List<Point> toPoints(float originX, float originY, float scale) {
        List<Point> points = new ArrayList<>();
        for (Node node : nodes) {
            points.add(new Point((node.getLon() - originX) * scale, (node.getLat() - originY) * scale));
        }
        return points;
    }

    public List<Point> toPoints(float originX, float originY, float scale, float z) {
        List<Point> points = new ArrayList<>();
        for (Node node : nodes) {
            points.add(new Point((node.getLon() - originX) * scale, (node.getLat() - originY) * scale, z));
        }
        return points;
    }

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Way{");
        for (Node node : nodes) {
            sb.append("\n\t");
            sb.append(node.toString());
            sb.append(", ");
        }
        sb.append("\n}");
        return sb.toString();
    }

}
