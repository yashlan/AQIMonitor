package com.c22_ce02.awmonitorapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.ForecastAirQualityToday
import com.c22_ce02.awmonitorapp.databinding.ItemRecycleviewAirQualityForecastBinding
import com.c22_ce02.awmonitorapp.utils.Animation.startIncrementTextAnimation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AirQualityForecastTodayAdapter(
    private val listForecast: ArrayList<ForecastAirQualityToday>
) : RecyclerView.Adapter<AirQualityForecastTodayAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemRecycleviewAirQualityForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currentHour = SimpleDateFormat("hh", Locale("id")).format(Date()).toInt()
        private val amPmMarker = SimpleDateFormat("a", Locale("id")).format(Date()).lowercase()

        fun bind(f: ForecastAirQualityToday) {
            with(binding) {
                tvHour.text = f.hour
                startIncrementTextAnimation(f.aqi, tvForecastAQI)
                tvLabelPM25.text = itemView.context.getString(R.string.pm25)
                iconStatusAQI.setImageResource(f.iconAQISrc)
                if (f.hour == "${currentHour}${amPmMarker}") {
                    tvHour.setTextColor(Color.WHITE)
                    tvForecastAQI.setTextColor(Color.WHITE)
                    tvLabelPM25.setTextColor(Color.WHITE)
                    cardItemAirForecastToday.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            when (f.aqi) {
                                in 0..50 -> R.color.warna_baik
                                in 51..100 -> R.color.warna_sedang
                                in 101..150 -> R.color.warna_tidak_sehat
                                in 151..300 -> R.color.warna_sangat_tidak_sehat
                                else -> R.color.warna_berbahaya
                            }
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder = MyViewHolder(
        ItemRecycleviewAirQualityForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) = holder.bind(listForecast[position])

    override fun getItemCount(): Int = listForecast.size
}