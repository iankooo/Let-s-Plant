package com.e.letsplant.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.e.letsplant.R
import com.e.letsplant.data.Plant
import com.e.letsplant.fragments.*
import com.e.letsplant.interfaces.ProfileSettingsEventListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class SecondActivity : MainActivity(), PlantsFragment.OnItemSelectedListener,
    ProfileSettingsEventListener {
    private var floatingActionButton: FloatingActionButton? = null
    private lateinit var profileSettingsLinearLayout: LinearLayout
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var fragment: Fragment? = null
    private val fragmentManager: FragmentManager = supportFragmentManager
    var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)?.setDisplayShowTitleEnabled(true)
        setContentView(R.layout.activity_second)
        userUid = firebaseAuth!!.uid
        floatingActionButton = findViewById(R.id.feedButton)
        profileSettingsLinearLayout = findViewById(R.id.profileSettingsLinearLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(profileSettingsLinearLayout)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fragment = FeedFragment.instance
        fragmentManager.beginTransaction().replace(R.id.fragment_container, (fragment)!!, "")
            .commit()
    }

    @SuppressLint("NonConstantResourceId")
    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.app_bar_explore -> fragment = ExploreFragment.instance
                R.id.app_bar_plants -> fragment = PlantsFragment.instance
                R.id.app_bar_alerts -> fragment = UsersFragment.instance
                R.id.app_bar_profile -> {
                    val editor: SharedPreferences.Editor =
                        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    editor.putString("profileId", userUid)
                    editor.apply()
                    fragment = ProfileFragment.instance
                }
            }
            if (fragment != null) fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment!!, "").commit()
            true
        }

    fun onClickFeedButton(view: View?) {
        fragment = FeedFragment.instance
        fragmentManager.beginTransaction().replace(R.id.fragment_container, (fragment)!!, "")
            .commit()
    }

    override fun onPlantItemSelected(plant: Plant) {
        fragment = PlantDetailedFragment.instance
        val args = Bundle()
        args.putString("title", plant.title)
        args.putString("image", plant.image)
        fragment!!.arguments = args
        fragmentManager.beginTransaction().replace(R.id.fragment_container, (fragment)!!, "")
            .commit()
    }

    override fun openActivityBottomSheet() {
        floatingActionButton!!.visibility = View.GONE
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        profileSettingsLinearLayout.visibility = View.VISIBLE
        profileSettingsLinearLayout.findViewById<View>(R.id.minusProfileSettingsImageView)
            .setOnClickListener { closeActivityBottomSheet() }
        profileSettingsLinearLayout.findViewById<View>(R.id.logoutLinearLayout)
            .setOnClickListener { logout() }
        profileSettingsLinearLayout.findViewById<View>(R.id.editProfileLinearLayout)
            .setOnClickListener {
                closeActivityBottomSheet()
                val intent = Intent("openEditProfileSettings")
                intent.putExtra("open", true)
                sendBroadcast(intent)
            }
    }

    private fun logout() {
        val preferences: SharedPreferences =
            getSharedPreferences(PREFS, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
        firebaseAuth!!.signOut()
        startActivity(Intent(this@SecondActivity, SignActivity::class.java))
        finish()
    }

    override fun closeActivityBottomSheet() {
        profileSettingsLinearLayout.visibility = View.GONE
        floatingActionButton!!.visibility = View.VISIBLE
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                profileSettingsLinearLayout.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    profileSettingsLinearLayout.visibility = View.GONE
                    floatingActionButton!!.visibility = View.VISIBLE
                }
            }
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}