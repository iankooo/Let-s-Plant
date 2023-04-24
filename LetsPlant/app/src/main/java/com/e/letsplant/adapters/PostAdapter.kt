package com.e.letsplant.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.data.Post
import com.e.letsplant.data.User
import com.e.letsplant.fragments.CommentsFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PostAdapter(private val context: Context?, private val posts: List<Post?>) : MainAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = posts[position]
        Glide.with((context)!!).load(post?.postImage)
            .into((holder as PostViewHolder).post_image)
        if ((post?.description == "")) holder.description.visibility = View.GONE else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post?.description
        }
        publisherInfo(
            holder.image_profile,
            holder.username,
            holder.publisher, post?.publisher
        )
        isLiked(post?.postId, holder.like)
        nrLikes(holder.likes, post?.postId)
        getComments(post?.postId, holder.comments)
        isSaved(post?.postId, holder.save)
        holder.save.setOnClickListener {
            if ((holder.save.tag == "save")) {
                databaseReference!!.child(DB_SAVES).child((userUid)!!)
                    .child(post?.postId!!).setValue(true)
            } else {
                databaseReference!!.child(DB_SAVES).child((userUid)!!)
                    .child(post?.postId!!).removeValue()
            }
        }
        holder.like.setOnClickListener {
            if ((holder.like.tag == "like")) {
                databaseReference!!.child(DB_LIKES)
                    .child(post?.postId!!).child(
                        (userUid)!!
                    ).setValue(true)
            } else {
                databaseReference!!.child(DB_LIKES)
                    .child(post?.postId!!).child(
                        (userUid)!!
                    ).removeValue()
            }
        }
        holder.comment.setOnClickListener { v ->
            val appCompatActivity = v.context as AppCompatActivity
            val fragment: Fragment = CommentsFragment.instance!!
            val bundle = Bundle()
            bundle.putString("postId", post?.postId)
            bundle.putString("publisherId", post?.publisher)
            fragment.arguments = bundle
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, (fragment)).commit()
        }
        holder.comments.setOnClickListener { v ->
            val appCompatActivity = v.context as AppCompatActivity
            val fragment: Fragment = CommentsFragment.instance!!
            val bundle = Bundle()
            bundle.putString("postId", post?.postId)
            bundle.putString("publisherId", post?.publisher)
            fragment.arguments = bundle
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, (fragment)).commit()
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_profile: ImageView
        var post_image: ImageView
        var like: ImageView
        var comment: ImageView
        var save: ImageView
        var username: TextView
        var likes: TextView
        var comments: TextView
        var publisher: TextView
        var description: TextView

        init {
            image_profile = itemView.findViewById(R.id.image_profile)
            post_image = itemView.findViewById(R.id.post_image)
            like = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            save = itemView.findViewById(R.id.save)
            username = itemView.findViewById(R.id.username)
            likes = itemView.findViewById(R.id.likes)
            comments = itemView.findViewById(R.id.comments)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
        }
    }

    private fun getComments(postId: String?, comments: TextView) {
        databaseReference!!.child(DB_COMMENTS).child((postId)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.text = "View all " + snapshot.childrenCount + " comments"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun isLiked(postid: String?, imageView: ImageView) {
        databaseReference!!.child(DB_LIKES).child((postid)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child((userUid)!!).exists()) {
                        imageView.setImageResource(R.drawable.ic_heart_red)
                        imageView.tag = "liked"
                    } else {
                        imageView.setImageResource(R.drawable.ic_heart)
                        imageView.tag = "like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun nrLikes(likes: TextView, postid: String?) {
        databaseReference!!.child(DB_LIKES).child((postid)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    likes.text = snapshot.childrenCount.toString() + " likes"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun publisherInfo(
        image_profile: ImageView,
        username: TextView,
        publisher: TextView,
        userId: String?
    ) {
        databaseReference!!.child(DB_USERS).child((userId)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    Glide.with((context)!!).load(user?.profileImage).into(image_profile)
                    username.text = user?.username
                    publisher.text = user?.username
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun isSaved(postId: String?, imageView: ImageView) {
        databaseReference!!.child(DB_SAVES).child((userUid)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child((postId)!!).exists()) {
                        imageView.setImageResource(R.drawable.ic_bookmark)
                        imageView.tag = "saved"
                    } else {
                        imageView.setImageResource(R.drawable.ic_bookmark_save)
                        imageView.tag = "save"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}