package com.example.weatherapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListItemBinding
import com.example.weatherapp.presentation.models.WeatherListItemUi
import com.squareup.picasso.Picasso


class RecyclerWeatherAdapter(
    val listener: Listener?,
) : ListAdapter<WeatherListItemUi, RecyclerWeatherAdapter.Holder>(Comparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }


    class Holder(view: View, val listener: Listener?) :
        RecyclerView.ViewHolder(view) {
        val binding = ListItemBinding.bind(view)
        var tempItem: WeatherListItemUi? = null

        init {//функция инициализации
            itemView.setOnClickListener {//itemViev это весь элемент на который происходит нажатие
                tempItem?.let { item -> listener?.onClick(item) }
            }
        }

        // Адаптер больше не форматирует данные: всё уже готово в WeatherListItemUi
        fun bind(item: WeatherListItemUi) = with(binding) {
            tempItem = item
            tvDateItem.text = item.title
            tvConditionItem.text = item.conditionText
            tvTempItem.text = item.tempText
            if (item.iconUrl.isNotEmpty()) Picasso.get().load(item.iconUrl).into(imItem)

        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherListItemUi>() {
        override fun areItemsTheSame(
            oldItem: WeatherListItemUi,
            newItem: WeatherListItemUi
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WeatherListItemUi,
            newItem: WeatherListItemUi
        ): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun onClick(item: WeatherListItemUi)
    }
}