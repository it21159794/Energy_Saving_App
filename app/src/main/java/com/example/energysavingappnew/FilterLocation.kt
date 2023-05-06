package com.example.energysavingappnew

import android.widget.Filter

class FilterLocation: Filter {

    //arraylist in which we want to search
    private var filterList: ArrayList<ModelLocation>

    //adpter in which filter need to be implemented
    private var adapterLocation: AdapterLocation

    //constructor
    constructor(filterList: ArrayList<ModelLocation>, adapterLocation: AdapterLocation) : super() {
        this.filterList = filterList
        this.adapterLocation = adapterLocation
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should note be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //searched value is nor null not empty

            //change to upper case, or lower
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelLocation> = ArrayList()
            for (i in 0 until filterList.size){
                //validate
                if (filterList[i].location.uppercase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterLocation.locationArrayList = results.values as ArrayList<ModelLocation>

        //notify changes
        adapterLocation.notifyDataSetChanged()
    }
}