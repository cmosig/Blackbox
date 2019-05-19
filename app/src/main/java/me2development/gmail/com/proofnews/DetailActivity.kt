package me2development.gmail.com.proofnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.AnyChart.vertical
import com.anychart.enums.HoverMode
import com.anychart.enums.TooltipPositionMode
import com.anychart.enums.TooltipDisplayMode
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.toast
import kotlin.math.roundToInt


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val j=intent?.extras?.getSerializable("S") as? ComplicatedJson
        if(j==null){
            toast("Error, Json null in DetailActivity")
            finish()
            return
        }

        val vertical= AnyChart.vertical()
        vertical.animation(true).title("The estimated trustability is ${j.allgemeinerScore().roundToInt()}%")

        val data=mutableListOf(
            CustomDataEntry("Style",j?.style?.score?.roundToInt()?:0,0),
            CustomDataEntry("Credibility",j?.credibility?.score?.roundToInt()?:0,0),
            CustomDataEntry("Propagation",j?.propagation?.score?.roundToInt()?:0,0),
            CustomDataEntry("Knowledge",j?.knowledge?.score?.roundToInt()?:0,0)
        )

        val set=Set.instantiate()
        set.data(data as List<DataEntry>?)
        val barData=set.mapAs("{ x: 'x', value: 'value' , fill: 'color' }")
        val jumpLineData=set.mapAs("{ x: 'x', value: 'jumpLine' }")

        val bar= vertical.bar(barData)
        bar.labels().format("{%Value}%")

        val jumpLine=vertical.jumpLine(jumpLineData)



        //ABBBBBBBBBBBBBBBBBBBBBBBBBB

        jumpLine.stroke("0 #60727B")
        jumpLine.labels().enabled(true)
        jumpLine.enabled(false)

        vertical.yScale().minimum(0.0)
        vertical.yScale().maximum(100.0)

        vertical.labels(true)

        vertical.tooltip()
.displayMode(TooltipDisplayMode.UNION)
.positionMode(TooltipPositionMode.POINT)
.unionFormat(
    "function() {\n" +
    "      return 'Plain: ' + this.points[1].value + '%' +\n" +
    "        '\\n' + 'Fact: ' + this.points[0].value + '%';\n" +
    "    }"
)

        vertical.interactivity().hoverMode(HoverMode.BY_X)

        vertical.xAxis(true)
        vertical.yAxis(true)
        vertical.yAxis(0).labels().format("{%Value}%")

        anyChartView.setChart(vertical)


        btn.setOnClickListener {
            startActivity(Intent(this@DetailActivity,DetailedSources::class.java).also{it.putExtra("S",j)})

        }

    }
}
private class CustomDataEntry(x: String, value: Number, jumpLine: Number ) : ValueDataEntry(x, value) {

    init {
        setValue("jumpLine", jumpLine)
        val dbl=value.toInt()
        val color1=when(dbl){
            in 0..32->"#FF1744"
            in 32..66->"#64B5F6"
            in 66..101->"#76FF03"
            else->"#64B5F6"
        }

        setValue("color",color1)
    }
}