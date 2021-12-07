package com.example.notify_around.models

import java.util.*

class Locations {
    var location_name: String? = null
    var location_sub_name: String? = null
    var full_name: String? = null
    var placeId: String? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val locations = o as Locations
        return placeId == locations.placeId
    }

    override fun hashCode(): Int {
        return Objects.hash(placeId)
    }
}