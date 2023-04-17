package com.e.letsplant.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.adapters.CommentAdapter
import com.e.letsplant.data.Comment
import com.e.letsplant.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class CommentsFragment : MainFragment() {
    private lateinit var addComment: EditText
    private var image_profile: ImageView? = null
    var userUid: String? = null
    private var postId: String? = null
    private var publisherId: String? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Comment?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title =
            "Comments"
        if (arguments != null) {
            postId = requireArguments().getString("postId")
            publisherId = requireArguments().getString("publisherId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_comments, container, false)
        userUid = firebaseAuth!!.uid
        addComment = view.findViewById(R.id.add_comment)
        image_profile = view.findViewById(R.id.image_profile)
        val post: TextView = view.findViewById(R.id.post)
        val recyclerView: RecyclerView = view.findViewById(R.id.commentsRecyclerView)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        commentList = ArrayList()
        commentAdapter = CommentAdapter(context, commentList as ArrayList<Comment?>)
        recyclerView.adapter = commentAdapter
        post.setOnClickListener {
            if ((addComment.text.toString() == "")) {
                Toast.makeText(context, "You can't send empty comment", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addComment()
            }
        }
        image
        readComments()
        return view
    }

    private fun addComment() {
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["comment"] = addComment.text.toString()
        hashMap["publisher"] = userUid
        databaseReference!!.child(DB_COMMENTS).child((postId)!!).push()
            .setValue(hashMap)
        addComment.setText("")
    }

    private val image: Unit
        get() {
            databaseReference!!.child(DB_USERS).child((userUid)!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user: User? = snapshot.getValue(
                            User::class.java
                        )
                        Glide.with((context)!!).load(user?.profileImage)
                            .into(image_profile!!)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun readComments() {
        databaseReference!!.child(DB_COMMENTS).child((postId)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentList!!.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val comment: Comment? = dataSnapshot.getValue(
                            Comment::class.java
                        )
                        commentList!!.add(comment)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = CommentsFragment()
                return field
            }
            private set
    }
}