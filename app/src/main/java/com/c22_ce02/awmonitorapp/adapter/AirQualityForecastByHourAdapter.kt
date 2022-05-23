package com.c22_ce02.awmonitorapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHour
import com.c22_ce02.awmonitorapp.databinding.ItemRecycleviewAirQualityForecastBinding
import com.c22_ce02.awmonitorapp.utils.Animation.startIncrementTextAnimation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AirQualityForecastByHourAdapter(
    private val listForecast: ArrayList<AirQualityForecastByHour>,
    private val canPlayAnim: Boolean
) : RecyclerView.Adapter<AirQualityForecastByHourAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemRecycleviewAirQualityForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currentHour = SimpleDateFormat("ha", Locale("id")).format(Date()).lowercase()

        fun bind(f: AirQualityForecastByHour) {
            with(binding) {
                if(canPlayAnim) {
                    startIncrementTextAnimation(f.aqi, tvForecastAQI)
                }
                tvHour.text = f.hour
                tvLabelAQI.text = itemView.context.getString(R.string.aqi)
                iconStatusAQI.setImageResource(f.iconAQISrc)

                if (f.hour.equals(currentHour, true)) {
                    tvHour.text = itemView.context.getString(R.string.now)
                    tvHour.setTextColor(Color.WHITE)
                    tvForecastAQI.setTextColor(Color.WHITE)
                    tvLabelAQI.setTextColor(Color.WHITE)
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

                itemView.setOnClickListener {
                    Toast.makeText(itemView.context, "you clicked item at : ${f.hour}", Toast.LENGTH_SHORT)
                        .show()
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