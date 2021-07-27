package com.app_devs.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.activities.AddHappyPlacesActivity
import com.app_devs.happyplaces.activities.MainActivity
import com.app_devs.happyplaces.databases.DatabaseHandler
import com.app_devs.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.item_happy_place.view.*

class HappyPlaceAdapter(private val context: Context, private val list:ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<HappyPlaceAdapter.MyViewHolder>() {

    private var onClickListener:OnClickListener?=null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_happy_place,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val item=list[position]
        holder.itemView.tvTitle.text = item.title
        holder.itemView.tvDescription.text = item.description
        holder.itemView.iv_place_image.setImageURI(Uri.parse(item.image))
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position,item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position:Int,requestCode:Int)
    {
        val intent=Intent(context,AddHappyPlacesActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,list[position])
        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)
    }
    fun deleteItem( position:Int)
    {
       val dbHandler=DatabaseHandler(context)
        val isDelete=dbHandler.deleteHappyPlace(list[position])
        if(isDelete > 0)
        {
            list.removeAt(position)
            notifyItemRemoved(position)
        }

    }


    fun setOnClickListener(onClickListener:OnClickListener)
    {
        this.onClickListener=onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int,model: HappyPlaceModel)

    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
    }
}