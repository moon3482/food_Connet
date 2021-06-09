package com.example.abled_food_connect.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewCommentChildActivity
import com.example.abled_food_connect.data.ReviewParentPageCommentGetDataItem

class ReviewParentPageCommentRvAdapter(val childCommentList: ArrayList<ReviewParentPageCommentGetDataItem>,var reviewWritingUserNicname : String) : RecyclerView.Adapter<ReviewParentPageCommentRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewParentPageCommentRvAdapter.CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_comment_parent_page_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return childCommentList.size
    }

    override fun onBindViewHolder(holder: ReviewParentPageCommentRvAdapter.CustromViewHolder, position: Int) {
        Glide.with(holder.commentProfileIv.context)
            .load(holder.commentProfileIv.context.getString(R.string.http_request_base_url)+childCommentList.get(position).profile_image)
            .circleCrop()
            .into(holder.commentProfileIv)

        holder.commentNicnameTv.text = childCommentList.get(position).nick_name
        if(childCommentList.get(position).nick_name == reviewWritingUserNicname){
            holder.isReveiwWriterTv.visibility = View.VISIBLE
        }
        holder.commentWritingTimeTv.text = childCommentList.get(position).comment_Writing_DateTime
        holder.commentContentTv.text = childCommentList.get(position).comment_content

        holder.childCommentCountTv.text = childCommentList.get(position).comment_class_child_count.toString()

        if(childCommentList.get(position).comment_class_child_count>0){
            holder.childCommentOpenBtnLinearLayout.visibility = View.VISIBLE
        }

        holder.childCommentWrtingBtn.setOnClickListener(View.OnClickListener {

            var toMoveChildCommentActivity : Intent = Intent(holder.childCommentWrtingBtn.context, ReviewCommentChildActivity::class.java)
            toMoveChildCommentActivity.putExtra("review_id", childCommentList.get(position).review_id)
            toMoveChildCommentActivity.putExtra("groupNum", childCommentList.get(position).groupNum)
            toMoveChildCommentActivity.putExtra("writing_user_id", childCommentList.get(position).writing_user_id)
            toMoveChildCommentActivity.putExtra("reviewWritingUserNicname",childCommentList.get(position).nick_name)
            toMoveChildCommentActivity.putExtra("commentEtOpen",true)
            startActivity(holder.childCommentWrtingBtn.context as Activity,toMoveChildCommentActivity,null)
        })

        holder.childCommentOpenBtnLinearLayout.setOnClickListener(View.OnClickListener {

            var toMoveChildCommentActivity : Intent = Intent(holder.childCommentWrtingBtn.context, ReviewCommentChildActivity::class.java)
            toMoveChildCommentActivity.putExtra("review_id", childCommentList.get(position).review_id)
            toMoveChildCommentActivity.putExtra("groupNum", childCommentList.get(position).groupNum)
            toMoveChildCommentActivity.putExtra("writing_user_id", childCommentList.get(position).writing_user_id)
            toMoveChildCommentActivity.putExtra("reviewWritingUserNicname",childCommentList.get(position).nick_name)
            startActivity(holder.childCommentWrtingBtn.context as Activity,toMoveChildCommentActivity,null)
        })


    }

    fun addItem(prof:ReviewParentPageCommentGetDataItem){

        childCommentList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        childCommentList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentProfileIv: ImageView = itemView.findViewById(R.id.commentProfileIv)
        val commentNicnameTv: TextView = itemView.findViewById(R.id.commentNicnameTv)
        val isReveiwWriterTv: TextView = itemView.findViewById(R.id.isReveiwWriterTv)
        val commentWritingTimeTv: TextView = itemView.findViewById(R.id.commentWritingTimeTv)
        val commentContentTv: TextView = itemView.findViewById(R.id.commentContentTv)
        val childCommentWrtingBtn: TextView = itemView.findViewById(R.id.childCommentWrtingBtn)
        val childCommentCountTv: TextView = itemView.findViewById(R.id.childCommentCountTv)
        val childCommentOpenBtnLinearLayout: LinearLayout = itemView.findViewById(R.id.childCommentOpenBtnLinearLayout)


    }

}


