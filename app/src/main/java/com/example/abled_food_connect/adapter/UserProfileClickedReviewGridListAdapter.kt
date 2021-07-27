package com.example.abled_food_connect.adapter

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileClickedReviewGridListActivity
import com.example.abled_food_connect.UserProfileClickedReviewVerticalListActivity
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem

class UserProfileClickedReviewGridListAdapter (val reviewDetailViewRvDataArraylist: ArrayList<ReviewDetailViewRvDataItem>) : RecyclerView.Adapter<UserProfileClickedReviewGridListAdapter.CustromViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_fragment_grid_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return reviewDetailViewRvDataArraylist.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        Glide.with(holder.reveiwPicture.context)
            .load(holder.reveiwPicture.context.getString(R.string.http_request_base_url)+reviewDetailViewRvDataArraylist.get(position).review_picture_0)
            .apply(RequestOptions().centerCrop())
            .override(700)
            .into(holder.reveiwPicture)


        holder.reveiwPicture.setOnClickListener(View.OnClickListener {


            var toMoveVerticalListActivityIntent = Intent(holder.reveiwPicture.context, UserProfileClickedReviewVerticalListActivity::class.java)

            //그리드뷰의 어레이리스트를 버티컬뷰 어레이리스트로 사용할 수 있게 넘겨준다.
            toMoveVerticalListActivityIntent.putExtra("reviewDetailViewRvDataArraylist", reviewDetailViewRvDataArraylist)

            //몇번째 포지션 아이템을 선택했는가. 버티컬뷰로 이동했을때 해당 포지션의 스크롤로 이동한다.
            toMoveVerticalListActivityIntent.putExtra("whatClickPositionInGridView", position)

            //그리드뷰에서 클릭한 데이터인가?.
            toMoveVerticalListActivityIntent.putExtra("isSentGridView", "yes")

            startActivity(holder.reveiwPicture.context,toMoveVerticalListActivityIntent,null)
        })



    }

    fun addItem(prof: ReviewDetailViewRvDataItem){

        reviewDetailViewRvDataArraylist.add(prof)
        notifyDataSetChanged()

    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reveiwPicture = itemView.findViewById<ImageView>(R.id.reveiwPicture)

    }

}