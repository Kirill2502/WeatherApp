package com.example.weatherapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListItemBinding
import com.example.weatherapp.domain.models.DayItem
import com.example.weatherapp.utils.FixRus
import com.squareup.picasso.Picasso


class RecyclerWeatherAdapter(
    val listener: Listener?,
    private val fixRus: FixRus
): ListAdapter<DayItem, RecyclerWeatherAdapter.Holder>(Comparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return Holder(view,listener,fixRus)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }





    class Holder(view: View, val listener: Listener?, private val fixRus: FixRus): RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        var tempItem:DayItem? =null
        init {//функция инициализации
           itemView.setOnClickListener {//itemViev это весь элемент на который происходит нажатие
               tempItem?.let { item -> listener?.onClick(item) }
           }
        }
        fun bind(item: DayItem) = with(binding){
           tempItem = item
            tvDateItem.text = item.time
            tvConditionItem.text = fixRus.getWeatherDescription(item.condition)//
            when(item.currentTemp){
                ""->tvTempItem.text ="${item.maxTemp}/${item.minTemp}°C"
                else ->tvTempItem.text = "${item.currentTemp}°C"
            }
            Picasso.get().load("https:"+item.imageUrl).into(imItem)

        }
    }
    class Comparator: DiffUtil.ItemCallback<DayItem>(){
        override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem == newItem
        }

    }
    interface Listener{
        fun onClick(item: DayItem)
    }
}