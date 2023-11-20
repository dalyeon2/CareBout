package com.example.carebout.view.home

import android.graphics.Color
import com.example.carebout.R
import com.example.carebout.databinding.ActivityHomeBinding
import com.example.carebout.view.home.db.Weight
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class WeightGraph(private val binding: ActivityHomeBinding) {

    fun setWeightGraph(weightList: List<Weight>) {
        val wList = weightList.sortedBy { it.date }
        val minWeight = wList.minBy { it.weight - 1 }.weight
        val maxWeight = wList.maxBy { it.weight + 1 }.weight
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
            axisLeft.axisMinimum = minWeight   //y축 왼쪽 표시 데이터 최솟값
            axisLeft.axisMaximum = maxWeight //y축 왼쪽 표시 데이터 최댓값
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

        for(w in wList) {
            addEntry(w.weight)
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
            data.notifyDataChanged() //데이터 변경 알림
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
        setCircleColor(Color.parseColor("#ffffff")) // circle color
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