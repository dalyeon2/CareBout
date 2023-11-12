package com.example.carebout.view.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityHomeBinding
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.ClinicDao
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val MIN_SCALE = 0.7f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    private lateinit var clinicDao: ClinicDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.helloName.text = "반가워, 쿵이야"
        setWeightGraph()



        clinicDao = AppDatabase.getInstance(this)!!.getClinicDao()

        val dataSet: MutableList<Pair<String, String>> = mutableListOf()

        for(c in clinicDao.getClinicAll()){
            if(c.tag_blood == true)
                dataSet.add(Pair("피검사", c.date!!))
            if(c.tag_ct == true)
                dataSet.add(Pair("CT", c.date!!))
            if(c.tag_checkup == true)
                dataSet.add(Pair("접종", c.date!!))
            if(c.tag_mri == true)
                dataSet.add(Pair("MRI", c.date!!))
            if(c.tag_xray == true)
                dataSet.add(Pair("X-Ray", c.date!!))
            if(c.tag_ultrasonic == true)
                dataSet.add(Pair("초음파", c.date!!))
        }

        dataSet.sortBy { it.second }

//        val dataSet: MutableList<Pair<String, String>> =
//            mutableListOf(Pair("X-ray", "01-04"), Pair("피검사", "01-05"), Pair("초음파", "01-06"),
//                Pair("피검사", "02-10"), Pair("X-ray", "03-10"), Pair("CT", "03-12"), Pair("MRI", "03-14"),
//                Pair("피검사", "04-05"), Pair("초음파", "04-10"), Pair("X-ray", "04-12"), Pair("피검사", "05-12"),
//                Pair("초음파", "05-14"))

        val recyclerAdapter = RecyclerAdapter(dataSet)
        binding.checkGraph.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.checkGraph.adapter = recyclerAdapter

        bottomTabClick(binding.bottomTapBarOuter, this)

        // 프로필 클릭시 반려동물 추가 액티비티로 (임시)
//        binding.profileImage.setOnClickListener{
//            val intent = Intent(this, AddPetActivity::class.java)
//            startActivity(intent)
//        }

        /* 여백, 너비에 대한 정의 */
//        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.viewPagerMargin) // dimen 파일 안에 크기를 정의해두었다
//        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.viewPagerWidth) // dimen 파일이 없으면 생성해야함
//        val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
//        val offsetPx = screenWidth - pageMarginPx - pagerWidth

//        binding.profileViewPager.setPageTransformer { page, position ->
//            page.translationX = position * (-offsetPx) }

        binding.profileViewPager.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기

        binding.profileViewPager.adapter = MyViewPagerAdapter(getProfileList())
        binding.profileViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.profileViewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.profileIndicator.setViewPager2(binding.profileViewPager)

        binding.profileViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Called when the scroll state changes (starting, stopping, or changing position)
            }

            override fun onPageSelected(position: Int) {
                // Called when a new page has been selected

            }

            override fun onPageScrollStateChanged(state: Int) {
                // Called when the page is scrolled
            }
        })


    }

    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // 왼쪽 페이지로 이동
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }

    private fun getProfileList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.koong, R.drawable.moong, R.drawable.sunset,
            R.drawable.stray1, R.drawable.stray2)
    }

    private fun setWeightGraph() {

        val xAxis: XAxis = binding.weightGraph.xAxis   //x축 가져오기

        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   //x축은 아래에 위치
            setEnabled(false)   // x축 안 보이기
            textSize = 10f  //텍스트 크기
            setDrawGridLines(false)  //x축 그리드 no. true가 기본값
            isGranularityEnabled = true //x축 간격 세분화 ok
            granularity = 1f    //x축 데이터 간격
            spaceMin = 0.3f //x축 1번 데이터와 왼쪽 y축 사이 간격
            spaceMax = 0.3f //x축 마지막 데이터와 오른쪽 y축 사이 간격
        }

        binding.weightGraph.apply {
            axisRight.isEnabled = false //y축 오른쪽 비활성화
            axisLeft.isEnabled = false  //y축 왼쪽 비활성화
            axisLeft.axisLineColor = resources.getColor(R.color.white)  // 다크모드를 위해 배경색이랑 같게 설정
            axisLeft.axisMinimum = 2f   //y축 왼쪽 표시 데이터 최솟값
            axisLeft.axisMaximum = 7.2f //y축 왼쪽 표시 데이터 최댓값
            axisLeft.setLabelCount(4, true)
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawGridLines(true)
            axisLeft.setDrawZeroLine(false)
            legend.apply {
                textSize = 15f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP  //수직 조정 : 위로
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 수평 조정 : 가운데로
                orientation = Legend.LegendOrientation.HORIZONTAL   //범례와 차트 정렬 : 수평
                setEnabled(false)
                setDrawInside(false)    //차트 안에 X
            }
        }

        val lineData = LineData()
        binding.weightGraph.data = lineData

        addEntry(3f)
        addEntry(3.5f)
        addEntry(4f)
        addEntry(4.4f)
        addEntry(5f)
        addEntry(5.2f)
        addEntry(5.4f)
        addEntry(5.6f)
        addEntry(6f)
        addEntry(6.2f)
        addEntry(5.9f)
        addEntry(5.7f)
        addEntry(5.3f)
        addEntry(5.1f)


    }

    private fun addEntry(a:Float) {
        val data = binding.weightGraph.data
        // 라인 차트
        data?.let { // 데이터가 null이 아닐 때 실행
            var set: ILineDataSet? = data.getDataSetByIndex(0)
            // 임의의 데이터셋 (0번 부터 시작)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            data.addEntry(Entry(set!!.entryCount.toFloat(), a), 0) // 데이터 엔트리 추가 Entry(x값, y값)
            data.notifyDataChanged() //
            binding.weightGraph.apply {
                notifyDataSetChanged() //
                moveViewToX(data.entryCount.toFloat()) // 좌우 스크롤
                setVisibleXRangeMaximum(8f) // 한 화면(?)에 최대 7개의 점을 보여줄 수 있음
                setPinchZoom(false) // 두손가락으로 확대 X
                isDoubleTapToZoomEnabled = false // 더블탭 확대 X
                description = null
            }
        }
    }

    private fun createSet() = LineDataSet(null, null).apply {
        axisDependency = YAxis.AxisDependency.LEFT // Y
        color = Color.parseColor("#A7A7A7") // line color
        setCircleColor(resources.getColor(R.color.white)) // circle color
        circleHoleColor = Color.parseColor("#6EC677") // fill circle 6EC677
        valueTextSize = 10f // 값(숫자) 크기
        lineWidth = 2.5f // 선 두께
        circleRadius = 6f // 외경 크기
        circleHoleRadius = 4f   // 내경 크기
        fillAlpha = 0 // 라인 색투명도
        valueTextColor = Color.parseColor("#A7A7A7")    //값(숫자) 색
        highLightColor = Color.BLACK //
        setDrawValues(true) // 원 위에 값 써줄까? ok
    }


}