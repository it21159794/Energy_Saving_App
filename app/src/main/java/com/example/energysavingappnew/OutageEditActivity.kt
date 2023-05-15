package com.example.energysavingappnew

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.energysavingappnew.databinding.ActivityOutageEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutageEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityOutageEditBinding

    private companion object{
        private const val TAG = "PDF_EDIT_TAG"
    }

    //outage id get from intent started from adapterOutageAdmin
    private var outageId = ""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold location titles
    private lateinit var locationTitleArrayList:ArrayList<String>

    //arraylist to hold location ids
    private lateinit var locationIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id to edit the book info
        outageId = intent.getStringExtra("outageId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadLocations()
        loadOutageInfo()

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, pick location
        binding.locationTv.setOnClickListener{
            locationDialog()
        }

        //handle click, begin update
        binding.submitBtn.setOnClickListener{
            validateData()
        }
    }

    private fun loadOutageInfo() {
        Log.d(TAG, "loadOutageInfo: Loading outage info")

        val ref = FirebaseDatabase.getInstance().getReference("Outages")
        ref.child((outageId))
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get outage info
                    selectedLocationId = snapshot.child("locationId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()

                    //set to views
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)

                    //load outage location info using locationId
                    Log.d(TAG, "onDataChange: Loading outage location info")
                    val refOutageLocation = FirebaseDatabase.getInstance().getReference("Locations")
                    refOutageLocation.child(selectedLocationId)
                        .addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get location
                                val location = snapshot.child("location").value
                                //set to text
                                binding.locationTv.text = location.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private var title = ""
    private var description = ""
    private fun validateData() {
        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
        }
        else if (selectedLocationId.isEmpty()){
            Toast.makeText(this, "Pick location", Toast.LENGTH_SHORT).show()
        }
        else{
            updateOutage()
        }
    }

    private fun updateOutage() {
        Log.d(TAG, "updateOutage: Starting updating outage info...")

        //show progress
        progressDialog.setMessage("Updating outage info...")
        progressDialog.show()

        //setup data to update to db
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["locationId"] = "$selectedLocationId"


        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Outages")
        ref.child(outageId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateOutage: Updated successfully...")
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Log.d(TAG, "updateOutage: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private var selectedLocationId = ""
    private var selectedLocationTitle = ""

    private fun locationDialog() {
        //show dialog to pick the location of outage. we already get the locations

        //make string array from arraylist of string
        val locationsArray = arrayOfNulls<String>(locationTitleArrayList.size)
        for(i in locationTitleArrayList.indices){
            locationsArray[i] = locationTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Location")
            .setItems(locationsArray){dialog, position ->
                //Handle click, save clicked location id and title
                selectedLocationId = locationIdArrayList[position]
                selectedLocationTitle = locationTitleArrayList[position]

                //set to textview
                binding.locationTv.text = selectedLocationTitle
            }
            .show() //show dialog
    }

    private fun loadLocations() {
        Log.d(TAG, "LoadLocations: loading locations..")

        locationTitleArrayList = ArrayList()
        locationIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into them
                locationIdArrayList.clear()
                locationTitleArrayList.clear()

                for (ds in snapshot.children) {
                    val id = "${ds.child("id").value}"
                    val location = "${ds.child("location").value}"

                    locationIdArrayList.add(id)
                    locationTitleArrayList.add(location)

                    Log.d(TAG, "onDataChange: Location ID $id")
                    Log.d(TAG, "onDataChange: Location $location")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}