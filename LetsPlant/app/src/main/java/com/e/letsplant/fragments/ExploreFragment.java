package com.e.letsplant.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends MainFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Fragment instance = null;
    private List<String> friendsList;
    private GoogleMap mMap;
    String userUid;

    public ExploreFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new ExploreFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer clickCount = (Integer) marker.getTag();

        if (clickCount == null)
            return false;

        if (clickCount == 0) {
            clickCount = 1;
        } else {
            clickCount = 0;
        }
        marker.setTag(clickCount);
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.places_keep));
            if (!success) {
                Log.e("map_style", "map style updated please do check it");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("map_style", "map is not updated yet ... do some other stuff");
        }

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(R.raw.places_keep);
        googleMap.setOnInfoWindowClickListener(this);


        databaseReference.child(DB_USERS).child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String userUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                User currentUser = snapshot.getValue(User.class);

                checkFriends();

                LatLng location = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(currentUser.getUsername())
                        .snippet(currentUser.getPhone())
                );
                marker.setTag(0);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFriends() {
        friendsList = new ArrayList<>();

        databaseReference.child("Follow").child(userUid).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendsList.add(dataSnapshot.getKey());
                }
                readFriendsLocations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFriendsLocations() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id : friendsList)
                        if (user.getId().equals(id)) {
                            LatLng location = new LatLng(user.getLatitude(), user.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(user.getUsername())
                                    .snippet(user.getPhone())
                            );
                            marker.setTag(0);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String telephone = "tel:" + marker.getSnippet();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(telephone));
        startActivity(intent);
    }
}