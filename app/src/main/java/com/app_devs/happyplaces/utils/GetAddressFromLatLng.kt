package com.app_devs.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.util.*

class GetAddressFromLatLng(context: Context, private val latitude:Double ,private val longitude:Double) :AsyncTask<Void,String,String>()
{
    private val geocoder:Geocoder=Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener:AddressListener

    override fun doInBackground(vararg params: Void?): String {
        try {
            /**
             * Returns an array of Addresses that are known to describe the
             * area immediately surrounding the given latitude and longitude.
             */
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addressList != null && addressList.isNotEmpty()) {
                val address: Address = addressList[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append(",")
                }
                sb.deleteCharAt(sb.length - 1) // Here we remove the last comma that we have added above from the address.
                return sb.toString()
            }
        } catch (e: IOException) {
            Log.e("HappyPlaces", "Unable connect to Geocoder")
        }

        return ""

    }

    override fun onPostExecute(resultString: String?) {
        if (resultString == null) {
            mAddressListener.onError()
        } else {
            mAddressListener.onAddressFound(resultString)
        }
        super.onPostExecute(resultString)
    }
    /**
     * A public function to set the AddressListener.
     */
    fun setAddressListener(addressListener: AddressListener) {
        mAddressListener = addressListener
    }

    /**
     * A public function to execute the AsyncTask from the class is it called.
     */
    fun getAddress() {
        execute()
    }
    interface AddressListener{
        fun onAddressFound(address:String?)
        fun onError()
    }

}