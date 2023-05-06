package com.example.energysavingappnew

class ModelOutage {

    //variables
    var uid:String = ""
    var id:String = ""
    var title:String = ""
    var description:String = ""
    var locationId:String = ""
    var timestamp:Long = 0
    var viewCount:Long = 0

    //empty constructor (required by firebase)
    constructor()

    //parameterized constructor
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        locationId: String,
        timestamp: Long,
        viewCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.locationId = locationId
        this.timestamp = timestamp
        this.viewCount = viewCount
    }



}