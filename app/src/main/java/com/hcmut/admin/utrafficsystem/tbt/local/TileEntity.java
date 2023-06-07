package com.hcmut.admin.utrafficsystem.tbt.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TileEntity {
    @PrimaryKey
    public long id;

    public TileEntity() {
    }

    @Ignore
    public TileEntity(long id) {
        this.id = id;
    }
}
