package com.example.abled_food_connect.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ReviewCommentGetDataItem

class ReviewCommentRvAdapter(var myItemArray: ArrayList<ReviewCommentGetDataItem>, reviewWriterNicname : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = myItemArray
    var reviewWriterNicname = reviewWriterNicname


    //클릭리스너

    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, commentWriterUserTbId: Int, nicname: String, parentOrchild : Int, groupNum : Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.review_comment_item_parent,
                    parent,
                    false
                )
                parent_ViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.review_comment_item_child,
                    parent,
                    false
                )
                child_ViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.review_comment_item_parent,
                    parent,
                    false
                )
                parent_ViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = datas.size

    override fun getItemViewType(position: Int): Int {
        return datas[position].comment_class
    }





    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (datas[position].comment_class) {
            0 -> {
                (holder as parent_ViewHolder).bind(datas[position])

            }
            1 -> {
                (holder as child_ViewHolder).bind(datas[position])

            }
            else -> {
                (holder as parent_ViewHolder).bind(datas[position])
            }
        }
    }

    inner class parent_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val commentProfileIv: ImageView = view.findViewById(R.id.commentProfileIv)
        private val commentNicnameTv: TextView = view.findViewById(R.id.commentNicnameTv)
        private val isReveiwWriterTv: TextView = view.findViewById(R.id.isReveiwWriterTv)
        private val commentWritingTimeTv: TextView = view.findViewById(R.id.commentWritingTimeTv)
        private val commentContentTv: TextView = view.findViewById(R.id.commentContentTv)
        private val childCommentWrtingBtn: TextView = view.findViewById(R.id.childCommentWrtingBtn)

        fun bind(item: ReviewCommentGetDataItem) {

            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+item.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            commentNicnameTv.text = item.nick_name
            if(item.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = item.comment_Writing_DateTime
            commentContentTv.text = item.comment_content


            childCommentWrtingBtn.setOnClickListener {
                itemClickListner.onClick(it,item.writing_user_id, item.nick_name,1,item.groupNum)
            }

        }
    }

    inner class child_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val commentProfileIv: ImageView = view.findViewById(R.id.commentProfileIv)
        private val commentNicnameTv: TextView = view.findViewById(R.id.commentNicnameTv)
        private val isReveiwWriterTv: TextView = view.findViewById(R.id.isReveiwWriterTv)
        private val commentWritingTimeTv: TextView = view.findViewById(R.id.commentWritingTimeTv)
        private val commentContentTv: TextView = view.findViewById(R.id.commentContentTv)
        //private val childCommentWrtingBtn: TextView = view.findViewById(R.id.childCommentWrtingBtn)

        fun bind(item: ReviewCommentGetDataItem) {

            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+item.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            commentNicnameTv.text = item.nick_name
            if(item.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = item.comment_Writing_DateTime
            commentContentTv.text = item.comment_content


//            childCommentWrtingBtn.setOnClickListener {
//                itemClickListner.onClick(it,item.writing_user_id, item.nick_name,1,item.groupNum)
//            }

        }
    }

}