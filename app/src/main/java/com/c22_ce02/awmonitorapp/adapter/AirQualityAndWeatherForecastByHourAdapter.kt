package com.c22_ce02.awmonitorapp.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityAndWeatherForecastByHour
import com.c22_ce02.awmonitorapp.databinding.ItemRecycleviewAirQualityForecastBinding
import com.c22_ce02.awmonitorapp.ui.activity.DetailsForecastActivity
import com.c22_ce02.awmonitorapp.ui.fragment.HomeFragment
import com.c22_ce02.awmonitorapp.utils.startIncrementTextAnimation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AirQualityAndWeatherForecastByHourAdapter(
    private val listForecast: ArrayList<AirQualityAndWeatherForecastByHour>,
    private val canPlayAnim: Boolean
) : RecyclerView.Adapter<AirQualityAndWeatherForecastByHourAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemRecycleviewAirQualityForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currentHour = SimpleDateFormat("ha", Locale("id")).format(Date()).lowercase()

        fun bind(f: AirQualityAndWeatherForecastByHour) {
            with(binding) {
                if (canPlayAnim) {
                    startIncrementTextAnimation(f.forecastAirQuality.aqi, tvForecastAQI)
                }
                tvHour.text = f.forecastAirQuality.hour
                tvLabelAQI.text = itemView.context.getString(R.string.aqi)
                iconStatusAQI.setImageResource(f.forecastAirQuality.iconAQISrc)

                if (f.forecastAirQuality.hour.equals(currentHour, true)) {
                    tvHour.text = itemView.context.getString(R.string.now)
                    tvHour.setTextColor(Color.WHITE)
                    tvForecastAQI.setTextColor(Color.WHITE)
                    tvLabelAQI.setTextColor(Color.WHITE)
                    cardItemAirForecastToday.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            when (f.forecastAirQuality.aqi) {
                                in 0..50 -> R.color.warna_baik
                                in 51..100 -> R.color.warna_sedang
                                in 101..150 -> R.color.warna_tidak_sehat
                                in 151..300 -> R.color.warna_sangat_tidak_sehat
                                else -> R.color.warna_berbahaya
                            }
                        )
                    )
                }

                itemView.apply {
                    setOnClickListener {
                        startAnimation(AlphaAnimation(1f, 0.5f))
                        val i = Intent(context, DetailsForecastActivity::class.java)
                        i.putExtra(HomeFragment.FORECAST_EXTRA, f)
                        context.startActivity(i)
                    }
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