package com.example.carebout.view.home

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityHomeBinding
import com.example.carebout.databinding.CustomDialogBinding
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.ClinicDao
import com.example.carebout.view.medical.db.InoculationDao
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    companion object {
        var homeActivity: HomeActivity? = null
    }

    private lateinit var binding: ActivityHomeBinding

    private var isFoatingPopupOpen = false
    private val MIN_SCALE = 0.7f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    private lateinit var db: AppDatabase
    private lateinit var weight: WeightDao
    private lateinit var clinicDao: ClinicDao
    private lateinit var inoculationDao: InoculationDao

    private lateinit var dialog: Dialog

    private var nowPid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeActivity = this

        db = AppDatabase.getInstance(this)!!
        weight = db.weightDao()
        clinicDao = AppDatabase.getInstance(this)!!.getClinicDao()
        inoculationDao = AppDatabase.getInstance(this)!!.getInocDao()

        // DB에 아무것도 없을 때 바로 반려동물 등록 페이지로
        if(db.personalInfoDao().getAllInfo().size == 0) {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 현재 클릭 중인 탭 tint
        binding.bottomTapBarOuter.homeImage.imageTintList = ColorStateList.valueOf(Color.parseColor("#6EC677"))
        binding.bottomTapBarOuter.homeText.setTextColor(Color.parseColor("#6EC677"))

        // 하단 탭바 클릭시 이동
        bottomTabClick(binding.bottomTapBarOuter, this)

        // 플로팅버튼 클릭시 메뉴 세개 보여줌(반려동물 추가, 정보 수정, 체중 기록)
        binding.floatingPopup.setOnClickListener {
            toggleFloatingPopup()
        }
        binding.homePopupMenuContainer.setOnClickListener {
            toggleFloatingPopup()
        }

        val profile = binding.profileViewPager

        profile.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기
        profile.adapter = MyViewPagerAdapter(this, getProfileList(), nowPid) // 어댑터 연결 (이미지 리스트도 보냄)
        profile.orientation = ViewPager2.ORIENTATION_HORIZONTAL  // 가로로 페이지 증가
        profile.setPageTransformer(ZoomOutPageTransformer())   // 다음과 같은 애니메이션 효과 적용
        binding.profileIndicator.setViewPager2(binding.profileViewPager)    // 인디케이터와 뷰페이저 연결

        // 각 페이지마다 선택되었을 때 보여줘야 할 데이터 설정
        profile.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val p = db.personalInfoDao().getAllInfo()
                nowPid = p[position].pid
                setWeightGraph(nowPid)

                val clinicAdapter = RecyclerAdapter(getClinicDataSet())
                binding.clinicRecycler.layoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.HORIZONTAL, false)
                binding.clinicRecycler.adapter = clinicAdapter

                val inoculationAdapter = RecyclerAdapter(getInoculationDataSet())
                binding.inoculationRecycler.layoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.HORIZONTAL, false)
                binding.inoculationRecycler.adapter = inoculationAdapter

                binding.helloName.text = "반가워, " + p[position].name + "!"
                if (p[position].sex == "male") {
                    binding.sex.text = "♂"
                    binding.sex.setTextColor(Color.parseColor("#0099ff"))
                } else {
                    binding.sex.text = "♀"
                    binding.sex.setTextColor(Color.parseColor("#ff005d"))
                }
                binding.birth.text = getAge(p[position].birth)
                binding.breed.text = p[position].breed
                binding.weight.text = db.weightDao().getWeightById(nowPid).last().weight.toString() + "kg"
            }
        })

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)

        setOnHomeMenuItemClick()

    }

    private fun getAge(birth: String): String {
        val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
        val nowList = nowDate.split("-")
        val birthList = birth.split("-")
        var yyyy = nowList[0].toInt() - birthList[0].toInt()
        var MM = nowList[1].toInt() - birthList[1].toInt()
        var dd = nowList[2].toInt() - birthList[2].toInt()
        var age: String = ""

        if (MM > 0) // 생일달 지났으면
            age = yyyy.toString() + "년 " + MM.toString() + "개월"
        else if (MM < 0)    // 생일달 안 지났으면
            age = (yyyy-1).toString() + "년 " + (12+MM).toString() + "개월"
        else if (dd >= 0)   // 생일달 && 날짜 지남
            age = yyyy.toString() + "년 " + MM.toString() + "개월"
        else    // 생일달 && 날짜 안 지남
            age = (yyyy-1).toString() + "년 " + (MM).toString() + "개월"

        return age
    }

    private fun getInoculationDataSet(): ArrayList<Pair<String, String>> {
        val inoculationDS: ArrayList<Pair<String, String>> = arrayListOf()

        for(i in inoculationDao.getInoculationAll(nowPid)) {
            if(i.tag_DHPPL == true)
                inoculationDS.add(Pair("DHPPL", i.date!!))
            if(i.tag_Corona == true)
                inoculationDS.add(Pair("코로나", i.date!!))
            if(i.tag_KC == true)
                inoculationDS.add(Pair("켄넬코프", i.date!!))
            if(i.tag_CVRP == true)
                inoculationDS.add(Pair("CVRP", i.date!!))
            if(i.tag_Heartworm == true)
                inoculationDS.add(Pair("심장사상충", i.date!!))
            if(i.tag_FID == true)
                inoculationDS.add(Pair("FID", i.date!!))
            if(i.tag_FL == true)
                inoculationDS.add(Pair("백혈병", i.date!!))
            if(i.tag_Rabies == true)
                inoculationDS.add(Pair("광견병", i.date!!))
        }

        inoculationDS.sortBy { it.second }

        return inoculationDS
    }

    private fun getClinicDataSet(): ArrayList<Pair<String, String>> {
        val clinicDS: ArrayList<Pair<String, String>> = arrayListOf()

        for(c in clinicDao.getClinicAll(nowPid)) {
            if(c.tag_blood == true)
                clinicDS.add(Pair("피검사", c.date!!))
            if(c.tag_ct == true)
                clinicDS.add(Pair("CT", c.date!!))
            if(c.tag_checkup == true)
                clinicDS.add(Pair("정기검진", c.date!!))
            if(c.tag_mri == true)
                clinicDS.add(Pair("MRI", c.date!!))
            if(c.tag_xray == true)
                clinicDS.add(Pair("X-Ray", c.date!!))
            if(c.tag_ultrasonic == true)
                clinicDS.add(Pair("초음파", c.date!!))
        }

        clinicDS.sortBy { it.second }

        return clinicDS
    }

    inner class ZoomOutPageTransformer() : ViewPager2.PageTransformer {
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
    
    fun getProfileList(): ArrayList<String> {
        val profileList = arrayListOf<String>()
        
        for (p in db.personalInfoDao().getAllInfo()) {
            p.image?.let{ profileList.add(it) }
        }

        return profileList
    }

    fun setWeightGraph(pid: Int) {

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

        for(wList in weight.getWeightById(pid)) {
            addEntry(wList.weight)
        }


    }

    private fun addEntry(weight: Float) {
        val data = binding.weightGraph.data
        // 라인 차트
        data?.let { // 데이터가 null이 아닐 때 실행
            var set: ILineDataSet? = data.getDataSetByIndex(0)
            // 임의의 데이터셋 (0번 부터 시작)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            data.addEntry(Entry(set!!.entryCount.toFloat(), weight), 0) // 데이터 엔트리 추가 Entry(x값, y값)
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

    private fun toggleFloatingPopup() {
        val rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
        val rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward)
        val fPopup: FloatingActionButton = binding.floatingPopup

        if (isFoatingPopupOpen) {
            fPopup.startAnimation(rotateBackward)
        } else {
            fPopup.startAnimation(rotateForward)
        }

        isFoatingPopupOpen = !isFoatingPopupOpen

        toggleHomePopupMenu()
    }

    private fun toggleHomePopupMenu() {
        val popupMenuContainer: FrameLayout = binding.homePopupMenuContainer
        val popupMenu: LinearLayout = binding.homePopupMenu

        if (isFoatingPopupOpen) {
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            popupMenu.startAnimation(fadeIn)
            popupMenu.visibility = View.VISIBLE

            val fadeInBackground = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            popupMenuContainer.startAnimation(fadeInBackground)
            popupMenuContainer.visibility = View.VISIBLE
        } else {
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            popupMenu.startAnimation(fadeOut)
            popupMenu.visibility = View.GONE

            val fadeOutBackground = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            popupMenuContainer.startAnimation(fadeOutBackground)
            popupMenuContainer.visibility = View.GONE
        }
    }

    private fun setOnHomeMenuItemClick() {
        binding.menuAddPet.setOnClickListener {
            val intent = Intent(this, AddPetActivity::class.java)
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            startActivity(intent)
        }
        binding.menuAddWeight.setOnClickListener {
            val intent = Intent(this, AddWeightActivity::class.java)
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            intent.putExtra("pid", nowPid)
            startActivity(intent)
        }
        binding.menuEditPet.setOnClickListener {
            val intent = Intent(this, EditPetActivity::class.java)
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            intent.putExtra("pid", nowPid)
            startActivity(intent)
        }
        binding.menuDeletePet.setOnClickListener{
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            showDialog()
        }
    }

    private fun showDialog() {
        val cdBinding = CustomDialogBinding.inflate(layoutInflater)
        dialog.show()

        // 아니오 버튼
        cdBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        // 예 버튼
        cdBinding.btnYes.setOnClickListener {
            Log.e("Yes", "Asdfa")
            val weightList = weight.getWeightById(nowPid)

            for (w in weightList) {
                val delW = Weight(w.pid, w.weight, w.date)
                delW.weightId = w.weightId
                weight.deleteInfo(delW)
            }

            val delP = db.personalInfoDao().getInfoById(nowPid)!!
            delP.pid = nowPid
            db.personalInfoDao().deleteInfo(delP)

            this@HomeActivity.finish()
            this@HomeActivity.startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
        }
    }
}
