package com.e.letsplant.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.data.Plant
import com.e.letsplant.fragments.MainFragment
import com.e.letsplant.fragments.PlantDetailedFragment

class PlantAdapter constructor(private val context: Context?, private val plantList: List<Plant?>) :
    MainAdapter() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant: Plant? = plantList[position]
        val plantId: String? = plant?.plantId
        val title: String? = plant?.title
        val image: String? = plant?.image
        val moisture: Int? = plant?.moisture
        val temp: Int? = plant?.temperature
        val temperature: String = temp.toString() + " \u2103"
        val light: Int? = plant?.light
        val humidity: Int? = plant?.humidity
        (holder as PlantViewHolder).title.text = title
        plant?.image?.let { Log.d("GLIDE", it) }
        Glide.with((context)!!)
            .load(image)
            .into(holder.image)
        if (moisture != null) {
            if (moisture in 1..89) {
                holder.moisture.visibility = View.VISIBLE
                holder.moisture.setImageResource(R.drawable.ic_water_red)
                holder.moistureTextView.visibility = View.VISIBLE
                holder.moistureTextView.text = "moisture: bad"
            } else if (moisture in 90..94) {
                holder.moisture.visibility = View.VISIBLE
                holder.moisture.setImageResource(R.drawable.ic_water_green)
                holder.moistureTextView.visibility = View.VISIBLE
                holder.moistureTextView.text = "moisture: good"
            } else if (moisture >= 95) {
                holder.moisture.visibility = View.VISIBLE
                holder.moisture.setImageResource(R.drawable.ic_water)
                holder.moistureTextView.visibility = View.VISIBLE
                holder.moistureTextView.text = "moisture: very good"
            }
        }
        if (light == 0) {
            holder.light.visibility = View.VISIBLE
            holder.light.setImageResource(R.drawable.ic_moon)
        }
        if (light == 1) {
            holder.light.visibility = View.VISIBLE
            holder.light.setImageResource(R.drawable.ic_sun)
        }
        if (humidity != null) {
            if (humidity > 0) {
                val h = "humidity: $humidity%"
                holder.humidityTextView.visibility = View.VISIBLE
                holder.humidityTextView.text = h
            }
        }
        if (temp != null) {
            if (temp > -99) {
                holder.temperatureTextView.visibility = View.VISIBLE
                holder.temperatureTextView.text = temperature
            }
        }
        holder.image.setOnClickListener { v ->
            val appCompatActivity: AppCompatActivity = v.context as AppCompatActivity
            val fragment: Fragment? = PlantDetailedFragment.instance
            val bundle = Bundle()
            bundle.putString("plantId", plantId)
            bundle.putString("title", title)
            bundle.putString("image", image)
            fragment!!.arguments = bundle
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, (fragment)).commit()
        }
        holder.deletePlant.setOnClickListener { deletePlant(plantId, title) }
    }

    override fun getItemCount(): Int {
        return plantList.size
    }

    class PlantViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val image: ImageView
        val deletePlant: ImageView
        val moisture: ImageView
        val moistureTextView: TextView
        val temperatureTextView: TextView
        val light: ImageView
        val humidityTextView: TextView

        init {
            title = itemView.findViewById(R.id.title)
            image = itemView.findViewById(R.id.image)
            moisture = itemView.findViewById(R.id.moisture)
            moistureTextView = itemView.findViewById(R.id.moistureTextView)
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView)
            light = itemView.findViewById(R.id.lightImageView)
            humidityTextView = itemView.findViewById(R.id.humidityTextView)
            deletePlant = itemView.findViewById(R.id.deletePlant)
        }
    }

    private fun deletePlant(id: String?, title: String?) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
        alert.setTitle(title)
        alert.setMessage("Delete this plant?")
        alert.setPositiveButton(
            android.R.string.yes
        ) { _, _ ->
            databaseReference!!.child(MainFragment.DB_PLANTS).child((id)!!)
                .removeValue()
        }
        alert.setNegativeButton(
            android.R.string.no
        ) { dialog, _ -> dialog.cancel() }
        alert.show()
    }
}