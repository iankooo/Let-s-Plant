package com.e.letsplant.activities

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.e.letsplant.App
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

abstract class MainActivity : AppCompatActivity() {
    var databaseReference: DatabaseReference? = App.databaseReference
    open var firebaseAuth: FirebaseAuth? = App.firebaseAuthReference
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
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

    companion object {
        const val PREFS: String = "PREFS"
        const val DB_USERS: String = "Users"
    }
}