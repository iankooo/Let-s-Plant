package com.e.letsplant.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.letsplant.R
import com.e.letsplant.data.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class MyPhotosAdapter constructor(private val context: Context?, private val posts: List<Post?>) :
    MainAdapter() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_photos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post: Post? = posts[position]
        Glide.with((context)!!).load(post!!.postImage).into((holder as ViewHolder).post_image)
        holder.post_image.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val popupMenu = PopupMenu(context, v)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.edit_post -> {
                                editPost(post.postId)
                                return true
                            }
                            R.id.delete_post -> {
                                databaseReference!!.child("Posts").child(post.postId!!)
                                    .removeValue()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) Toast.makeText(
                                            context,
                                            "Post deleted!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                return true
                            }
                            else -> return false
                        }
                    }
                })
                popupMenu.inflate(R.menu.post_menu)
                popupMenu.show()
            }
        })
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var post_image: ImageView

        init {
            post_image = itemView.findViewById(R.id.post_image)
        }
    }

    private fun editPost(postId: String?) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(
            (context)!!
        )
        alertDialog.setTitle("Edit Post")
        val editText = EditText(context)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = layoutParams
        alertDialog.setView(editText)
        getText(postId, editText)
        alertDialog.setPositiveButton(
            "Edit"
        ) { _, _ ->
            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["description"] = editText.text.toString()
            databaseReference!!.child("Posts").child((postId)!!).updateChildren(hashMap)
        }.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun getText(postId: String?, editText: EditText) {
        databaseReference!!.child("Posts").child((postId)!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    editText.setText(
                        Objects.requireNonNull(
                            snapshot.getValue(
                                Post::class.java
                            )
                        )!!.description
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}