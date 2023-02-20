package com.hsb.syedhaseeb_funprime_task.ui

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.hsb.syedhaseeb_funprime_task.R
import com.hsb.syedhaseeb_funprime_task.data.ViewTypeModel
import com.hsb.syedhaseeb_funprime_task.utils.ViewHolder_Simple
import com.hsb.syedhaseeb_funprime_task.utils.ads.populateUnifiedNativeAdView
import com.hsb.syedhaseeb_funprime_task.utils.setImage

class GridViewAdapter(
    val activity: Activity,
    context: Context,
    private var mList: ArrayList<ViewTypeModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ctx: Context = context
    var imageClick: ((Int) -> Unit)? = null
    private var nativeAd: NativeAd? = null

    private inner class ViewHolderSimple(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.simpleImage)
        fun bind(position: Int) {
            val recyclerViewModel = mList[position]
            recyclerViewModel.drawable?.let { ctx.setImage(it, imageView) }
            imageView.setOnClickListener {
                imageClick?.invoke(position)
            }
        }
    }

    private inner class ViewHolderAd(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var native: ConstraintLayout? = null
        var shimmerFrameLayout: ShimmerFrameLayout? = null
        var adFrame: FrameLayout? = null

        init {
            native = itemView.findViewById(R.id.nativeAdCard)
            shimmerFrameLayout = native?.findViewById(R.id.shimmerContainerSetting)
            adFrame = native?.findViewById(R.id.adFrame)
        }

        fun bind() {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ViewHolder_Simple) {
            return ViewHolderSimple(
                LayoutInflater.from(ctx).inflate(R.layout.simplelayout, parent, false)
            )
        }
        return ViewHolderAd(
            LayoutInflater.from(ctx).inflate(R.layout.adlayout, parent, false)

        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mList[position].viewType == ViewHolder_Simple) {
            (holder as ViewHolderSimple).bind(position)

        } else {
            (holder as ViewHolderAd).bind()
            val itemViewHolder = holder as ViewHolderAd
            val adView = LayoutInflater.from(ctx)
                .inflate(R.layout.native_ad_layout_mini, null) as NativeAdView
            nativeAd?.let {
                adView.let { it1 -> populateUnifiedNativeAdView(it, it1) }
                itemViewHolder.shimmerFrameLayout?.stopShimmer()
                itemViewHolder.shimmerFrameLayout?.visibility = GONE
                itemViewHolder.adFrame?.visibility = VISIBLE
                itemViewHolder.native?.visibility = VISIBLE
                itemViewHolder.adFrame?.removeAllViews()
                itemViewHolder.adFrame?.addView(adView)
            }
        }
    }

    fun setNative(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return mList[position].viewType
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun update(newList: ArrayList<ViewTypeModel>) {
        this.mList = newList
        notifyDataSetChanged()
    }
}