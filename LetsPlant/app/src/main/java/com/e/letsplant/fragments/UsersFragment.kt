package com.e.letsplant.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.letsplant.R
import com.e.letsplant.adapters.UserAdapter
import com.e.letsplant.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class UsersFragment : MainFragment() {
    private var mUsers: MutableList<User?>? = null
    private var noUsersMessageTextView: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Objects.requireNonNull((requireActivity() as AppCompatActivity).supportActionBar)?.title = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (firebaseAuth!!.currentUser != null) userUid =
            firebaseAuth!!.currentUser!!.uid
        rootView = inflater.inflate(R.layout.fragment_users, container, false)
        noUsersMessageTextView = rootView?.findViewById(R.id.noFriendsMessageTextView)
        recyclerView = rootView?.findViewById(R.id.friendsRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mUsers = ArrayList()
        userAdapter = UserAdapter(context, mUsers)
        recyclerView?.adapter = userAdapter
        allUsers
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_feed_bar_menu, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                if (!TextUtils.isEmpty(s.trim { it <= ' ' }) && s != "") {
                    searchUsers(s)
                } else {
                    allUsers
                }
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (!TextUtils.isEmpty(s.trim { it <= ' ' }) && s != "") {
                    searchUsers(s)
                } else {
                    allUsers
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchUsers(query: String) {
        databaseReference!!.child(DB_USERS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mUsers!!.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val user: User? = dataSnapshot.getValue(
                            User::class.java
                        )
                        if (user?.id != userUid) {
                            if (user?.username?.lowercase(Locale.getDefault())?.contains(
                                    query.lowercase(
                                        Locale.getDefault()
                                    )
                                )!!
                            ) {
                                mUsers!!.add(user)
                            }
                        }
                    }
                    userAdapter!!.updateDataSet(mUsers)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private val allUsers: Unit
        get() {
            databaseReference!!.child(DB_USERS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mUsers!!.clear()
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val user: User? = dataSnapshot.getValue(
                                User::class.java
                            )
                            if (firebaseAuth!!.currentUser != null) {
                                if (user?.id != userUid) {
                                    mUsers!!.add(user)
                                }
                            }
                        }
                        userAdapter!!.updateDataSet(mUsers)
                        if (mUsers!!.size == 0) {
                            noUsersMessageTextView!!.visibility = View.VISIBLE
                        } else {
                            noUsersMessageTextView!!.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = UsersFragment()
                return field
            }
            private set
    }
}