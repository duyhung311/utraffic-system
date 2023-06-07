package com.hcmut.admin.utrafficsystem.tbt.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.hcmut.admin.utrafficsystem.tbt.osm.Node;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;

import java.util.HashMap;
import java.util.List;

@Entity
public class WayEntity {
    private static final Gson gson = new Gson();

    private static class NodesGson {
        public final List<Node> nodes;

        public NodesGson(List<Node> nodes) {
            this.nodes = nodes;
        }
    }

    private static class TagsGson {
        public final HashMap<String, HashMap<String, String>> tags;

        public TagsGson(HashMap<String, HashMap<String, String>> tags) {
            this.tags = tags;
        }
    }

    @PrimaryKey
    public long id;
    public String nodes;
    public String tags;

    public WayEntity() {
    }

    @Ignore
    public WayEntity(long id, List<Node> nodes, HashMap<String, HashMap<String, String>> tags) {
        this.id = id;
        this.nodes = gson.toJson(new NodesGson(nodes));
        this.tags = gson.toJson(new TagsGson(tags));
    }

    public Way toWay() {
        NodesGson nodesGson = gson.fromJson(nodes, NodesGson.class);
        TagsGson tagsGson = gson.fromJson(tags, TagsGson.class);
        return new Way(id, nodesGson.nodes, tagsGson.tags);
    }
}
