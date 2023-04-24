package com.e.letsplant.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.adapters.MyPhotosAdapter
import com.e.letsplant.data.Post
import com.e.letsplant.data.User
import com.e.letsplant.interfaces.ProfileSettingsEventListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class ProfileFragment : MainFragment() {
    private var profileSettingsEventListener: ProfileSettingsEventListener? = null
    private var isProfileSettingsOpen: Boolean = false
    private var posts: TextView? = null
    private var friends: TextView? = null
    private var image_profile: ImageView? = null
    private var edit_profile: Button? = null
    private var email: EditText? = null
    private var username: EditText? = null
    private var phone: EditText? = null
    private var location: EditText? = null
    private var my_photos: ImageButton? = null
    private var saved_photos: ImageButton? = null
    var recyclerView_posts: RecyclerView? = null
    var myPhotosAdapter: MyPhotosAdapter? = null
    var postList: MutableList<Post?>? = null
    var mySaves: MutableList<String?>? = null
    var recyclerView_saves: RecyclerView? = null
    var myPhotosAdapter_saves: MyPhotosAdapter? = null
    lateinit var postList_saves: MutableList<Post?>
    private var menu: Menu? = null
    private var filePath: Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var profileId: String? = null
    var userUid: String? = null
    private var receiverUpdateDownload: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val open: Boolean = intent.getBooleanExtra("open", false)
            if (open) {
                initialize(true)
                menu!!.getItem(0).isVisible = true
                menu!!.getItem(1).isVisible = true
                menu!!.getItem(2).isVisible = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title =
            "Profile"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        userUid = firebaseAuth!!.uid
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileId = sharedPreferences.getString("profileId", "none")
        edit_profile = rootView?.findViewById(R.id.edit_profile)
        posts = rootView?.findViewById(R.id.posts)
        friends = rootView?.findViewById(R.id.friends)
        image_profile = rootView?.findViewById(R.id.profileImageView)
        email = rootView?.findViewById(R.id.emailEditText)
        username = rootView?.findViewById(R.id.usernameEditText)
        phone = rootView?.findViewById(R.id.phoneEditText)
        location = rootView?.findViewById(R.id.locationEditText)
        my_photos = rootView?.findViewById(R.id.my_photos)
        saved_photos = rootView?.findViewById(R.id.saved_photos)
        recyclerView_posts = rootView?.findViewById(R.id.recycler_view_photos)
        recyclerView_posts?.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerView_posts?.layoutManager = linearLayoutManager
        postList = ArrayList()
        myPhotosAdapter = MyPhotosAdapter(context, postList!!)
        recyclerView_posts?.adapter = myPhotosAdapter
        recyclerView_saves = rootView?.findViewById(R.id.recycler_view_saved_photos)
        recyclerView_saves?.setHasFixedSize(true)
        val linearLayoutManager_saves: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerView_saves?.layoutManager = linearLayoutManager_saves
        postList_saves = ArrayList()
        myPhotosAdapter_saves = MyPhotosAdapter(context, postList_saves)
        recyclerView_saves?.adapter = myPhotosAdapter_saves
        recyclerView_posts?.visibility = View.VISIBLE
        recyclerView_saves?.visibility = View.GONE
        userInfo()
        initialize(false)
        getFriends()
        nrPosts
        photos
        saves
        if ((profileId == userUid)) {
            edit_profile?.visibility = View.GONE
        } else {
            edit_profile?.visibility = View.VISIBLE
            checkFriend()
            saved_photos?.visibility = View.GONE
        }
        edit_profile?.setOnClickListener {
            when (edit_profile?.text.toString()) {
                "edit profile" -> {}
                "add friend" -> databaseReference!!.child("Follow").child((userUid)!!)
                    .child("friends").child(
                        (profileId)!!
                    ).setValue(true)
                "unfriend" -> databaseReference!!.child("Follow").child((userUid)!!)
                    .child("friends").child(
                        (profileId)!!
                    ).removeValue()
            }
        }
        my_photos?.setOnClickListener {
            recyclerView_posts?.visibility = View.VISIBLE
            recyclerView_saves?.visibility = View.GONE
        }
        saved_photos?.setOnClickListener {
            recyclerView_posts?.visibility = View.GONE
            recyclerView_saves?.visibility = View.VISIBLE
        }
        val filter = IntentFilter("openEditProfileSettings")
        requireActivity().registerReceiver(receiverUpdateDownload, filter)
        return rootView
    }

    private fun userInfo() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading profile")
        progressDialog.show()
        databaseReference!!.child(DB_USERS).child((profileId)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (context == null) {
                        return
                    }
                    currentUserInfo = (snapshot.getValue(User::class.java))!!
                    Glide.with((context)!!).load(currentUserInfo.profileImage)
                        .into(image_profile!!)
                    filePath = Uri.parse(currentUserInfo.profileImage)
                    email?.setText(currentUserInfo.email)
                    username?.setText(currentUserInfo.username)
                    phone?.setText(currentUserInfo.phone)
                    location?.setText(currentUserInfo.location)
                    longitude = currentUserInfo.longitude
                    latitude = currentUserInfo.latitude
                    progressDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadUser:onCancelled", error.toException())
                }
            })
    }

    private fun checkFriend() {
        databaseReference!!.child(DB_FOLLOW).child((userUid)!!)
            .child("friends").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child((profileId)!!)
                            .exists()
                    ) edit_profile!!.text = "unfriend" else edit_profile!!.text = "add friend"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getFriends() {
        databaseReference!!.child(DB_FOLLOW).child((profileId)!!)
            .child("friends").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friends!!.text = "" + snapshot.childrenCount
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private val nrPosts: Unit
        get() {
            databaseReference!!.child(DB_POSTS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var i: Int = 0
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val post: Post? = dataSnapshot.getValue(Post::class.java)
                            if ((Objects.requireNonNull(post)?.publisher == profileId)) i++
                        }
                        posts!!.text = "" + i
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun initialize(value: Boolean) {
        email!!.isCursorVisible = value
        email!!.isLongClickable = value
        email!!.isClickable = value
        email!!.isFocusable = value
        email!!.isSelected = value
        username!!.isCursorVisible = value
        username!!.isLongClickable = value
        username!!.isClickable = value
        username!!.isFocusable = value
        username!!.isFocusableInTouchMode = value
        username!!.isSelected = value
        phone!!.isCursorVisible = value
        phone!!.isLongClickable = value
        phone!!.isClickable = value
        phone!!.isFocusable = value
        phone!!.isFocusableInTouchMode = value
        phone!!.isSelected = value
        location!!.isCursorVisible = value
        location!!.isLongClickable = value
        location!!.isClickable = value
        location!!.isFocusable = value
        location!!.isFocusableInTouchMode = value
        location!!.isSelected = value
        if (!value) {
            email!!.setBackgroundResource(android.R.color.transparent)
            username!!.setBackgroundResource(android.R.color.transparent)
            phone!!.setBackgroundResource(android.R.color.transparent)
            location!!.setBackgroundResource(android.R.color.transparent)
            image_profile!!.setOnClickListener(null)
        } else {
            email!!.setBackgroundResource(android.R.drawable.edit_text)
            username!!.setBackgroundResource(android.R.drawable.edit_text)
            phone!!.setBackgroundResource(android.R.drawable.edit_text)
            location!!.setBackgroundResource(android.R.drawable.edit_text)
            image_profile!!.setOnClickListener { selectImage() }
            context?.let { Places.initialize(it, "AIzaSyAYJE72q4PNe5oH5GbT2OHV186UO3t30qo") }
            location!!.isFocusable = false
            location!!.setOnClickListener {
                val fieldList: List<Place.Field> =
                    listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
                val intent: Intent? =
                    activity?.let {
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                            .build(it)
                    }
                startActivityForResult(intent, 100)
            }
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            profileSettingsEventListener = activity as ProfileSettingsEventListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement ProfileSettingsEventListener")
        }
    }

    override fun onPause() {
        super.onPause()
        isProfileSettingsOpen = false
        profileSettingsEventListener!!.closeActivityBottomSheet()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.top_profile_bar_menu, menu)
        menu.getItem(0).isVisible = false
        menu.getItem(1).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_menu) {
            if (!isProfileSettingsOpen) {
                isProfileSettingsOpen = true
                profileSettingsEventListener!!.openActivityBottomSheet()
            } else {
                isProfileSettingsOpen = false
                profileSettingsEventListener!!.closeActivityBottomSheet()
            }
            return true
        }
        if (item.itemId == R.id.action_cancel) {
            initialize(false)
            menu!!.getItem(0).isVisible = false
            menu!!.getItem(1).isVisible = false
            menu!!.getItem(2).isVisible = true
            return true
        }
        if (item.itemId == R.id.action_save) {
            updateCurrentUserProfile()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateCurrentUserProfile() {
        val email: String = email!!.text.toString().trim { it <= ' ' }
        val username: String = username!!.text.toString().trim { it <= ' ' }
        val phone: String = phone!!.text.toString().trim { it <= ' ' }
        val location: String = location!!.text.toString().trim { it <= ' ' }
        val profileImage: String = filePath.toString()
        if (TextUtils.isEmpty(username)) {
            this.username!!.error = "Username is required!"
            return
        }
        if (username.length < 4) {
            this.username!!.error = "Username must be >= 4  characters"
            return
        }
        val user =
            User(userUid, email, location, latitude, longitude, phone, profileImage, username)
        val userValues: Map<String, Any?> = user.toMap()
        databaseReference!!.child(DB_USERS).child((userUid)!!)
            .updateChildren(
                (userValues)
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    menu!!.getItem(0).isVisible = false
                    menu!!.getItem(1).isVisible = false
                    menu!!.getItem(2).isVisible = true
                }
            }
        if (profileImage != "" && !profileImage.contains("https")) {
            uploadImage()
        }
        initialize(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == Activity.RESULT_OK) && (data != null) && (data.data != null)) {
            filePath = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    filePath
                )
                image_profile!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)
            location!!.setText(place.address)
            latitude = place.latLng.latitude
            longitude = place.latLng.longitude
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status: Status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(context, status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Updating...")
            progressDialog.show()
            val ref: StorageReference =
                storageReference!!.child(ALL_USER_PROFILE_IMAGES)
                    .child(userUid + "." + getFileExtension(filePath))
            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    if (taskSnapshot.metadata != null) {
                        if (taskSnapshot.metadata!!.reference != null) {
                            val result: Task<Uri> = taskSnapshot.storage.downloadUrl
                            result.addOnSuccessListener { uri ->
                                val imageUrl: String = uri.toString()
                                progressDialog.dismiss()
                                databaseReference!!.child(DB_USERS)
                                    .child(
                                        (userUid)!!
                                    ).child("profileImage").setValue(imageUrl)
                            }
                        }
                    }
                }.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private val photos: Unit
        get() {
            databaseReference!!.child(DB_POSTS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList!!.clear()
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val post: Post? = dataSnapshot.getValue(Post::class.java)
                            if ((post?.publisher == profileId)) postList!!.add(post)
                        }
                        postList?.reverse()
                        myPhotosAdapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    private val saves: Unit
        get() {
            mySaves = ArrayList()
            databaseReference!!.child(DB_SAVES).child((userUid)!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            mySaves?.add(dataSnapshot.key)
                        }
                        readSaves()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun readSaves() {
        databaseReference?.child(DB_POSTS)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList_saves!!.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val post: Post? = dataSnapshot.getValue(Post::class.java)
                        for (id: String? in mySaves!!) if ((post?.postId == id)) postList_saves!!.add(
                            post
                        )
                    }
                    myPhotosAdapter_saves?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = ProfileFragment()
                return field
            }
            private set
    }
}