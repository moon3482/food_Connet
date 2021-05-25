package com.example.abled_food_connect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.Datas.ReviewFragmentLodingDataItem
import com.example.abled_food_connect.R

class ReviewFragmentGridViewAdapter (val profileList: ArrayList<ReviewFragmentLodingDataItem>) : RecyclerView.Adapter<ReviewFragmentGridViewAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_fragment_grid_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {



        Glide.with(holder.gender.context)
            .load("http://3.37.36.188/"+profileList.get(position).review_picture_0)
            .apply(RequestOptions().centerCrop())
            .into(holder.gender)



        holder.gender.setOnClickListener {
                view-> removeItem(position)
        }
    }

    fun addItem(prof: ReviewFragmentLodingDataItem){

        profileList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        profileList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gender = itemView.findViewById<ImageView>(R.id.gender_Iv)

    }

}
