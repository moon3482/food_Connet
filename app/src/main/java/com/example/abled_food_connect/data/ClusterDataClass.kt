package com.example.abled_food_connect.data

import com.naver.maps.geometry.LatLng
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

class ClusterDataClass(val position:LatLng, val name :String ):TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(position.latitude,position.longitude)
    }
    /**
     * Set the title of the marker
     * @param title string to be set as title
     */
    var title: String? = null
    /**
     * Set the description of the marker
     * @param snippet string to be set as snippet
     */
    var snippet: String? = null

    constructor(lat: Double, lng: Double) : this(LatLng(lat, lng), String()) {
        title = null
        snippet = null
    }

    constructor(lat: Double, lng: Double, title: String?, snippet: String?) : this(
        LatLng(lat, lng), String()
    ) {
        this.title = title
        this.snippet = snippet
    }
}