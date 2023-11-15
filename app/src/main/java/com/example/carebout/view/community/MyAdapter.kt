package com.example.carebout.view.community

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.databinding.ItemRecyclerviewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//항목 뷰를 가지는 역할
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

//항목 구성자: 어댑터
class MyAdapter(val contents: MutableList<String>?):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        // 현재 날짜와 요일 설정
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
            val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
            applyPattern("${koreanDays[Calendar.DAY_OF_WEEK - 1]}")
        }

        val formattedDate = dateFormat.format(currentDate)
        val formattedDay = dayFormat.format(currentDate)

        binding.date.text = formattedDate
        binding.day.text = formattedDay
    }
}

//리사이클러 뷰 꾸미기
class MyDecoration(val context: Context) : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    //모든 항목이 출력된 후 호출
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        /*
        //뷰 크기 계산
        val width = parent.width
        val height = parent.height
        //이미지 크기 계산
        val img: Drawable? = ResourcesCompat.getDrawable(context.resources, R.drawable.kbo, null)
        val drWidth = img?.intrinsicWidth
        val drHeight = img?.intrinsicHeight
        //이미지가 그려질 위치 계산
        val left = width / 2 - drWidth?.div(2) as Int
        val top = height / 2 - drHeight?.div(2) as Int
        //이미지 출력
        c.drawBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.kbo),
            left.toFloat(), top.toFloat(), null
        )
         */
    }

    //각 항목을 꾸미기 위해 호출
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        ViewCompat.setElevation(view, 20.0f)
    }
}