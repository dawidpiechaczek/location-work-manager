package com.appsirise.locationworkmanager

import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
): LocationRepository {

    override fun insert(location: LocationEntity) {
        locationDao.insert(location)
    }

    override fun delete() {
        locationDao.deleteAll()
    }
}