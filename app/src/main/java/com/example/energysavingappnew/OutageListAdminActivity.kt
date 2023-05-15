package com.example.energysavingappnew

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.energysavingappnew.databinding.ActivityOutageListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutageListAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityOutageListAdminBinding

    private companion object{
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    //category id, title
    private var locationId = ""
    private var location = ""

    //arraylist to hold outage
    private lateinit var outageArrayList: ArrayList<ModelOutage>
    //adapter
    private lateinit var adapterOutageAdmin: AdapterOutageAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutageListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get from intent, that we passed from adapter
        val intent = intent
        locationId = intent.getStringExtra("locationId").toString()
        location = intent.getStringExtra("location").toString()

        //set outage location
        binding.subTitleTv.text = location

        //load outage
        loadOutageList()

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //filter data
                try{
                    adapterOutageAdmin.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG, "OnTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //handle click,
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadOutageList() {
        //init arraylist
        outageArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Outages")
        ref.orderByChild("locationId").equalTo(locationId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    outageArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelOutage::class.java)
                        //add to list
                        if (model != null) {
                            outageArrayList.add(model)
                            Log.d(TAG, "onDataChange : ${model.title} ${model.locationId}" )
                        }
                    }
                    //setup adapter
                    adapterOutageAdmin = AdapterOutageAdmin(this@OutageListAdminActivity, outageArrayList)
                    binding.outageRv.adapter = adapterOutageAdmin
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}