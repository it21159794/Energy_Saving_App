package com.example.energysavingappnew

import android.widget.Filter
import androidx.constraintlayout.widget.ConstraintSet.Constraint

//used to filter data from recyclerview / search outage from outage list in recyclerview
class FilterOutageAdmin : Filter {
    //arraylist in which we want to search
    var filterList: ArrayList<ModelOutage>

    //adpter in which filter need to be implemented
    var adapterOutageAdmin: AdapterOutageAdmin

    //constructor
    constructor(filterList: ArrayList<ModelOutage>, adapterOutageAdmin: AdapterOutageAdmin) {
        this.filterList = filterList
        this.adapterOutageAdmin = adapterOutageAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint //value to search
        val results = FilterResults()

        //value to be searched mot be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //change to upper case, or lowercase tp avoid case sensitivity
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<ModelOutage>()
            for (i in filterList.indices){
                //validate if match
                if (filterList[i].title.lowercase().contains(constraint)){
                    //seared value is similar to value in list, add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterOutageAdmin.outageArrayList = results.values as ArrayList<ModelOutage>

        //notify changes
        adapterOutageAdmin.notifyDataSetChanged()
    }
}