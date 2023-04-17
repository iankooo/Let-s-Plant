package com.e.letsplant.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.letsplant.R
import com.e.letsplant.adapters.PlantAdapter
import com.e.letsplant.data.Plant
import com.e.letsplant.data.PlantAlertItem
import com.e.letsplant.listeners.RecyclerItemClickListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class PlantsFragment : MainFragment() {
    private var listener: OnItemSelectedListener? = null
    private var mImageUri: Uri? = null
    private var plantList: MutableList<Plant?>? = null
    private var recyclerView: RecyclerView? = null
    private var plantAdapter: PlantAdapter? = null
    private var sensorCode: EditText? = null
    private var plantNameEditText: EditText? = null
    private var cardView: CardView? = null
    private var imageView: ImageView? = null
    private var mDBListener: ValueEventListener? = null
    var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title =
            "My plants"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_plants, container, false)
        cardView = rootView?.findViewById(R.id.cardView)
        imageView = rootView?.findViewById(R.id.imageView)
        val uploadImageView: ImageView? = rootView?.findViewById(R.id.uploadImageView)
        plantNameEditText = rootView?.findViewById(R.id.plantNameEditText)
        sensorCode = rootView?.findViewById(R.id.sensorCode)
        recyclerView = rootView?.findViewById(R.id.plantRecyclerView)
        recyclerView?.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading plants")
        progressDialog.show()
        plantList = ArrayList()
        plantAdapter = PlantAdapter(context, plantList!!)
        recyclerView?.adapter = plantAdapter
        userUid = firebaseAuth!!.uid
        readPlants(progressDialog)
        uploadImageView?.setOnClickListener { uploadImage() }
        recyclerView?.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        //listener.onPlantItemSelected(plantList.get(position));
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        val popup = PopupMenu(context, view)
                        popup.inflate(R.menu.plant_long_press)
                        popup.setOnMenuItemClickListener(object :
                            PopupMenu.OnMenuItemClickListener {
                            override fun onMenuItemClick(item: MenuItem): Boolean {
                                when (item.itemId) {
                                    R.id.plant_delete -> {
                                        val alert: AlertDialog.Builder =
                                            AlertDialog.Builder(context)
                                        alert.setTitle(plantList?.get(position)?.title)
                                        alert.setMessage("Delete this plant?")
                                        alert.setPositiveButton(
                                            android.R.string.yes
                                        ) { _, _ ->
                                            val plant: Plant? = plantList?.get(position)
                                            databaseReference!!.child(DB_PLANTS)
                                                .child(plant?.plantId!!).removeValue()
                                        }
                                        alert.setNegativeButton(
                                            android.R.string.no
                                        ) { dialog, _ -> dialog.cancel() }
                                        alert.show()
                                        return true
                                    }
                                    else -> return false
                                }
                            }
                        })
                        popup.show()
                    }
                })
        )
        return rootView
    }

    private fun readPlants(progressDialog: ProgressDialog) {
        mDBListener = databaseReference!!.child(DB_PLANTS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    plantList!!.clear()
                    for (postSnapshot: DataSnapshot in snapshot.children) {
                        val plant: Plant? = postSnapshot.getValue(Plant::class.java)
                        if ((plant?.owner == userUid)) plantList!!.add(plant)
                    }
                    plantAdapter!!.notifyDataSetChanged()
                    progressDialog.dismiss()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(ContentValues.TAG, databaseError.message)
                    progressDialog.dismiss()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_plants_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_add_plant) {
            val items: Array<PlantAlertItem> = arrayOf(
                PlantAlertItem("Take a photo", R.drawable.ic_photo_camera),
                PlantAlertItem("Load from gallery", R.drawable.ic_picture)
            )
            val adapter: ListAdapter = object : ArrayAdapter<PlantAlertItem?>(
                requireContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items
            ) {
                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    //Use super class to create the View
                    val v: View = super.getView(position, convertView, parent)
                    val tv: TextView = v.findViewById(android.R.id.text1)

                    //Put the image on the TextView
                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0)

                    //Add margin between image and text (support various screen densities)
                    val dp5: Int = (5 * resources.displayMetrics.density + 0.5f).toInt()
                    tv.compoundDrawablePadding = dp5
                    return v
                }
            }
            AlertDialog.Builder(context)
                .setTitle("Add new plant")
                .setAdapter(adapter) { _, item ->
                    if (item == 0) {
                        takeImage()
                    }
                    if (item == 1) selectImage()
                }.setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun takeImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == Activity.RESULT_OK) && (data != null) && (data.data != null)) {
            mImageUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    mImageUri
                )
                plantNameEditText!!.setText("")
                plantNameEditText!!.isFocusable = true
                sensorCode!!.visibility = View.VISIBLE
                sensorCode!!.setText("")
                sensorCode!!.isFocusable = true
                imageView!!.setImageBitmap(bitmap)
                cardView!!.visibility = View.VISIBLE
                cardView!!.onFocusChangeListener =
                    OnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) cardView!!.visibility = View.GONE
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            mImageUri = data!!.data
            val extras: Bundle? = data.extras
            val imageBitmap: Bitmap? = extras!!.get("data") as Bitmap?
            imageView!!.setImageBitmap(imageBitmap)
            plantNameEditText!!.setText("")
            plantNameEditText!!.isFocusable = true
            sensorCode!!.visibility = View.VISIBLE
            sensorCode!!.setText("")
            sensorCode!!.isFocusable = true
            imageView!!.setImageBitmap(imageBitmap)
            cardView!!.visibility = View.VISIBLE
            cardView!!.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) cardView!!.visibility = View.GONE
            }
        }
    }

    private fun uploadImage() {
        val title: String = plantNameEditText!!.text.toString().trim { it <= ' ' }
        val code: String = sensorCode!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(title)) {
            plantNameEditText!!.error = "Plant name is required!"
            return
        }
        if (mImageUri != null) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref: StorageReference =
                storageReference!!.child(ALL_PLANT_IMAGES)
                    .child(userUid + System.currentTimeMillis() + "." + getFileExtension(mImageUri))
            ref.putFile(mImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    if (taskSnapshot.metadata != null) {
                        if (taskSnapshot.metadata!!.reference != null) {
                            val result: Task<Uri> = taskSnapshot.storage.downloadUrl
                            result.addOnSuccessListener { uri ->
                                val imageUrl: String = uri.toString()
                                cardView!!.visibility = View.GONE
                                progressDialog.dismiss()
                                val plantId: String? =
                                    databaseReference!!.child(DB_PLANTS)
                                        .push().key
                                val plant = Plant(
                                    plantId,
                                    title,
                                    imageUrl,
                                    0,
                                    -99,
                                    -1,
                                    0,
                                    userUid,
                                    code
                                )
                                databaseReference!!.child(DB_PLANTS)
                                    .child(
                                        (plantId)!!
                                    ).setValue(plant)
                            }
                        }
                    }
                }.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }.addOnProgressListener { taskSnapshot ->
                    val progress: Double =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnItemSelectedListener) {      // context instanceof YourActivity
            listener = context // = (YourActivity) context
        } else {
            throw ClassCastException(
                (context.toString()
                        + " must implement PlantsFragment.OnItemSelectedListener")
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseReference!!.child(DB_PLANTS)
            .removeEventListener((mDBListener)!!)
    }

    interface OnItemSelectedListener {
        fun onPlantItemSelected(plant: Plant)
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = PlantsFragment()
                return field
            }
            private set
        const val REQUEST_IMAGE_CAPTURE: Int = 1
    }
}