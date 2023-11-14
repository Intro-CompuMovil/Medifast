package com.icm.medifast


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout

class ImageAdapter(private val context: Context, private val bitmapList: MutableList<Bitmap>) : BaseAdapter() {

    lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return bitmapList.size
    }

    override fun getItem(position: Int): Any {
        return bitmapList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    fun addBitmap(bitmap: Bitmap) {
        bitmapList.add(bitmap)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.grid_item_image, null)
        }

        val imageView = convertView!!.findViewById<ImageView>(R.id.grid_image)
        imageView.setImageBitmap(bitmapList[position])
        imageView.layoutParams = LinearLayout.LayoutParams(900, 900)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.elevation = 8f
        }

        val fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)
        fadeIn.duration = 1000
        fadeIn.start()

        return convertView
    }
}
