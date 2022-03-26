package com.appsirise.locationworkmanager

interface LocationRepository {
    fun insert(location: LocationEntity)
    fun delete()
}