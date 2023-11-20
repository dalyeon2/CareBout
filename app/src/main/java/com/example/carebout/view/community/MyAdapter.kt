package com.example.carebout.view.community

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.request.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.example.carebout.databinding.ItemRecyclerviewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.text.ParseException

//항목 뷰를 가지는 역할
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

//항목 구성자: 어댑터
class MyAdapter(
    val contents: MutableList<String>?,
    val imageUris: MutableList<Uri>?,
    val dates: MutableList<String?>,
    val day: MutableList<String?>
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //항목 개수를 판단하기 위해 자동 호출
    override fun getItemCount(): Int {
        return contents?.size ?: 0
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    //항목 뷰를 가지는 뷰 홀더를 준비하기 위해 자동 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
        val binding = ItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = MyViewHolder(binding)

        // 아이템 클릭 리스너 설정
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClickListener?.onItemClick(position)
        }

        return viewHolder
    }

    //각 항목을 구성하기 위해 호출
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding

        //뷰에 데이터 출력
        binding.itemData.text = contents!![position]

        // 이미지를 추가했을 때만 보이도록 처리
        val itemImageUri = imageUris?.getOrNull(position)
        if (itemImageUri != null && itemImageUri != Uri.EMPTY) {
            binding.itemImage.visibility = View.VISIBLE

            Glide.with(holder.itemView.context).clear(binding.itemImage)

            // 이미지 로딩
            val requestOptions = RequestOptions()
                .fitCenter() // 이미지를 중앙에 맞게 조절

            Glide.with(holder.itemView)
                .asBitmap()
                .load(itemImageUri)
                .apply(requestOptions)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Bitmap이 준비되면 ImageView에 설정
                        binding.itemImage.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // 이미지가 로드 해제되면 할 작업이 있다면 여기에 추가
                    }
                })
        } else {
            binding.itemImage.visibility = View.GONE
            binding.itemImage.setImageBitmap(null)
        }

        // 날짜 데이터
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        val itemDate = dates.getOrNull(position)
        binding.date.text = itemDate?.let {
            try {
                val parsedDate = dateFormat.parse(it)
                val newDateFormat = SimpleDateFormat("MM'월' dd'일'", Locale.getDefault())
                newDateFormat.format(parsedDate)
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        } ?: run {
            val currentDate = Calendar.getInstance().time
            val newDateFormat = SimpleDateFormat("MM'월' dd'일'", Locale.getDefault())
            newDateFormat.format(currentDate)
        }

        val itemDay = day.getOrNull(position)
        binding.day.text = when (itemDay) {
            "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일" -> itemDay
            else -> {
                val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
                koreanDays.getOrNull(currentDayOfWeek - 1) ?: "표시되지 않음"
            }
        }
    }
}