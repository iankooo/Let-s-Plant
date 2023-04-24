package com.e.letsplant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.e.letsplant.App
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

abstract class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var databaseReference: DatabaseReference? = App.databaseReference
    var firebaseAuth: FirebaseAuth? = App.firebaseAuthReference
    var userUid = firebaseAuth!!.uid

    companion object {
        const val DB_USERS = "Users"
        const val DB_SAVES = "Saves"
        const val DB_LIKES = "Likes"
        const val DB_COMMENTS = "Comments"
        const val DB_FOLLOW = "Follow"
    }
}