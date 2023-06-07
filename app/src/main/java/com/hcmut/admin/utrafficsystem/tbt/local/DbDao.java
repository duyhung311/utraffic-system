package com.hcmut.admin.utrafficsystem.tbt.local;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DbDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM wayentity JOIN waytileentity ON wayentity.id = waytileentity.wayId WHERE waytileentity.tileId = :tileId")
    List<WayEntity> getWaysByTileId(long tileId);

    @Query("SELECT id FROM tileentity WHERE id IN (:tileIds)")
    List<Long> getAllTileIds(List<Long> tileIds);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertWays(List<WayEntity> wayEntities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTile(TileEntity tileEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertWayTile(WayTileEntity wayTileEntity);

    @Transaction
    default void insertWaysAndTile(List<WayEntity> wayEntities, TileEntity tileId) {
        Log.d("DbDao", "insertWaysAndTile: " + wayEntities.size() + " " + tileId.id);
        insertWays(wayEntities);
        insertTile(tileId);
        for (WayEntity wayEntity : wayEntities) {
            insertWayTile(new WayTileEntity(wayEntity.id, tileId.id));
        }
    }

    @Query("DELETE FROM tileentity")
    void clearTileEntity();

    @Query("DELETE FROM wayentity")
    void clearWayEntity();

    @Query("DELETE FROM waytileentity")
    void clearWayTileEntity();

    @Transaction
    default void clearAll() {
        clearWayTileEntity();
        clearTileEntity();
        clearWayEntity();
    }
}
