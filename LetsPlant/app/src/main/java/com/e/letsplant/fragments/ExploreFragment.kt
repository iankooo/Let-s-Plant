package com.e.letsplant.fragments

import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.e.letsplant.R
import com.e.letsplant.data.User
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class ExploreFragment : MainFragment(), OnMapReadyCallback, LocationListener,
    OnMarkerClickListener, OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private var friendsList: MutableList<String?>? = null
    private var mMap: GoogleMap? = null
    var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title =
            ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_explore, container, false)
        userUid = FirebaseAuth.getInstance().currentUser!!.uid
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return rootView
    }

    override fun onLocationChanged(location: Location) {}
    override fun onConnected(bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onMarkerClick(marker: Marker): Boolean {
        var clickCount: Int? = marker.tag as Int? ?: return false
        clickCount = if (clickCount == 0) {
            1
        } else {
            0
        }
        marker.tag = clickCount
        return false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success: Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.places_keep)
            )
            if (!success) {
                Log.e("map_style", "map style updated please do check it")
            }
        } catch (e: NotFoundException) {
            Log.e("map_style", "map is not updated yet ... do some other stuff")
        }
        mMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        googleMap.mapType = R.raw.places_keep
        googleMap.setOnInfoWindowClickListener(this)
        databaseReference!!.child(MainFragment.DB_USERS).child((userUid)!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //String userUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                    val currentUser: User? = snapshot.getValue(
                        User::class.java
                    )
                    checkFriends()
                    val location =
                        LatLng(currentUser?.latitude!!, currentUser.longitude)
                    val marker: Marker = mMap!!.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(currentUser.username)
                            .snippet(currentUser.phone)
                    )
                    marker.tag = 0
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun checkFriends() {
        friendsList = ArrayList()
        databaseReference!!.child("Follow").child((userUid)!!).child("friends")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (friendsList as ArrayList<String?>).clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        (friendsList as ArrayList<String?>).add(dataSnapshot.key)
                    }
                    readFriendsLocations()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun readFriendsLocations() {
        databaseReference!!.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user: User? = dataSnapshot.getValue(
                        User::class.java
                    )
                    for (id: String? in friendsList!!) if ((user?.id == id)) {
                        val location = LatLng(user?.latitude!!, user.longitude)
                        val marker: Marker = mMap!!.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(user.username)
                                .snippet(user.phone)
                        )
                        marker.tag = 0
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        val telephone: String = "tel:" + marker.snippet
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(telephone)
        startActivity(intent)
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = ExploreFragment()
                return field
            }
            private set
    }
}