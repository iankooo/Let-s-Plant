package com.e.letsplant.adapters

import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.data.*
import com.e.letsplant.fragments.FeedFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CommentAdapter constructor(
    private val context: Context?,
    private val comments: List<Comment?>
) : MainAdapter() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment: Comment? = comments[position]
        (holder as CommentViewHolder).comment.text = comment!!.comment
        getUserInfo(holder.image_profile, holder.username, comment.publisher)
        holder.comment.setOnClickListener { v ->
            val appCompatActivity: AppCompatActivity = v.context as AppCompatActivity
            val fragment: Fragment = FeedFragment()
            val bundle = Bundle()
            bundle.putString("publisherId", comment.publisher)
            fragment.arguments = bundle
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
        }
        holder.image_profile.setOnClickListener { v ->
            val appCompatActivity: AppCompatActivity = v.context as AppCompatActivity
            val fragment: Fragment = FeedFragment()
            val bundle = Bundle()
            bundle.putString("publisherId", comment.publisher)
            fragment.arguments = bundle
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class CommentViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_profile: ImageView
        var username: TextView
        var comment: TextView

        init {
            image_profile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)
        }
    }

    private fun getUserInfo(imageView: ImageView, username: TextView, publisherId: String?) {
        databaseReference!!.child(DB_USERS).child((publisherId)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(
                        User::class.java
                    )
                    Glide.with((context)!!).load(user!!.profileImage).into(imageView)
                    username.text = user.username
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}