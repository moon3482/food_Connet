package com.example.abled_food_connect.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewCommentChildActivity
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ReviewParentPageCommentGetDataItem
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewParentPageCommentRvAdapter(val childCommentList: ArrayList<ReviewParentPageCommentGetDataItem>,var reviewWritingUserNicname : String) : RecyclerView.Adapter<ReviewParentPageCommentRvAdapter.CustromViewHolder>(){



    //클릭리스너
    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, position: Int, review_id :Int ,comment_tb_id : Int)

    }

    //클릭리스너 선언
    private lateinit var itemClickListener: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }




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

        //프로필 이미지 클릭시 프로필보기
        holder.commentProfileIv.setOnClickListener(View.OnClickListener {
            var toMoveUserProfileActivity : Intent = Intent(holder.commentProfileIv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", childCommentList.get(position).writing_user_id)
            startActivity(
                holder.commentProfileIv.context,
                toMoveUserProfileActivity,
                null
            )
        })


        holder.commentNicnameTv.text = childCommentList.get(position).nick_name

        //닉네임 클릭시 프로필보기
        holder.commentNicnameTv.setOnClickListener(View.OnClickListener {
            var toMoveUserProfileActivity : Intent = Intent(holder.commentNicnameTv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", childCommentList.get(position).writing_user_id)
            startActivity(
                holder.commentNicnameTv.context,
                toMoveUserProfileActivity,
                null
            )
        })

        if(MainActivity.user_table_id == childCommentList.get(position).writing_user_id){
            holder.commentDotBtn.visibility = View.VISIBLE
        }

        holder.commentDotBtn.setOnClickListener(View.OnClickListener {
            var pop = PopupMenu(holder.commentDotBtn.context,holder.commentDotBtn)

            pop.menuInflater.inflate(R.menu.comment_3_dot_menu, pop.menu)

            // 2. 람다식으로 처리
            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.commentDeleteBtn ->{
                        var builder = AlertDialog.Builder(holder.commentDotBtn.context)
                        builder.setTitle("댓글삭제")
                        builder.setMessage("댓글을 삭제하시겠습니까?")

                        // 버튼 클릭시에 무슨 작업을 할 것인가!
                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                when (p1) {
                                    DialogInterface.BUTTON_POSITIVE ->{
                                        Log.d("TAG", "닫기버튼 클릭")
                                        Log.d("position", position.toString())
                                    }

                                    DialogInterface.BUTTON_NEGATIVE ->{

                                        reviewDeleteBtnClick(holder.commentDotBtn.context,childCommentList.get(position).comment_id,position,childCommentList.get(position).review_id,childCommentList.get(position).comment_class,childCommentList.get(position).groupNum)
                                        Toast.makeText(holder.commentDotBtn.context, "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show()

                                    }

                                }
                            }
                        }

                        builder.setPositiveButton("취소", listener)
                        builder.setNegativeButton("삭제", listener)

                        builder.show()
                    }
                }
                false
            }
            pop.show()
        })

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

            itemClickListener.onClick(it,position,childCommentList.get(position).review_id, childCommentList.get(position).comment_id)

            var toMoveChildCommentActivity : Intent = Intent(holder.childCommentWrtingBtn.context, ReviewCommentChildActivity::class.java)
            toMoveChildCommentActivity.putExtra("review_id", childCommentList.get(position).review_id)
            toMoveChildCommentActivity.putExtra("groupNum", childCommentList.get(position).groupNum)
            toMoveChildCommentActivity.putExtra("writing_user_id", childCommentList.get(position).writing_user_id)
            toMoveChildCommentActivity.putExtra("reviewWritingUserNicname",childCommentList.get(position).nick_name)
            toMoveChildCommentActivity.putExtra("commentEtOpen",true)
            startActivity(holder.childCommentWrtingBtn.context as Activity,toMoveChildCommentActivity,null)
        })

        holder.childCommentOpenBtnLinearLayout.setOnClickListener(View.OnClickListener {

            itemClickListener.onClick(it,position,childCommentList.get(position).review_id, childCommentList.get(position).comment_id)


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
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,getItemCount())
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //삭제버튼 나오는 3dot버튼
        val commentDotBtn: ImageView = itemView.findViewById(R.id.commentDotBtn)

        val commentProfileIv: ImageView = itemView.findViewById(R.id.commentProfileIv)
        val commentNicnameTv: TextView = itemView.findViewById(R.id.commentNicnameTv)
        val isReveiwWriterTv: TextView = itemView.findViewById(R.id.isReveiwWriterTv)
        val commentWritingTimeTv: TextView = itemView.findViewById(R.id.commentWritingTimeTv)
        val commentContentTv: TextView = itemView.findViewById(R.id.commentContentTv)
        val childCommentWrtingBtn: TextView = itemView.findViewById(R.id.childCommentWrtingBtn)
        val childCommentCountTv: TextView = itemView.findViewById(R.id.childCommentCountTv)
        val childCommentOpenBtnLinearLayout: LinearLayout = itemView.findViewById(R.id.childCommentOpenBtnLinearLayout)


    }



    //댓글 삭제버튼
    fun reviewDeleteBtnClick(context : Context, what_click_comment_tb_id:Int,position: Int,review_id:Int,parentOrChild: Int ,groupNum: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.commentDeleteBtn::class.java)
        val comment_del = api.comment_delete_btn_click(MainActivity.user_table_id,what_click_comment_tb_id,review_id,parentOrChild,groupNum)


        comment_del.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val returnString: String = response.body()!!

                    if(returnString =="true"){
                        Log.d("트루", "트루")
                        removeItem(position)
                    }else{
                        Log.d("false", "false")
                    }


                }


            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

}