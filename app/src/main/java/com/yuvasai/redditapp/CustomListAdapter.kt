package com.yuvasai.redditapp

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.yuvasai.redditapp.model.Post


class CustomListAdapter(private val mContext: Context, private val mResource: Int, objects: ArrayList<Post>) :
    ArrayAdapter<Post>(mContext, mResource, objects) {
    private var lastPosition = -1
    var listItemClickListener : ListItemClickListener? = null

    /**
     * Holds variables in a View
     */
    private class ViewHolder {
        internal var title: TextView? = null
        internal var author: TextView? = null
        internal var date_updated: TextView? = null
        internal var mProgressBar: ProgressBar? = null
        internal var thumbnailURL: ImageView? = null
        internal var cardView : CardView? = null
        internal var container : RelativeLayout? = null
    }

    init {

        //sets up the image loader library
        setupImageLoader()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView


        //get the persons information
        val title = getItem(position)!!.title
        val imgUrl = getItem(position)!!.thumbnailURL
        val author = getItem(position)!!.author
        val date_updated = getItem(position)!!.date_updated


        try {


            //create the view result for showing the animation
            val result: View

            //ViewHolder object
            val holder: ViewHolder

            if (convertView == null) {
                val inflater = LayoutInflater.from(mContext)
                convertView = inflater.inflate(mResource, parent, false)
                holder = ViewHolder()
                holder.title = convertView!!.findViewById(R.id.cardTitle)
                holder.thumbnailURL = convertView!!.findViewById(R.id.cardImage) as ImageView
                holder.author = convertView!!.findViewById(R.id.cardAuthor)
                holder.date_updated = convertView!!.findViewById(R.id.cardUpdated)
                holder.mProgressBar = convertView!!.findViewById(R.id.cardProgressDialog)
                holder.cardView = convertView.findViewById(R.id.cv_card)
                holder.container = convertView.findViewById(R.id.rl_container)

                result = convertView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                result = convertView
            }


            //            Animation animation = AnimationUtils.loadAnimation(mContext,
            //                    (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
            //            result.startAnimation(animation);

            lastPosition = position

            holder.title!!.text = title
            holder.author!!.text = author
            holder.date_updated!!.text = date_updated

            holder.cardView?.setOnClickListener{
                listItemClickListener?.onItemClicked(position)

            }

            holder.container?.setOnClickListener{
                listItemClickListener?.onItemClicked(position)
            }

            //create the imageloader object
            val imageLoader = ImageLoader.getInstance()

            val defaultImage = mContext.getResources()
                .getIdentifier("@drawable/image_failed", null, mContext.getPackageName())

            //create display options
            val options = DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build()

            //download and display image from url
            imageLoader.displayImage(
                imgUrl,
                holder.thumbnailURL,
                options,
                object : ImageLoadingListener {
                    override fun onLoadingStarted(imageUri: String, view: View) {
                        holder.mProgressBar!!.visibility = View.VISIBLE
                    }

                    override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
                        holder.mProgressBar!!.visibility = View.GONE
                    }

                    override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                        holder.mProgressBar!!.visibility = View.GONE
                    }

                    override fun onLoadingCancelled(imageUri: String, view: View) {
                        holder.mProgressBar!!.visibility = View.GONE
                    }

                })

            return convertView
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "getView: IllegalArgumentException: " + e.message)
            return convertView!!
        }

    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private fun setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        val defaultOptions = DisplayImageOptions.Builder()
            .cacheOnDisc(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(FadeInBitmapDisplayer(300)).build()

        val config = ImageLoaderConfiguration.Builder(
            mContext
        )
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(WeakMemoryCache())
            .discCacheSize(100 * 1024 * 1024).build()

        ImageLoader.getInstance().init(config)
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    companion object {

        private val TAG = "CustomListAdapter"
    }

}