package com.example.weatherapp.adapters

import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.DayItem
import com.example.weatherapp.FixRus
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso


class RecyclerWeatherAdapter(val listener: Listener?): ListAdapter<DayItem, RecyclerWeatherAdapter.Holder>(Comparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return Holder(view,listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }





    class Holder(view: View,val listener: Listener?): RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        var tempItem:DayItem? =null
        init {//функция инициализации
           itemView.setOnClickListener {//itemViev это весь элемент на который происходит нажатие
               tempItem?.let { item -> listener?.onClick(item) }
           }
        }
        fun bind(item: DayItem) = with(binding){
            val mainFixRus = FixRus()
           tempItem = item
            tvDateItem.text = item.time
            tvConditionItem.text = mainFixRus.getWeatherDescription(item.condition)//
            when(item.currentTemp){
                ""->tvTempItem.text ="${item.maxTemp}°C/${item.minTemp}°C"
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