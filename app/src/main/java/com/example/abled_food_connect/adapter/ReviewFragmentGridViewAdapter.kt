package com.example.abled_food_connect.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.MainFragmentActivity
import com.example.abled_food_connect.data.ReviewFragmentLodingDataItem
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewDetailViewRvActivity

class ReviewFragmentGridViewAdapter (val profileList: ArrayList<ReviewFragmentLodingDataItem>) : RecyclerView.Adapter<ReviewFragmentGridViewAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_fragment_grid_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {



        Glide.with(holder.reveiwPicture.context)
            .load(holder.reveiwPicture.context.getString(R.string.http_request_base_url)+profileList.get(position).review_picture_0)
            .apply(RequestOptions().centerCrop())
            .into(holder.reveiwPicture)


        holder.reveiwPicture.setOnClickListener(View.OnClickListener {
            var reviewDetailViewIntent = Intent(holder.reveiwPicture.context, ReviewDetailViewRvActivity::class.java)
            reviewDetailViewIntent.putExtra("review_id", profileList.get(position).review_id.toString())
            startActivity(holder.reveiwPicture.context,reviewDetailViewIntent,null)
        })



    }

    fun addItem(prof: ReviewFragmentLodingDataItem){

        profileList.add(prof)
        notifyDataSetChanged()

    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reveiwPicture = itemView.findViewById<ImageView>(R.id.reveiwPicture)

    }

}
