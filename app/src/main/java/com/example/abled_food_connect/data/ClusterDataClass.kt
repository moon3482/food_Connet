package com.example.abled_food_connect.data

import com.example.abled_food_connect.data.kakaoDataClass.Document
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

class ClusterDataClass(val position: LatLng, val name: String, val document: Document) : TedClusterItem {


    override fun getTedLatLng(): TedLatLng {

        return TedLatLng(position.latitude, position.longitude)
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

    var status: Int = 0


    constructor(lat: Double, lng: Double,document: Document) : this(
        LatLng(lat, lng), String(),  Document(document.roadAddressName,document.categoryGroupCode,document.categoryGroupName,document.categoryName,document.distance,document.id,document.phone,document.placeName,document.placeUrl,document.roadAddressName,document.x,document.y)
    ) {
        title = null
        snippet = null

    }

    constructor(lat: Double, lng: Double, title: String?, snippet: String?,document: Document) : this(
        LatLng(lat, lng), String(),Document(document.roadAddressName,document.categoryGroupCode,document.categoryGroupName,document.categoryName,document.distance,document.id,document.phone,document.placeName,document.placeUrl,document.roadAddressName,document.x,document.y)
    ) {
        this.title = title
        this.snippet = snippet
    }

    fun getname(): String {
        return name
    }


}