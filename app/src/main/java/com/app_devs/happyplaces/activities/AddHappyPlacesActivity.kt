package com.app_devs.happyplaces.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.databases.DatabaseHandler
import com.app_devs.happyplaces.models.HappyPlaceModel
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.jar.Manifest
import kotlin.math.log
//xyz
class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener {
    private var cal= Calendar.getInstance()
    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener

    private var saveImageToInternalStorage:Uri?=null
    private var mLongitude:Double=0.0
    private var mLatitude:Double=0.0

    private var mHappyPlaceDetails:HappyPlaceModel?=null

    private lateinit var mFusedLocationClient:FusedLocationProviderClient


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_places)

        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)


//        if(!Places.isInitialized())
//        {
//            Places.initialize(this,resources.getString(R.string.google_maps_api_key))
//        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            mHappyPlaceDetails=intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)

        }


        //i think after ok is pressed
        dateSetListener=DatePickerDialog.OnDateSetListener {
            _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        updateDate()
        if(mHappyPlaceDetails!=null)
        {
            supportActionBar?.title="Edit Happy Place"

            et_title.setText(mHappyPlaceDetails!!.title)
            et_description.setText(mHappyPlaceDetails!!.description)
            et_date.setText(mHappyPlaceDetails!!.date)
            et_location.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)

            iv_place_image.setImageURI(saveImageToInternalStorage)

            btn_save.text = "UPDATE"

        }
        et_date.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)

        btn_save.setOnClickListener(this)
        //et_location.setOnClickListener(this)
        tv_select_current_location.setOnClickListener(this)
    }

    private fun isLocationEnabled():Boolean
    {
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData()
    {
        Log.i("INSIDE FUN","YES")
        var mLocationRequest=LocationRequest()
        mLocationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval=1000
        mLocationRequest.numUpdates=1
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,mLocationCallback, Looper.myLooper()
        )

    }
    private val mLocationCallback=object :LocationCallback()
    {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.i("InsideCallback","YES")
            val mLastLocation= locationResult.lastLocation
            mLatitude=mLastLocation.latitude
            mLongitude=mLastLocation.longitude
            Log.i("Latitude","$mLatitude")
            Log.i("Longitude","$mLongitude")

        }
    }


    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.et_date ->
            {
                DatePickerDialog(this,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()

            }
            R.id.tv_add_image ->
            {
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val items= arrayOf("Select from gallery","Capture from camera")
                pictureDialog.setItems(items){
                    _, which->
                    when(which)
                        {
                            0-> chooseFromGallery()
                            1->{
                                takePhoto()
                            }
                        }
                }
                pictureDialog.show()
            }
            R.id.btn_save ->
            {
                when
                {
                    et_title.text.isNullOrEmpty() ->
                        Toast.makeText(this,"Title can't be empty",Toast.LENGTH_SHORT).show()
                    et_description.text.isNullOrEmpty() ->
                        Toast.makeText(this,"Description can't be empty",Toast.LENGTH_SHORT).show()
                    et_location.text.isNullOrEmpty() ->
                        Toast.makeText(this,"Location can't be empty",Toast.LENGTH_SHORT).show()
                    saveImageToInternalStorage==null ->
                        Toast.makeText(this,"Image can't be empty",Toast.LENGTH_SHORT).show()
                    else ->
                    {
                        val happyPlaceModel=HappyPlaceModel(if(mHappyPlaceDetails==null) 0 else mHappyPlaceDetails!!.id,
                                et_title.text.toString(),
                                et_description.text.toString(),
                                saveImageToInternalStorage.toString(),
                                et_location.text.toString(),
                                mLatitude,mLongitude,et_date.text.toString()
                        )
                        val dbHandler=DatabaseHandler(this)
                        if(mHappyPlaceDetails==null) {
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                            if(addHappyPlace>0)
                            {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                        else{
                            val updateHappyPlace=dbHandler.updateHappyPlace((happyPlaceModel))
                            if(updateHappyPlace>0)
                            {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }

                    }

                }
            }
            R.id.tv_select_current_location->
            {
                if(!isLocationEnabled()) {
                    Toast.makeText(this, "You've not enabled your location", Toast.LENGTH_SHORT)
                        .show()

                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                else
                {
                    Dexter.withActivity(this).withPermissions(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ).withListener(object :MultiplePermissionsListener{
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if(report!!.areAllPermissionsGranted())
                            {
                                Log.i("CHECKINTO","CHECKED")
                                requestNewLocationData()
                            }

                        }
                        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                            showPermissionRationale()
                        }
                    }).onSameThread().check()
                }
            }
            /*R.id.et_location->
            {
                try {
                    // These are the list of fields which we required is passed
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                    // Start the autocomplete intent with a unique request code.
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(this@AddHappyPlacesActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

                }catch (e:java.lang.Exception)
                {
                    e.printStackTrace()
                }
            }*/

        }
    }


    private fun chooseFromGallery() {
        isCamera=false
        Dexter.withContext(this).withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted())
                {
                    val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    resultLauncher.launch(intent)
                }

            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                showPermissionRationale()
            }
        }).onSameThread().check();


    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
                .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
                .setPositiveButton("Go to settings")
                {
                    _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel") { dialog,
                                               _ ->
                    dialog.dismiss()
                }.show()


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDate()
    {
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat,Locale.getDefault())

        et_date.setText(sdf.format(cal.time).toString())
    }

    private var resultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->
        if (result.resultCode==Activity.RESULT_OK && isCamera)
        {
           val captured= result.data?.extras?.get("data") as Bitmap
            saveImageToInternalStorage=saveImageToInternalStorage(captured)
            Log.e("SAVED","PATH :: $saveImageToInternalStorage")
            iv_place_image.setImageBitmap(captured)

        }
        else if (result.resultCode==Activity.RESULT_OK)
        {
            //it gives the uri from the intent which is result.data
            val contentUri=result.data!!.data
            try {
                val selectedImage=MediaStore.Images.Media.getBitmap(this.contentResolver,contentUri)
                 saveImageToInternalStorage=saveImageToInternalStorage(selectedImage)
                Log.e("SAVED","PATH :: $saveImageToInternalStorage")
                Log.d("CHECK","CHECKED")
                iv_place_image.setImageBitmap(selectedImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else if(result.resultCode== PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            val place=Autocomplete.getPlaceFromIntent(result.data!!)
            et_location.setText(place.address)
            mLatitude=place.latLng!!.latitude
            mLongitude=place.latLng!!.longitude

        }

    }
    companion object{
        private const val GALLERY_REQUEST_CODE=1
        private var isCamera=false
        private const val IMAGE_DIRECTORY="HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE=2

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK)
        {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                et_location.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
    }
    private fun takePhoto()
    {
        Dexter.withContext(this).withPermissions(
               android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted())
                {
                    isCamera =true
                    val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    resultLauncher.launch(intent)
                }

            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                showPermissionRationale()
            }
        }).onSameThread().check();
    }

    private fun saveImageToInternalStorage(bitmap:Bitmap) :Uri
    {
        val wrapper= ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        //using the directory to create a file
        file=File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream:OutputStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException)
        {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)


    }



}