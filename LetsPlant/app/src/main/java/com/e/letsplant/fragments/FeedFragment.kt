package com.e.letsplant.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.letsplant.R
import com.e.letsplant.adapters.PostAdapter
import com.e.letsplant.data.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class FeedFragment : MainFragment() {
    private var noPostsMessageTextView: TextView? = null
    private var posts: MutableList<Post?>? = null
    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var friendsList: MutableList<String?>? = null
    var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title =
            "Let's Plant"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false)
        userUid = firebaseAuth!!.uid
        posts = ArrayList()
        noPostsMessageTextView = rootView!!.findViewById(R.id.noPostsMessageTextView)
        recyclerView = rootView!!.findViewById(R.id.feedRecyclerView)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        postAdapter = PostAdapter(context, posts!!)
        recyclerView!!.adapter = postAdapter
        checkFriends()
        return rootView
    }

    private fun checkFriends() {
        friendsList = ArrayList()
        databaseReference!!.child(DB_FOLLOW).child((userUid)!!)
            .child("friends").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList?.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        friendsList?.add(dataSnapshot.key)
                    }
                    readPosts()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun readPosts() {
        databaseReference!!.child(DB_POSTS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    posts!!.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val post: Post? = dataSnapshot.getValue(Post::class.java)
                        for (id: String? in friendsList!!) if ((post?.publisher == id)) {
                            posts!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                    if (posts!!.size == 0) {
                        noPostsMessageTextView!!.visibility = View.VISIBLE
                    } else {
                        noPostsMessageTextView!!.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = FeedFragment()
                return field
            }
            private set
    }
}