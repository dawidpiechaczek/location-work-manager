package com.appsirise.locationworkmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert
    fun insert(location: LocationEntity)

    @Query("DELETE FROM locations")
    fun deleteAll()
}
