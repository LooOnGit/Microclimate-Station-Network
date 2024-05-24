package com.example.loofarm.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.DropBoxManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentFarmBinding
import com.example.loofarm.databinding.FragmentHistoryValueBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.collection.LLRBNode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryValueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryValueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHistoryValueBinding

    val profitValues = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryValueBinding.inflate(layoutInflater, container, false)

        dataListing()

        initEvents()

        return binding.root
    }

    private fun initEvents() {
        binding.btnOutHistory.setOnClickListener(){
            val targetFragment = FarmFragment()
            // Thực hiện transaction để chuyển đổi Fragment
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction().setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
            transaction.replace(R.id.frLayout, targetFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    fun dataListing(){
        profitValues.add(Entry(0f, 134f))
        profitValues.add(Entry(1f, 53f))
        profitValues.add(Entry(2f, 100f))
        profitValues.add(Entry(3f, 120f))
        profitValues.add(Entry(4f, 135f))
        profitValues.add(Entry(5f, 225f))
        setChart()
    }

    fun setChart(){
        val dataSet: LineDataSet

        if(binding.chart.data !=  null && binding.chart.data.dataSetCount > 0){
            dataSet = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            dataSet.values = profitValues
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        }else{
            dataSet = LineDataSet(profitValues, "Cảm biến")
            dataSet.setColors(Color.RED)
            dataSet.valueTextColor = Color.WHITE
            dataSet.circleColors = listOf(Color.BLUE)
            dataSet.lineWidth = 5f
            dataSet.circleRadius = 5f

            dataSet.setDrawValues(false)
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)

            val data = LineData(dataSet)
            binding.chart.data = data
            binding.chart.invalidate() //refreshing chart

            binding.chart.legend.isEnabled = false
            binding.chart.description.isEnabled = false
            binding.chart.setNoDataText("No data available")
            binding.chart.axisLeft.axisMinimum = 10f
            binding.chart.axisRight.axisMinimum = 10f
            binding.chart.animateXY(1400, 1400)
            binding.chart.setTouchEnabled(true)
            binding.chart.setPinchZoom(true)
            binding.chart.axisRight.isEnabled = false
            binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            binding.chart.zoom(0f, 0f, 0f, 0f)

            val legend = binding.chart.legend
            legend.isEnabled = true
            legend.form = Legend.LegendForm.LINE
            legend.textSize = 12f
            legend.textColor = Color.BLACK
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryValueFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryValueFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}