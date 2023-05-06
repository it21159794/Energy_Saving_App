package com.example.energysavingappnew

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.energysavingappnew.databinding.ActivityLocationAddBinding
import com.example.energysavingappnew.databinding.ActivityOutageAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutageAddActivity : AppCompatActivity() {

    //setup view binding activity_outage_add --> ActivityOutageAddBinding
    private lateinit var binding:ActivityOutageAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog (show while uploading pdf)
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold locations
    private lateinit var locationArrayList: ArrayList<ModelLocation>

    //uri of picked pdf------------------------------------------------

    //tag
    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutageAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadOutageLocation()


        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, show location pick dialog
        binding.locationTv.setOnClickListener {
            locationPickDialog()
        }

        //handle click, start (uploading) submit
        binding.submitBtn.setOnClickListener{
            //upload outage infor to firebase db

            validateData()
        }

    }

    private var title = ""
    private var description = ""
    private var location = ""

    private fun uploadOutageInforToDb() {
        progressDialog.setMessage("Saving user info..")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["location"] = "$location"
        hashMap["locationId"] = "$selectedLocationId"
        hashMap["timestamp"] = timestamp
        hashMap["viewCount"] = 0

        //db reference DB > Outages > OutageId > (Outage info)
        val ref = FirebaseDatabase.getInstance().getReference("Outages")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Submitted..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to submit Outage details due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }





    private fun validateData() {
        //validate data
        Log.d(TAG, "validateData: validating data")

        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        location = binding.locationTv.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        }
        else if (location.isEmpty()){
            Toast.makeText(this, "Pick location..", Toast.LENGTH_SHORT).show()
        }
        else{
            //data validated, begin upload
            uploadOutageInforToDb()
        }
    }

    private fun loadOutageLocation() {
        Log.d(TAG, "loadOutageLocation: Loading outage locations")
        //init arrayList
        locationArrayList = ArrayList()

        //db reference to load locations DF > Locations
        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                locationArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelLocation::class.java)
                    //add to arrayList
                    locationArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.location}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectedLocationId = ""
    private var selectedLocationTitle = ""

    private fun locationPickDialog(){
        //get string array of locations from arraylist
        val locationsArray = arrayOfNulls<String>(locationArrayList.size)
        for (i in locationsArray.indices){
            locationsArray[i] = locationArrayList[i].location
        }

        //alert dialog
        val builder  = AlertDialog.Builder(this)
        builder.setTitle("Choose Location")
            .setItems(locationsArray){dialog, which ->
                //handle item click
                //get clicked item
                selectedLocationTitle = locationArrayList[which].location
                selectedLocationId = locationArrayList[which].id
                //set location to textview
                binding.locationTv.text = selectedLocationTitle

                Log.d(TAG, "locationPickDialog: Selected Location ID: $selectedLocationId")
                Log.d(TAG, "locationPickDialog: Selected Location Title: $selectedLocationTitle")
            }
            .show()
    }

}