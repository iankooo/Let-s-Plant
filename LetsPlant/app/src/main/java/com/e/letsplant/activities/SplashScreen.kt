package com.e.letsplant.activities

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SplashScreen : MainActivity() {

    private val fAuthListener: AuthStateListener =
        AuthStateListener { firebaseAuth ->
            val intent: Intent
            val userUid: String? = firebaseAuth.uid
            if (userUid != null) {
                intent = Intent(applicationContext, SecondActivity::class.java)
            } else {
                intent = Intent(applicationContext, SignActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

    public override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener((fAuthListener))
    }

    public override fun onStop() {
        super.onStop()
        firebaseAuth!!.removeAuthStateListener(fAuthListener)
    }
}