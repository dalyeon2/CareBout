package com.example.carebout.view.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityHomeBinding
import com.example.carebout.view.home.db.PersonalInfo
import com.example.carebout.view.home.db.PersonalInfoDB
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.ClinicDao
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val MIN_SCALE = 0.7f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    private lateinit var db: PersonalInfoDB
    private lateinit var clinicDao: ClinicDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWeightGraph()

        db = PersonalInfoDB.getInstance(this)!!
        clinicDao = AppDatabase.getInstance(this)!!.getClinicDao()

        val profileList: ArrayList<String> = arrayListOf()

        for (p in db.personalInfoDao().getAllInfo()) {
            p.image?.let { profileList.add(it) }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            for (p in db.personalInfoDao().getAllInfo()) {
//                p.image?.let { profileList.add(it) }
//            }
//        }

        val checkDataSet: MutableList<Pair<String, String>> = mutableListOf()
        val dataSet2: MutableList<Pair<String, String>> = mutableListOf()

        for(c in clinicDao.getClinicAll()){
            if(c.tag_blood == true)
                checkDataSet.add(Pair("피검사", c.date!!))
            if(c.tag_ct == true)
                checkDataSet.add(Pair("CT", c.date!!))
            if(c.tag_checkup == true)
                checkDataSet.add(Pair("정기검진", c.date!!))
            if(c.tag_mri == true)
                checkDataSet.add(Pair("MRI", c.date!!))
            if(c.tag_xray == true)
                checkDataSet.add(Pair("X-Ray", c.date!!))
            if(c.tag_ultrasonic == true)
                checkDataSet.add(Pair("초음파", c.date!!))
        }

        for(c in clinicDao.getClinicAll()){
            if(c.tag_blood == true)
                dataSet2.add(Pair("피검사", c.date!!))
            if(c.tag_ct == true)
                dataSet2.add(Pair("CT", c.date!!))
            if(c.tag_checkup == true)
                dataSet2.add(Pair("접종", c.date!!))
        }

        checkDataSet.sortBy { it.second }
        dataSet2.sortBy { it.second }

        val recyclerAdapter = RecyclerAdapter(checkDataSet)
        binding.checkGraph.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.checkGraph.adapter = recyclerAdapter

        bottomTabClick(binding.bottomTapBarOuter, this)

        // 플로팅버튼 클릭시 반려동물 추가 액티비티로
        binding.addProfileBtn.setOnClickListener{
//            CoroutineScope(Dispatchers.IO).launch {
//                Log.d("aaaaaaaaaa", db.weightDao().TEMP().toString())
//                Log.d("aaaaaaaaaa", db.personalInfoDao().getAllInfo().toString())
//            }
            val intent = Intent(this, AddPetActivity::class.java)
            startActivity(intent)
        }

        val profile = binding.profileViewPager

        profile.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기
        profile.adapter = MyViewPagerAdapter(this, profileList) // 어댑터 연결 (이미지 리스트도 보냄)
        profile.orientation = ViewPager2.ORIENTATION_HORIZONTAL  // 가로로 페이지 증가
        profile.setPageTransformer(ZoomOutPageTransformer())   // 다음과 같은 애니메이션 효과 적용
        binding.profileIndicator.setViewPager2(binding.profileViewPager)    // 인디케이터와 뷰페이저 연결

        // 각 페이지마다 선택되었을 때 보여줘야 할 데이터 설정
        profile.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Called when a new page has been selected

                val p = db.personalInfoDao().getAllInfo()

                binding.helloName.text = "반가워, " + p[position].name + "!"
                binding.man.text = p[position].sex
                binding.birth.text = p[position].birth
                binding.weight.text = p[position].breed
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

    private fun getProfileList(db: PersonalInfoDao): MutableList<Pair<Int, String>> {
        val profileList = mutableListOf<Pair<Int, String>>()


        profileList.sortBy{ it.first }

        return profileList
    }

//    private fun

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