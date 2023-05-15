package com.example.energysavingappnew

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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

        //handle click, show dialog with option 1)Edit Outage 2)Delete Outage
        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        //we don't need.........

    }

    private fun moreOptionsDialog(model: ModelOutage, holder: AdapterOutageAdmin.HolderOutageAdmin) {
        //get id,title of outage
        val outageId = model.id
        val outageTitle = model.title

        //options to show in dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) {dialog, position ->
                //handle item click
                if(position==0){
                    //Edit is clicked
                    val intent = Intent(context, OutageEditActivity::class.java)
                    intent.putExtra("outageId", outageId)//passed outageId, will be used to edit the book
                    context.startActivity(intent)
                }
                else if (position ==1){
                    //Delete is clicked

                    //show confirmation dialog first if you need...
                    MyApplication.deleteOutage(context, outageId, outageTitle)
                }
            }
            .show()
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