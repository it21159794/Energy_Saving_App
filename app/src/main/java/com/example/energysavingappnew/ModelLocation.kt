package com.example.energysavingappnew

class ModelLocation {
    //validate, must match as in firebase
    var id:String = ""
    var location:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    //empty constructor, required by firebase
    constructor()

    constructor(id: String, location: String, timestamp: Long, uid: String){
        this.id = id
        this.location = location
        this.timestamp = timestamp
        this.uid = uid
    }
}