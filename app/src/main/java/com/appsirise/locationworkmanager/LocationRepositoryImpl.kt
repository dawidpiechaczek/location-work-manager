package com.appsirise.locationworkmanager

class LocationRepositoryImpl(
    appDatabase: AppDatabase
): LocationRepository {

    private val locationDao: LocationDao = appDatabase.locationDao()

    override fun insert(location: LocationEntity) {
        locationDao.insert(location)
    }

    override fun delete() {
        locationDao.deleteAll()
    }
}