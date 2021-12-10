package com.example.notify_around.models

import java.util.*

class Locations {
    var location_name: String? = null
    var location_sub_name: String? = null
    var full_name: String? = null
    var placeId: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val locations = other as Locations
        return placeId == locations.placeId
    }

    override fun hashCode(): Int {
        return Objects.hash(placeId)
    }
}