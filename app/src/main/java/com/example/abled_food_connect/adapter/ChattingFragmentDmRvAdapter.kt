package com.example.abled_food_connect.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.DirectMessageActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChattingFragmentDmRvDataItem

class ChattingFragmentDmRvAdapter (val DMArrayList: ArrayList<ChattingFragmentDmRvDataItem>) : RecyclerView.Adapter<ChattingFragmentDmRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingFragmentDmRvAdapter.CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chating_dm_fragments_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return DMArrayList.size
    }

    override fun onBindViewHolder(holder: ChattingFragmentDmRvAdapter.CustromViewHolder, position: Int) {

        Glide.with(holder.dmListProfileIv.context)
            .load(holder.dmListProfileIv.context.getString(R.string.http_request_base_url)+DMArrayList.get(position).your_thumbnail_image)
            .circleCrop()
            .into(holder.dmListProfileIv)


        holder.dmListUserNicNameTv.text = DMArrayList.get(position).your_nick_name
        holder.dmListSendTimeTv.text = DMArrayList.get(position).send_time
        if(DMArrayList.get(position).text_or_image == "Image"){
            holder.dmListMessageTv.text = "사진을 보냈습니다."
        }else{
            holder.dmListMessageTv.text = DMArrayList.get(position).content
        }



        if(DMArrayList.get(position).not_read_message_count_row == 0){
            holder.dmListMessageCountTv.visibility =View.GONE
        }else {
            holder.dmListMessageCountTv.text = DMArrayList.get(position).not_read_message_count_row.toString()
        }


        holder.DMListClickBtn.setOnClickListener {

            var toDirectMessageActivity : Intent = Intent(holder.DMListClickBtn.context, DirectMessageActivity::class.java)
            toDirectMessageActivity.putExtra("writer_user_tb_id", DMArrayList.get(position).your_table_id)
            toDirectMessageActivity.putExtra("clicked_user_NicName", DMArrayList.get(position).your_nick_name)
            toDirectMessageActivity.putExtra("clicked_user_ProfileImage", DMArrayList.get(position).your_thumbnail_image)
            ContextCompat.startActivity(holder.DMListClickBtn.context, toDirectMessageActivity, null)
        }
    }

    fun addItem(prof:ChattingFragmentDmRvDataItem){

        DMArrayList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        DMArrayList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dmListProfileIv = itemView.findViewById<ImageView>(R.id.dmListProfileIv)
        val dmListUserNicNameTv = itemView.findViewById<TextView>(R.id.dmListUserNicNameTv)
        val dmListSendTimeTv = itemView.findViewById<TextView>(R.id.dmListSendTimeTv)
        val dmListMessageTv = itemView.findViewById<TextView>(R.id.dmListMessageTv)
        val dmListMessageCountTv = itemView.findViewById<TextView>(R.id.dmListMessageCountTv)

        val DMListClickBtn = itemView.findViewById<LinearLayout>(R.id.DMListClickBtn)



    }

}