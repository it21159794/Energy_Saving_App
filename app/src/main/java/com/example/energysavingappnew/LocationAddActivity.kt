package com.example.energysavingappnew

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.energysavingappnew.databinding.ActivityLocationAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LocationAddActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityLocationAddBinding

    //firebase auth
    private lateinit var firebaseAuth:FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, begin upload location
        binding.submitBtn.setOnClickListener{
            validateData()
        }
    }

    private var location = ""

    private fun validateData() {
        //validate data

        //get data
        location = binding.locationEt.text.toString().trim()

        //validate data
        if (location.isEmpty()){
            Toast.makeText(this, "Enter Location", Toast.LENGTH_SHORT).show()
        }
        else{
            addLocationFirebase()
        }

    }

    private fun addLocationFirebase() {
        //show progress
        progressDialog.show()

        //get timestap
        val timestamp = System.currentTimeMillis()

        //setup data to add in firebase db
        val hashMap = HashMap<String,Any>() //second param is Any, because the value could be of any type
        hashMap["id"] = "$timestamp"  //put in string quotes because timestamp is in double, we need in string for id
        hashMap["location"] = location
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to firebase db: Database root > Locations > locationId > location info
        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //added successfully
                progressDialog.dismiss()
                Toast.makeText(this,"Added Successfully...", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{ e->
                //failed to add
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}