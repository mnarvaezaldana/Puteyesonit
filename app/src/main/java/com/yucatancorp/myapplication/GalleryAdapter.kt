package com.yucatancorp.myapplication

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView

class GalleryAdapter(private val photoListSrc: ArrayList<Uri>, private val context: Context): RecyclerView.Adapter<GalleryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        return GalleryHolder(LayoutInflater.from(context).inflate(R.layout.item_photo_storage, parent, false))
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.initFrom(photoListSrc[position], context)
    }

    override fun getItemCount(): Int {
        return photoListSrc.size
    }

}

class GalleryHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private var photo: ShapeableImageView = itemView.findViewById(R.id._image)

    fun initFrom(photoSrc: Uri, context: Context){
        Glide.with(context)
            .applyDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
            .load(photoSrc)
            .centerInside()
            .override(100,100)
            .skipMemoryCache(true)
            .into(photo as ImageView)
    }

}