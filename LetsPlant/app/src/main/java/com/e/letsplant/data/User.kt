package com.e.letsplant.data

import com.google.firebase.database.Exclude

class User {
    var id: String? = null
    var email: String? = null
    var location: String? = null
    var latitude = 0.0
    var longitude = 0.0
    var phone: String? = null
    var profileImage = ""
    var username: String? = null

    constructor()
    constructor(
        id: String?,
        email: String?,
        location: String?,
        latitude: Double,
        longitude: Double,
        phone: String?,
        profileImage: String,
        username: String?
    ) {
        this.id = id
        this.email = email
        this.location = location
        this.latitude = latitude
        this.longitude = longitude
        this.phone = phone
        this.profileImage = profileImage
        this.username = username
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["id"] = id
        result["email"] = email
        result["location"] = location
        result["latitude"] = latitude
        result["longitude"] = longitude
        result["phone"] = phone
        result["profileImage"] = profileImage
        result["username"] = username
        return result
    }
}