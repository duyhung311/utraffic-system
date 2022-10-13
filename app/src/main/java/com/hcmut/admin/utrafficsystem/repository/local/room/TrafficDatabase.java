package com.hcmut.admin.utrafficsystem.repository.local.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hcmut.admin.utrafficsystem.repository.local.room.dao.StatusRenderDataDAO;
import com.hcmut.admin.utrafficsystem.repository.local.room.entity.StatusRenderDataEntity;

@Database(entities = {StatusRenderDataEntity.class}, exportSchema = false, version = 5)
public abstract class TrafficDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "traffic_db";

    private static TrafficDatabase trafficDatabase;

    public static TrafficDatabase getInstance(Context context) {
        if (trafficDatabase == null) {
            trafficDatabase = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TrafficDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return trafficDatabase;
    }

    //*************** define getter for DAO object here *******************//
    public abstract StatusRenderDataDAO getStatusRenderDataDAO();
}