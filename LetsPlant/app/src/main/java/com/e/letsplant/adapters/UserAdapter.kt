package com.e.letsplant.adapters

import android.content.*
import android.view.*
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.data.*
import com.e.letsplant.fragments.ProfileFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserAdapter constructor(private val context: Context?, private var users: List<User?>?) :
    MainAdapter() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user: User? = users!![position]
        (holder as UserViewHolder).actionTextView.visibility = View.VISIBLE
        holder.userUsernameTextView.text = user?.username
        Glide.with((context)!!).load(user?.profileImage).into(holder.userProfileImageView)
        checkIsFriend(user, holder.actionTextView)
        holder.itemView.setOnClickListener {
            val editor: SharedPreferences.Editor =
                context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileId", user?.id)
            editor.apply()
            (context as FragmentActivity?)!!.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment.instance!!)
                .commit()
        }
        holder.actionTextView.setOnClickListener { v ->
            if ((v.tag.toString() == "user")) {
                addFriend(user)
                v.tag = "friend"
            } else {
                unFriend(user)
                v.tag = "user"
            }
        }
    }

    fun updateDataSet(usersList: List<User?>?) {
        users = usersList
        notifyDataSetChanged()
    }

    private fun checkIsFriend(user: User?, actionTextView: ImageView) {
        databaseReference!!.child(DB_FOLLOW).child((userUid)!!)
            .child("friends")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(user?.id!!)
                            .exists()
                    ) actionTextView.setImageResource(R.drawable.ic_user_24dp) else actionTextView.setImageResource(
                        R.drawable.ic_add_user
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun addFriend(newFriend: User?) {
        databaseReference!!.child(DB_FOLLOW).child((userUid)!!)
            .child("friends").child(newFriend?.id!!).setValue(true)
    }

    private fun unFriend(user: User?) {
        databaseReference!!.child(DB_FOLLOW).child((userUid)!!)
            .child("friends").child(user?.id!!).removeValue()
    }

    override fun getItemCount(): Int {
        return users!!.size
    }

    class UserViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userProfileImageView: ImageView
        var userUsernameTextView: TextView
        var actionTextView: ImageView

        init {
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView)
            userUsernameTextView = itemView.findViewById(R.id.userUsernameTextView)
            actionTextView = itemView.findViewById(R.id.actionTextView)
        }
    }
}