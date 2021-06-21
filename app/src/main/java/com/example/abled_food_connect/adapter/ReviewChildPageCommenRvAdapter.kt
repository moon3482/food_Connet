package com.example.abled_food_connect.adapter


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ReviewChildPageCommentGetDataItem

class ReviewChildPageCommenRvAdapter(var myItemArrayChildPage: ArrayList<ReviewChildPageCommentGetDataItem>, reviewWriterNicname : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = myItemArrayChildPage
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

        fun bind(itemChildPage: ReviewChildPageCommentGetDataItem) {

            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+itemChildPage.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            //프로필 이미지 클릭시 프로필엑티비티로 이동
            commentProfileIv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentProfileIv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentProfileIv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })

            commentNicnameTv.text = itemChildPage.nick_name


            //닉네임 클릭시 프로필엑티비티로 이동
            commentNicnameTv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentNicnameTv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentNicnameTv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })

            if(itemChildPage.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = itemChildPage.comment_Writing_DateTime
            commentContentTv.text = itemChildPage.comment_content


            childCommentWrtingBtn.setOnClickListener {
                itemClickListner.onClick(it,itemChildPage.writing_user_id, itemChildPage.nick_name,1,itemChildPage.groupNum)
            }

        }
    }

    inner class child_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val commentProfileIv: ImageView = view.findViewById(R.id.commentProfileIv)
        private val commentNicnameTv: TextView = view.findViewById(R.id.commentNicnameTv)
        private val isReveiwWriterTv: TextView = view.findViewById(R.id.isReveiwWriterTv)
        private val commentWritingTimeTv: TextView = view.findViewById(R.id.commentWritingTimeTv)
        private val commentContentTv: TextView = view.findViewById(R.id.commentContentTv)
        private val childCommentWrtingBtn: TextView = view.findViewById(R.id.childCommentWrtingBtn)

        fun bind(itemChildPage: ReviewChildPageCommentGetDataItem) {

            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+itemChildPage.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            //프로필 이미지 클릭시 프로필엑티비티로 이동
            commentProfileIv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentProfileIv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentProfileIv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })

            commentNicnameTv.text = itemChildPage.nick_name


            //닉네임 클릭시 프로필엑티비티로 이동
            commentNicnameTv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentNicnameTv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentNicnameTv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })



            if(itemChildPage.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = itemChildPage.comment_Writing_DateTime


            //@유저아이디 + 댓글 내용 만들기

            val username = "@"+itemChildPage.sendTargetUserNicName+" "
            val comment = itemChildPage.comment_content
            val text = username + comment

            val startName = text.indexOf(username)
            val endName = startName + username.length
            val startComment = text.indexOf(comment)
            val endComment = text.length

            val spannableString = SpannableString(text)
            val nameClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    var toMoveUserProfileActivity : Intent = Intent(commentWritingTimeTv.context, UserProfileActivity::class.java)
                    toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.sendTargetUserTable_id)
                    startActivity(
                        commentWritingTimeTv.context,
                        toMoveUserProfileActivity,
                        null
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.color = Color.BLUE
                    ds.isUnderlineText = false
                }
            }
            val commentClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }

            spannableString.setSpan(nameClickableSpan, startName, endName, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(commentClickableSpan, startComment, endComment, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            commentContentTv.text = spannableString
            // 문자열에 사이트 링크로 사용되는 href 가 있다면 setMovementMethod()
            //함수를 사용해야 한다. 그렇게 해야 링크를 클릭해서 원하는 페이지로 이동할 수 있다.
            commentContentTv.movementMethod = LinkMovementMethod.getInstance()







            childCommentWrtingBtn.setOnClickListener {
                itemClickListner.onClick(it,itemChildPage.writing_user_id, itemChildPage.nick_name,1,itemChildPage.groupNum)
            }

        }
    }

}