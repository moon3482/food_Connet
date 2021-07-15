package com.example.abled_food_connect.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.RankingFragmentRvDataItem

class RankingFragmentRvAdapter(val rankingList: ArrayList<RankingFragmentRvDataItem>) : RecyclerView.Adapter<RankingFragmentRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_fragments_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

//        if(rankingList.get(position).rank == "1"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }else if(rankingList.get(position).rank == "2"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }else if(rankingList.get(position).rank == "3"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }




        holder.rankTv.text = rankingList.get(position).rank
        if(rankingList.get(position).isTopMenu ==1){
            //메뉴일 경우
            holder.profileImageIv.visibility = View.GONE


            holder.rankTv.setTypeface(null, Typeface.BOLD)
            holder.nicnameTv.setTypeface(null, Typeface.BOLD)
            holder.rankingPointTv.setTypeface(null, Typeface.BOLD)
            holder.tierTv.setTypeface(null, Typeface.BOLD)

        }else{


            holder.profileImageIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+rankingList.get(position).profile_image)
//            Glide.with(holder.profileImageIv.context)
//                .load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+rankingList.get(position).profile_image)
//                .into(holder.profileImageIv)
        }

        holder.nicnameTv.text = rankingList.get(position).user_tb_nicname
        holder.rankingPointTv.text = rankingList.get(position).rank_point.toString()
        holder.tierTv.text = rankingList.get(position).tier



//        holder.gender.setOnClickListener {
//                view-> removeItem(position)
//        }
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun addItem(prof:RankingFragmentRvDataItem){

        rankingList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        rankingList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageIv = itemView.findViewById<ImageView>(R.id.profileImageIv)
        val rankTv = itemView.findViewById<TextView>(R.id.rankTv)
        val nicnameTv = itemView.findViewById<TextView>(R.id.nicnameTv)
        val rankingPointTv = itemView.findViewById<TextView>(R.id.rankingPointTv)

        val tierTv =  itemView.findViewById<TextView>(R.id.tierTv)


    }

}