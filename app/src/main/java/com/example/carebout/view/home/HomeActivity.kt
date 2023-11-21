package com.example.carebout.view.home

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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
import com.example.carebout.view.medical.MyPid
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.ClinicDao
import com.example.carebout.view.medical.db.InoculationDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var db: AppDatabase
    private lateinit var weightDao: WeightDao
    private lateinit var clinicDao: ClinicDao
    private lateinit var inoculationDao: InoculationDao

    private lateinit var dialog: Dialog
    private lateinit var cAdapter: RecyclerAdapter
    private lateinit var iAdapter: RecyclerAdapter
    private lateinit var wGraph: WeightGraph
    private lateinit var vpAdapter: MyViewPagerAdapter

    private var nowPid: Int = 0
    private var nowPosition: Int = 0
    private var isFoatingPopupOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        weightDao = db.weightDao()
        clinicDao = AppDatabase.getInstance(this)!!.getClinicDao()
        inoculationDao = AppDatabase.getInstance(this)!!.getInocDao()

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

        initDialog()
        setOnHomeMenuItemClick()

    }

    override fun onResume() {
        super.onResume()

        // DB에 아무것도 없을 때 바로 반려동물 등록 페이지로
        if(db.personalInfoDao().getAllInfo().isEmpty()) {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            setViewPager()
        }

    }

    private fun setViewPager() {
        val profile = binding.profileViewPager
        vpAdapter = MyViewPagerAdapter(this, getProfileList())

        profile.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기
        profile.adapter = vpAdapter
        profile.orientation = ViewPager2.ORIENTATION_HORIZONTAL  // 가로로 페이지 증가
        profile.setPageTransformer(ZoomOutPageTransformer())   // 다음과 같은 애니메이션 효과 적용
        binding.profileIndicator.setViewPager2(binding.profileViewPager)    // 인디케이터와 뷰페이저 연결

        // 각 페이지마다 선택되었을 때 보여줘야 할 데이터 설정
        profile.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val p = db.personalInfoDao().getAllInfo()

                nowPosition = position
                nowPid = p[nowPosition].pid
              
                //현재 화면의 pid 저장해 공유하는 것, 별로라도 일단 지우지 말아주세요ㅜㅠ
                MyPid.setPid(nowPid)

                setWeightGraph()
                setClinicRecycler()
                setInoculationRecycler()

                binding.helloName.text = "반가워, " + p[nowPosition].name + " !"
                if (p[nowPosition].sex == "male") {
                    binding.sex.text = "♂"
                    binding.sex.setTextColor(Color.parseColor("#0099ff"))
                } else {
                    binding.sex.text = "♀"
                    binding.sex.setTextColor(Color.parseColor("#ff005d"))
                }
                binding.birth.text = getAge(p[nowPosition].birth)
                binding.breed.text = p[nowPosition].breed
                binding.weight.text = db.weightDao().getWeightById(nowPid).last().weight.toString() + "kg"
            }
        })
    }

    private fun setWeightGraph() {
        wGraph = WeightGraph(binding)
        wGraph.setWeightGraph(weightDao.getWeightById(nowPid))
    }

    private fun setClinicRecycler() {
        cAdapter = RecyclerAdapter(getClinicDataSet())
        binding.checkRecycler.layoutManager = LinearLayoutManager(
            this@HomeActivity, RecyclerView.HORIZONTAL, false).apply {
                stackFromEnd = true
        }
        binding.checkRecycler.adapter = cAdapter
    }

    private fun setInoculationRecycler() {
        iAdapter = RecyclerAdapter(getInoculationDataSet())
        binding.inoculationRecycler.layoutManager = LinearLayoutManager(
            this@HomeActivity, RecyclerView.HORIZONTAL, false).apply {
                stackFromEnd = true
        }
        binding.inoculationRecycler.adapter = iAdapter
    }

    private fun getAge(birth: String): String {
        val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
        val nowList = nowDate.split("-")
        val birthList = birth.split("-")
        val yyyy = nowList[0].toInt() - birthList[0].toInt()
        val MM = nowList[1].toInt() - birthList[1].toInt()
        val dd = nowList[2].toInt() - birthList[2].toInt()
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
                inoculationDS.add(Pair("복막염", i.date!!))
            if(i.tag_FL == true)
                inoculationDS.add(Pair("백혈병", i.date!!))
            if(i.tag_Rabies == true)
                inoculationDS.add(Pair("광견병", i.date!!))
        }

        inoculationDS.sortBy { it.second }

        return inoculationDS
    }

    private fun getProfileList(): ArrayList<String> {
        val profileList = arrayListOf<String>()

        for (p in db.personalInfoDao().getAllInfo()) {
            profileList.add( p.image.ifEmpty { p.animal } )
        }

        return profileList
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
        binding.menuEditPet.setOnClickListener {
            val intent = Intent(this, EditPetActivity::class.java)
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            intent.putExtra("pid", nowPid)
            startActivity(intent)
        }
        binding.menuAddWeight.setOnClickListener {
            val intent = Intent(this, AddWeightActivity::class.java)
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            intent.putExtra("pid", nowPid)
            startActivity(intent)
        }
        binding.menuDeletePet.setOnClickListener{
            toggleFloatingPopup() // 메뉴 팝업 창을 닫습니다.
            dialog.show()
        }
    }

    private fun initDialog(){
        val cdBinding = CustomDialogBinding.inflate(layoutInflater)

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(cdBinding.root)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        // 아니오 버튼
        cdBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        // 예 버튼
        cdBinding.btnYes.setOnClickListener {
            // 몸무게 삭제
            val weightList = weightDao.getWeightById(nowPid)
            for (w in weightList) {
                val delW = Weight(w.pid, w.weight, w.date)
                delW.weightId = w.weightId
                weightDao.deleteInfo(delW)
            }

            // 검사 내역 삭제
            val clin = db.getClinicDao().getClinicByPid(nowPid)
            if (clin != null) {
                db.getClinicDao().deletePidClinic(nowPid)
            }

            // 접종 및 구충 내역 삭제
            val inoc = db.getInocDao().getInoculationByPid(nowPid)
            if (inoc != null) {
                db.getInocDao().deletePidInoc(nowPid)
            }

            // medi 삭제
            val medi = db.getMedicineDao().getMediByPid(nowPid)
            if (medi != null) {
                db.getMedicineDao().deletePidMedi(nowPid)
            }

            // 반려동물 삭제
            val delP = db.personalInfoDao().getInfoById(nowPid)!!
            delP.pid = nowPid
            db.personalInfoDao().deleteInfo(delP)
            vpAdapter.removeItem(nowPosition)

            dialog.dismiss()
            
            // 삭제 후 반려동물이 하나도 없다면 EmptyActivity로
            if(db.personalInfoDao().getAllInfo().isEmpty()) {
                val intent = Intent(this, EmptyActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
}