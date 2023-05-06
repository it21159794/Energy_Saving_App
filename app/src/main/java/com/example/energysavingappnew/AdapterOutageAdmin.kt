package com.example.energysavingappnew

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.energysavingappnew.databinding.RowOutageAdminBinding

class AdapterOutageAdmin :RecyclerView.Adapter<AdapterOutageAdmin.HolderOutageAdmin>, Filterable {

    //context
    private var context: Context

    //arraylist to hold outage
    public var outageArrayList: ArrayList<ModelOutage>
    private val filterList:ArrayList<ModelOutage>

    //viewBinding
    private lateinit var  binding:RowOutageAdminBinding

    //filter object
    private var filter: FilterOutageAdmin? = null

    //constructor
    constructor(context: Context, outageArrayList: ArrayList<ModelOutage>) : super() {
        this.context = context
        this.outageArrayList = outageArrayList
        this.filterList = outageArrayList
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOutageAdmin {
        //bind/infalte layout row_outage_admin.xml
        binding = RowOutageAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderOutageAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderOutageAdmin, position: Int) {
        //Get Date, Set Data, Handle click etc

        //get data
        val model = outageArrayList[position]
        val outageId = model.id
        val locationId = model.locationId
        val title = model.title
        val description = model.description
        val timestamp = model.timestamp

        //convert timestamp tp dd/MM/yyyy
        val formatDate = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formatDate

        //load future details like location

        //load location
        MyApplication.loadLocation(locationId, holder.locationTv)

        //we don't need.........

    }

    override fun getItemCount(): Int {
        return outageArrayList.size //item count
    }


    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterOutageAdmin(filterList, this)
        }

        return filter as FilterOutageAdmin
    }

    //view holder class for row_outage_admin.xml
    inner class HolderOutageAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        //UI views of row_outage_admin.xml
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val locationTv = binding.locationTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn

    }
}