package com.example.abled_food_connect.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.*
import com.example.abled_food_connect.data.MyPageUserScheduleDataItem
import java.util.*
import kotlin.collections.ArrayList

class MyScheduleTodayScheduleListRvAdapter (val ScheduleDataList: ArrayList<MyPageUserScheduleDataItem>) : RecyclerView.Adapter<MyScheduleTodayScheduleListRvAdapter.CustromViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_schedule_today_list_rv_item ,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return ScheduleDataList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {



        if(ScheduleDataList.get(position).name_host == MainActivity.loginUserNickname){
            holder.hostOrGuestIv.setImageResource(R.drawable.ic_bell_ring_alarm)
            holder.hostorGusetTv.setText("호스트")
        }else{
            holder.hostOrGuestIv.setImageResource(R.drawable.ic_multiple_users_silhouette)
            holder.hostorGusetTv.setText("게스트")
        }




        var daySplit = ScheduleDataList.get(position).appointment_day .split("-")


        val cal = Calendar.getInstance()
        cal.set(daySplit[0].toInt(),daySplit[1].toInt()-1,daySplit[2].toInt())




        var dayOfWeekStr :String = "어떤요일"

        when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> dayOfWeekStr = "일요일"
            2 -> dayOfWeekStr = "월요일"
            3 -> dayOfWeekStr = "화요일"
            4 -> dayOfWeekStr = "수요일"
            5 -> dayOfWeekStr = "목요일"
            6 -> dayOfWeekStr = "금요일"
            7 -> dayOfWeekStr = "토요일"

        }






        var timeSplit  = ScheduleDataList.get(position).appointment_time.split(":")

        var scheduleHour = timeSplit[0].toInt()
        var scheduleMinute = timeSplit[1].toInt()

        var amOrPmStr = "AM"
        if( scheduleHour > 11){
            amOrPmStr = "PM"
            if(scheduleHour>12){
                scheduleHour = scheduleHour -12
            }

        }



        holder.meetingDateTv.text = daySplit[1]+"월 "+daySplit[2]+"일 "+dayOfWeekStr+" "+amOrPmStr+" "+scheduleHour.toString()+" : "+scheduleMinute.toString()
        holder.meetingTitleTv.text = ScheduleDataList.get(position).room_title




        var addressSplit = ScheduleDataList.get(position).restaurant_address.split(" ")
        var addressStr = addressSplit[0]+">"+addressSplit[1]
        holder.restaurantAddressTv.text = addressStr+">"+ScheduleDataList.get(position).restaurant_name



        //모임이 진행중인경우
        if(ScheduleDataList.get(position).meeting_result == 0) {
            holder.reviewWritingBtn.setBackgroundColor(Color.GRAY)
            holder.reviewWritingBtn.setOnClickListener({
                Toast.makeText(holder.reviewWritingBtn.context, "모임완료 후 클릭해주세요.", Toast.LENGTH_SHORT).show()
            })
            holder.userEvaluationBtn.setBackgroundColor(Color.GRAY)
            holder.userEvaluationBtn.setOnClickListener({
                Toast.makeText(holder.reviewWritingBtn.context, "모임완료 후 클릭해주세요.", Toast.LENGTH_SHORT).show()
            })
        }
        //방장이 모임이 잘 끝났다고 표시함
        else if(ScheduleDataList.get(position).meeting_result == 1) {
            holder.meetingDateTv.setBackgroundColor(Color.parseColor("#fcba03"))

            //리뷰버튼
            if(ScheduleDataList.get(position).review_result == 0) {
                holder.reviewWritingBtn.setOnClickListener({
                    //리뷰작성페이지로 이동
                    //Toast.makeText(holder.reviewWritingBtn.context, "리뷰작성페이지로 이동", Toast.LENGTH_SHORT).show()

                    var toReviewWritingIntent : Intent = Intent(holder.reviewWritingBtn.context, ReviewWriting::class.java)
                    toReviewWritingIntent.putExtra("room_id", ScheduleDataList.get(position).room_id)

                    startActivity(
                        holder.reviewWritingBtn.context,
                        toReviewWritingIntent,
                        null
                    )


                })
            }

            //모임이 완료 되었을 때
            else if (ScheduleDataList.get(position).review_result == 1) {

                //리뷰가 작성이 된 상태이다.

                holder.reviewWritingBtn.setBackgroundColor(Color.BLACK)
                holder.reviewWritingBtn.text = "작성완료"
                holder.reviewWritingBtn.setOnClickListener({

                    Toast.makeText(holder.reviewWritingBtn.context, "리뷰를 작성하셨습니다.", Toast.LENGTH_SHORT).show()

                })
            }

            //유저평가버튼

            if(ScheduleDataList.get(position).user_evaluation == 0) {
                holder.userEvaluationBtn.setOnClickListener({
                    //유저평가페이지로 이동
                    var toMeetingUserEvaluationActivityIntent : Intent = Intent(holder.userEvaluationBtn.context, MeetingUserEvaluationActivity::class.java)
                    toMeetingUserEvaluationActivityIntent.putExtra("room_id", ScheduleDataList.get(position).room_id)

                    startActivity(
                        holder.userEvaluationBtn.context,
                        toMeetingUserEvaluationActivityIntent,
                        null
                    )

                })
            }

            else if (ScheduleDataList.get(position).user_evaluation == 1) {
                //유저평가가 완료된 상태이다.
                holder.userEvaluationBtn.setBackgroundColor(Color.BLACK)
                holder.userEvaluationBtn.text = "평가완료"
                holder.userEvaluationBtn.setOnClickListener({

                        Toast.makeText(holder.reviewWritingBtn.context, "유저평가를 완료 하셨습니다.", Toast.LENGTH_SHORT).show()

                })
            }



        }
        //모임이 취소된 경우
        else if(ScheduleDataList.get(position).meeting_result == 2) {
            holder.meetingDateTv.setBackgroundColor(Color.parseColor("#595959"))


            holder.reviewWritingBtn.setBackgroundColor(Color.GRAY)
            holder.reviewWritingBtn.setOnClickListener({
                Toast.makeText(holder.reviewWritingBtn.context, "취소된 모임입니다.", Toast.LENGTH_SHORT).show()
            })
            
            holder.userEvaluationBtn.setBackgroundColor(Color.GRAY)
            holder.userEvaluationBtn.setOnClickListener({
                Toast.makeText(holder.reviewWritingBtn.context, "취소된 모임입니다.", Toast.LENGTH_SHORT).show()
            })


        }



    }

    fun addItem(prof: MyPageUserScheduleDataItem){

        ScheduleDataList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        ScheduleDataList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hostOrGuestIv = itemView.findViewById<ImageView>(R.id.hostOrGuestIv)
        val hostorGusetTv = itemView.findViewById<TextView>(R.id.hostorGusetTv)
        val meetingDateTv = itemView.findViewById<TextView>(R.id.meetingDateTv)
        val meetingTitleTv = itemView.findViewById<TextView>(R.id.meetingTitleTv)
        val restaurantAddressTv = itemView.findViewById<TextView>(R.id.restaurantAddressTv)
        //val restaurantNameTv = itemView.findViewById<TextView>(R.id.restaurantNameTv)

        val reviewWritingBtn = itemView.findViewById<TextView>(R.id.reviewWritingBtn)
        val userEvaluationBtn = itemView.findViewById<TextView>(R.id.userEvaluationBtn)





    }

}
