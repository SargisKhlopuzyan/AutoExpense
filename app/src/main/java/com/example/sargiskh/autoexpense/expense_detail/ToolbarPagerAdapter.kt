package com.example.sargiskh.autoexpense.expense_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.sargiskh.autoexpense.R

class ToolbarPagerAdapter(val context: Context) {

    val tabTitles = arrayOf("Image", "Drawing", "Voice")
    val imageResId = intArrayOf(R.drawable.toolbar_icon_image, R.drawable.toolbar_icon_drawing, R.drawable.toolbar_icon_voice)

    fun getTabView(position: Int): View {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        val v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        val textView = v.findViewById(R.id.textView) as TextView
        textView.text = tabTitles[position]
        val imageView = v.findViewById(R.id.imageView) as ImageView
        imageView.setImageResource(imageResId[position])
        return v
    }

}