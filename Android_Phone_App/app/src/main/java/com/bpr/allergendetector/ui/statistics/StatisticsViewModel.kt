package com.bpr.allergendetector.ui.statistics

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val scanCounterRepo: ScanCounterRepo =
        ScanCounterRepo(ScanCounterDB.getInstance(application).scanCounterDAO())

    val allScanCounters = scanCounterRepo.getAllScanCounters()

    fun insertAll(scanCounters: List<ScanCounter>) {
        viewModelScope.launch {
            scanCounterRepo.insertAll(scanCounters)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            scanCounterRepo.deleteAll()
        }
    }

    fun setupBarChart(
        barChart: BarChart,
        isYearSelected: Boolean,
        scanCounters: List<ScanCounter>
    ) {

        val entries = mutableListOf<BarEntry>()

        val xAxis: XAxis = barChart.xAxis

        if (isYearSelected) {
            // iterate through each month of the year and get the scan count for that month
            val monthsOfYear = getMonthsNumbers()

            monthsOfYear.forEachIndexed { index, _ ->
                val scanCountersForMonth =
                    scanCounters.filter { it.date.substring(5, 7) == monthsOfYear[index] }

                val countForMonth = scanCountersForMonth.sumOf { it.count }

                entries.add(BarEntry(index.toFloat(), countForMonth.toFloat()))
            }

            xAxis.valueFormatter = MonthAxisValueFormatter() // Set month labels

        } else {
            val datesOfWeek = getDatesOfCurrentWeek()

            datesOfWeek.forEachIndexed { index, date ->
                val scanCounter = scanCounters.find { it.date == date }
                val count = scanCounter?.count ?: 0
                entries.add(BarEntry(index.toFloat(), count.toFloat()))
            }

            xAxis.valueFormatter = DayAxisValueFormatter() // Set day labels
        }

        val dataSet = BarDataSet(entries, "")

        // custom ValueFormatter for the y-values to remove decimal places
        dataSet.valueFormatter = BarValueFormatter()
        val barData = BarData(dataSet)
        barChart.data = barData

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val legend = barChart.legend
        legend.isEnabled = false

        barChart.description.isEnabled = false

        // start at 0 and force integer values
        barChart.axisLeft.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f

        barChart.axisRight.granularity = 1f
        barChart.axisRight.axisMinimum = 0f

        if (isYearSelected) {
            highlightCurrentMonth(barChart)
        } else {
            highlightToday(barChart)
        }

        adjustToDarkMode(barChart)

        barChart.invalidate()
    }

    fun setupLineChart(
        lineChart: LineChart,
        isYearSelected: Boolean,
        scanCounters: List<ScanCounter>
    ) {

        val entriesHarmful = mutableListOf<Entry>()
        val entriesHarmless = mutableListOf<Entry>()
        val datesOfWeek = getDatesOfCurrentWeek()

        val xAxis: XAxis = lineChart.xAxis

        if (isYearSelected) {
            val monthsOfYear = getMonthsNumbers()

            monthsOfYear.forEachIndexed { index, _ ->
                val scanCountersForMonth =
                    scanCounters.filter { it.date.substring(5, 7) == monthsOfYear[index] }

                val countForMonth = scanCountersForMonth.sumOf { it.count }
                val isHarmfulCountForMonth = scanCountersForMonth.sumOf { it.isHarmful }

                entriesHarmful.add(Entry(index.toFloat(), isHarmfulCountForMonth.toFloat()))
                entriesHarmless.add(
                    Entry(
                        index.toFloat(),
                        (countForMonth - isHarmfulCountForMonth).toFloat()
                    )
                )
            }

            xAxis.valueFormatter = MonthAxisValueFormatter() // Set month labels

        } else {

            datesOfWeek.forEachIndexed { index, date ->
                val scanCounter = scanCounters.find { it.date == date }
                val totalCount = scanCounter?.count ?: 0
                val isHarmfulCount = scanCounter?.isHarmful ?: 0
                val isHarmlessCount = totalCount - isHarmfulCount

                entriesHarmful.add(Entry(index.toFloat(), isHarmfulCount.toFloat()))
                entriesHarmless.add(Entry(index.toFloat(), isHarmlessCount.toFloat()))
            }

            xAxis.valueFormatter = DayAxisValueFormatter() // Set day labels
        }

        val harmlessDataSet = LineDataSet(entriesHarmless, "Harmless Products")
        val harmfulDataSet = LineDataSet(entriesHarmful, "Harmful Products")

        // custom ValueFormatter for the y-values to remove decimal places
        harmlessDataSet.valueFormatter = LineValueFormatter()
        harmfulDataSet.valueFormatter = LineValueFormatter()

        // colors for the lines
        harmlessDataSet.color = Color.GREEN
        harmfulDataSet.color = Color.RED

        val lineData = LineData(harmlessDataSet, harmfulDataSet)
        lineChart.data = lineData

        // set the xAxis position to bottom and remove the grid lines
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        // customize the legend
        val legend = lineChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.form = Legend.LegendForm.CIRCLE

        lineChart.description.isEnabled = false

        lineChart.setExtraOffsets(
            0f,
            0f,
            0f,
            20f
        ) // create spacing between the chart and the legend

        // start at 0 and force integer values
        lineChart.axisLeft.granularity = 1f
        lineChart.axisLeft.axisMinimum = 0f

        lineChart.axisRight.granularity = 1f
        lineChart.axisRight.axisMinimum = 0f

        adjustToDarkMode(lineChart)

        lineChart.invalidate()
    }

    inner class DayAxisValueFormatter : ValueFormatter() {

        private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index >= 0 && index < daysOfWeek.size) {
                daysOfWeek[index]
            } else {
                ""
            }
        }
    }

    inner class MonthAxisValueFormatter : ValueFormatter() {

        private val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec"
        )

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index >= 0 && index < months.size) {
                months[index]
            } else {
                ""
            }
        }
    }

    private fun getMonthsNumbers(): Array<String> {
        return arrayOf(
            "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12"
        )
    }

    inner class BarValueFormatter : ValueFormatter() {
        override fun getBarLabel(barEntry: BarEntry?): String {
            // format the y-value without decimal places
            return barEntry?.y?.toInt().toString()
        }
    }

    inner class LineValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            // format the y-value to be without decimal places
            return value.toInt().toString()
        }
    }

    private fun highlightToday(barChart: BarChart) {
        val todayIndex =
            Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 // adjust to zero-based index

        if (todayIndex >= 0) {
            val highlight = Highlight(todayIndex.toFloat(), 0f, 0)
            barChart.highlightValue(highlight, false)
        }
    }

    private fun highlightCurrentMonth(barChart: BarChart) {
        val currentMonthIndex =
            Calendar.getInstance().get(Calendar.MONTH)

        val highlight = Highlight(currentMonthIndex.toFloat(), 0f, 0)
        barChart.highlightValue(highlight, false)
    }


    private fun getDatesOfCurrentWeek(): List<String> {
        val calendar = Calendar.getInstance()

        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.time = Date()

        // move to the beginning of the week
        while (calendar.get(Calendar.DAY_OF_WEEK) != calendar.firstDayOfWeek) {
            calendar.add(Calendar.DAY_OF_WEEK, -1)
        }

        // get all the dates of the week
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datesOfWeek = mutableListOf<String>()

        repeat(7) {
            datesOfWeek.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        return datesOfWeek
    }

    private fun adjustToDarkMode(chart: Chart<*>) {
        // check if dark mode is enabled
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                "my_preferences",
                Context.MODE_PRIVATE
            )
        val nightMode = sharedPreferences.getBoolean("DARK_MODE", false)

        if (nightMode) {
            chart.xAxis.textColor = Color.WHITE
            chart.data.setValueTextColor(Color.WHITE)
            chart.legend.textColor = Color.WHITE

            if (chart is BarChart) {
                chart.axisLeft.textColor = Color.WHITE
                chart.axisRight.textColor = Color.WHITE
            } else if (chart is LineChart) {
                chart.axisLeft.textColor = Color.WHITE
                chart.axisRight.textColor = Color.WHITE
            }
        }
    }
}