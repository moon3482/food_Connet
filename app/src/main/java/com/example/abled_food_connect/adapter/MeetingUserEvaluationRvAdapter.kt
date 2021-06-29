package com.example.abled_food_connect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MeetingEvaluationUserListRvDataItem

class MeetingUserEvaluationRvAdapter(val meetingEndUserList: ArrayList<MeetingEvaluationUserListRvDataItem>) : RecyclerView.Adapter<MeetingUserEvaluationRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.meeting_user_evaluation_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return meetingEndUserList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        //작성자 프로필
        Glide.with(holder.profileIv.context)
            .load(holder.profileIv.context.getString(R.string.http_request_base_url)+ meetingEndUserList.get(position).thumbnail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.profileIv)

        if(meetingEndUserList.get(position).is_host == true){
            holder.isHostTv.text = "호스트 "
        }else{
            holder.isHostTv.visibility = View.GONE
        }

        holder.userNicNameTv.text = meetingEndUserList.get(position).user_nickname


        holder.userEvaluationBtn.setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(holder.userEvaluationBtn.context,holder.userEvaluationBtn)
            popupMenu.menuInflater.inflate(R.menu.meeting_user_evaluation_pop_up_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.Item1 ->{
                        holder.userEvaluationTv.text = "유쾌함"
                        itemClickListener.onClick(it, position,"유쾌함")
                    }

                    R.id.Item2 -> {
                        holder.userEvaluationTv.text = "고독한미식가"
                        itemClickListener.onClick(it, position,"고독한미식가")
                    }
                    R.id.Item3 ->{
                        holder.userEvaluationTv.text = "재미있음"
                        itemClickListener.onClick(it, position,"재미있음")
                    }
                    R.id.Item4 ->{
                        holder.userEvaluationTv.text = "시끄러움"
                        itemClickListener.onClick(it, position,"시끄러움")
                    }
                    R.id.Item5 -> {
                        holder.userEvaluationTv.text = "무뚝뚝"
                        itemClickListener.onClick(it, position, "무뚝뚝")
                    }
                    R.id.Item6 ->{
                        holder.userEvaluationTv.text = "맛잘알"
                        itemClickListener.onClick(it, position,"맛잘알")
                    }
                    R.id.Item7 ->{
                        holder.userEvaluationTv.text = "친화력갑"
                        itemClickListener.onClick(it, position,"친화력갑")
                    }
                    R.id.Item8 ->{
                        holder.userEvaluationTv.text = "미소지기"
                        itemClickListener.onClick(it, position,"미소지기")
                    }
                    R.id.Item9 ->{
                        holder.userEvaluationTv.text = "부담스러움"
                        itemClickListener.onClick(it, position,"부담스러움")
                    }

                    R.id.Item10 ->{
                        holder.userEvaluationTv.text = "선택안함"
                        itemClickListener.onClick(it, position,"선택안함")
                    }

                }
                true
            })
            popupMenu.show()
        }
    }

    fun addItem(prof:MeetingEvaluationUserListRvDataItem){

        meetingEndUserList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        meetingEndUserList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileIv = itemView.findViewById<ImageView>(R.id.profileIv)
        val isHostTv = itemView.findViewById<TextView>(R.id.isHostTv)
        val userNicNameTv = itemView.findViewById<TextView>(R.id.userNicNameTv)
        val userEvaluationTv = itemView.findViewById<TextView>(R.id.userEvaluationTv)
        val userEvaluationBtn = itemView.findViewById<LinearLayout>(R.id.userEvaluationBtn)


    }


    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int, clickedText: String)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

}