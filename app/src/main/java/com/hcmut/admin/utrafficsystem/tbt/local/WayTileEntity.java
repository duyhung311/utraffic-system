package com.hcmut.admin.utrafficsystem.tbt.local;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(primaryKeys = {"wayId", "tileId"},
        indices = {@Index(value = "wayId"), @Index(value = "tileId")},
        foreignKeys = {
                @ForeignKey(entity = WayEntity.class, parentColumns = "id", childColumns = "wayId"),
                @ForeignKey(entity = TileEntity.class, parentColumns = "id", childColumns = "tileId")
        })
public class WayTileEntity {
    public long wayId;
    public long tileId;

    public WayTileEntity() {
    }

    @Ignore
    public WayTileEntity(long wayId, long tileId) {
        this.wayId = wayId;
        this.tileId = tileId;
    }
}