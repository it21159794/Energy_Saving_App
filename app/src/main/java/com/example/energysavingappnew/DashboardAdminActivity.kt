package com.example.energysavingappnew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.energysavingappnew.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //arrayList to hold locations
    private lateinit var locationArrayList: ArrayList<ModelLocation>

    //adapter
    private lateinit var adapterLocation: AdapterLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadLocations()

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //called as and when user type anything
                try {
                    adapterLocation.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        //handle click, logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        //handle click, start add location page
        binding.addLocationBtn.setOnClickListener{
            startActivity(Intent(this, LocationAddActivity::class.java))
        }

        //handle click, start add details page
        binding.addLocationFab.setOnClickListener{
            startActivity(Intent(this, OutageAddActivity::class.java))
        }

        binding.locationRv.setOnClickListener{
            startActivity(Intent(this, OutageListAdminActivity::class.java))
        }
    }

    private fun loadLocations() {
        //init arrayList
        locationArrayList = ArrayList()

        //get all locations from firebase data.. Firebase DB > Locations
        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                //clear list before starting adding data into it
                locationArrayList.clear()
                for (ds in snapshot.children){
                    //get data as model
                    var model = ds.getValue(ModelLocation::class.java)

                    //add to arraylist
                    locationArrayList.add(model!!)
                }
                //setup adapter
                adapterLocation = AdapterLocation(this@DashboardAdminActivity, locationArrayList)
                //set adapter to recyclerview
                binding.locationRv.adapter = adapterLocation
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //not logged in, goto main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            //logged in, get and user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text = email
        }

    }
}