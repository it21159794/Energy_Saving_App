package com.example.energysavingappnew

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.media.tv.TvContract.BaseTvColumns
import android.security.identity.AccessControlProfileId
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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


        fun deleteOutage(context: Context, outageId: String, outageTitle: String){

            val TAG = "DELETE_OUTAGE_TAG"

            Log.d(TAG, "deleteOutage: deleting..")

            //progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $outageTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

//            Log.d(TAG, "deleteOutage: Deleting from storage..")
//            val storageReference = FirebaseStorage
            val ref = FirebaseDatabase.getInstance().getReference("Outages")
            ref.child(outageId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "deleteOutage: Deleted from db too...")
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteOutage: Failed to delete from db due to ${e.message}")
                    Toast.makeText(context, "Failed to delete from db due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

}