package com.example.energysavingappnew

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.energysavingappnew.databinding.RowLocationBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterLocation:RecyclerView.Adapter<AdapterLocation.HolderLocation>, Filterable {

    private val context: Context
    public var locationArrayList: ArrayList<ModelLocation>
    private var filterList: ArrayList<ModelLocation>

    private var filter: FilterLocation? = null

    private lateinit var binding: RowLocationBinding

    //constructor
    constructor(context: Context, locationArrayList: ArrayList<ModelLocation>) {
        this.context = context
        this.locationArrayList = locationArrayList
        this.filterList = locationArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLocation {
        //inflate/bind row_location.xml
        binding = RowLocationBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderLocation(binding.root)
    }

    override fun getItemCount(): Int {
        return locationArrayList.size //number of items in list
    }

    override fun onBindViewHolder(holder: HolderLocation, position: Int) {
        //Get Data, set data, handle clicks etc

        //get data
        val model = locationArrayList[position]
        val id = model.id
        val location = model.location
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.locationTv.text = location

        //handle click, delete category
        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure want to delete this location?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteLocation(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }

        //handle click, start outage list admin activity, also pass outage id, title
        holder.itemView.setOnClickListener{
            val intent = Intent(context, OutageListAdminActivity::class.java)
            intent.putExtra("locationId", id)
            intent.putExtra("location", location)
            context.startActivity(intent)
        }
    }

    private fun deleteLocation(model: ModelLocation, holder: HolderLocation) {
        //get id of location to declare
        val id = model.id
        //firebase DB > Locations > Locationid
        val ref = FirebaseDatabase.getInstance().getReference("Locations")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }




    //ViewHolder class to hold/init UI views for row_category.xml
    inner class HolderLocation(itemView: View): RecyclerView.ViewHolder(itemView){
        //init ui views
        var locationTv:TextView = binding.locationTv
        var deleteBtn:ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterLocation(filterList, this)
        }
        return filter as FilterLocation
    }


}