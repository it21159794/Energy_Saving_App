package com.example.energysavingappnew

import android.app.Application
import android.media.tv.TvContract.BaseTvColumns
import android.security.identity.AccessControlProfileId
import android.text.format.DateFormat
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MyApplication:Application() {
    override fun onCreate(){
        super.onCreate()
    }

    companion object{
        //a static method to convert timestamp to proper date format, so we can use it everywhere in project, no need to rewrite again
        fun formatTimeStamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
                //format dd/MM/yyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun loadLocation(locationId: String, locationTv: TextView){
            //load location using location id from database
            val ref = FirebaseDatabase.getInstance().getReference("Locations")
            ref.child(locationId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get location
                        val location:String = "${snapshot.child("location").value}"

                        //set location
                        locationTv.text = location
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

    }

}