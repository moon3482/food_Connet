package com.example.abled_food_connect.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ActionAlarmListDataItem

class ActionAlarmRvAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var datas = ArrayList<ActionAlarmListDataItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {

            //부모댓글일때
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_parent_comment,
                    parent,
                    false
                )
                MultiViewHolder0(view)
            }

            //자식댓글일때
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_child_comment,
                    parent,
                    false
                )
                MultiViewHolder1(view)
            }

            //좋아요가 눌렸을때
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_like_click,
                    parent,
                    false
                )
                MultiViewHolder2(view)
            }

            //else도 좋아요가 눌렸을때이다. 추가할때 수정요망
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_like_click,
                    parent,
                    false
                )
                MultiViewHolder2(view)
            }
        }
    }
    override fun getItemCount(): Int = datas.size


    override fun getItemViewType(position: Int): Int {
        var type : Int = 0

        if(datas[position].action_type =="parent_comment"){
            type = 0
        } else if(datas[position].action_type =="child_comment"){
            type = 1
        } else if(datas[position].action_type =="review_like_btn"){
            type = 2
        }

        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            0 -> {
                (holder as MultiViewHolder0).bind(datas[position])

            }
            1 -> {
                (holder as MultiViewHolder1).bind(datas[position])

            }

            2 -> {
                (holder as MultiViewHolder2).bind(datas[position])
            }
        }
    }

    inner class MultiViewHolder0(view: View) : RecyclerView.ViewHolder(view) {

        private val parentCommentClickBtnLL: LinearLayout = view.findViewById(R.id.parentCommentClickBtnLL)
        private val parentCommentDatetimeTv: TextView = view.findViewById(R.id.parentCommentDatetimeTv)
        private val parentCommentTitleTv: TextView = view.findViewById(R.id.parentCommentTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            parentCommentDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }
            var sender_comment_content = spannable{ bold(color(Color.BLACK,item.sender_comment_content)) }

            parentCommentTitleTv.text = which_text_choose+ " 글에 "+ sender_user_tb_nicname + "님이 댓글을 남기셨습니다. : "+ sender_comment_content
        }
    }
    inner class MultiViewHolder1(view: View) : RecyclerView.ViewHolder(view) {

        private val childCommentClickBtnLL: LinearLayout = view.findViewById(R.id.childCommentClickBtnLL)
        private val childCommentDatetimeTv: TextView = view.findViewById(R.id.childCommentDatetimeTv)
        private val childCommentTitleTv: TextView = view.findViewById(R.id.childCommentTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            childCommentDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }
            var sender_comment_content = spannable{ bold(color(Color.BLACK,item.sender_comment_content)) }


            childCommentTitleTv.text = which_text_choose+ " 댓글에 "+ sender_user_tb_nicname + "님이 답글을 남기셨습니다. : "+ sender_comment_content


        }
    }

    inner class MultiViewHolder2(view: View) : RecyclerView.ViewHolder(view) {

        private val userLikeAlertLL: LinearLayout = view.findViewById(R.id.userLikeAlertLL)
        private val userLikeDatetimeTv: TextView = view.findViewById(R.id.userLikeDatetimeTv)
        private val userLikeTitleTv: TextView = view.findViewById(R.id.userLikeTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            userLikeDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }

            userLikeTitleTv.text = which_text_choose+" 글을 "+ sender_user_tb_nicname +"님이 좋아합니다."

        }
    }




    //텍스트 스팬
    fun spannable(func: () -> SpannableString) = func()
    private fun span(s: CharSequence, o: Any) = (if (s is String) SpannableString(s) else s as? SpannableString
        ?: SpannableString("")).apply { setSpan(o, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

    operator fun SpannableString.plus(s: SpannableString) = SpannableString(TextUtils.concat(this, s))
    operator fun SpannableString.plus(s: String) = SpannableString(TextUtils.concat(this, s))

    fun bold(s: CharSequence) = span(s, StyleSpan(Typeface.BOLD))
    fun color(color: Int, s: CharSequence) = span(s, ForegroundColorSpan(color))

}