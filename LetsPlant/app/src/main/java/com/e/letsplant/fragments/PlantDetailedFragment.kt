package com.e.letsplant.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.e.letsplant.R

class PlantDetailedFragment : MainFragment() {
    private var plantId: String = ""
    private var title: String = ""
    private var image: String = ""
    private var plantDetailedTextView: TextView? = null
    private var plantDetailedImageView: ImageView? = null
    private var plantDetailedDescriptionEditView: EditText? = null
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            plantId = requireArguments().getString("plantId", "")
            title = requireArguments().getString("title", "")
            image = requireArguments().getString("image", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userUid = firebaseAuth!!.uid
        return inflater.inflate(R.layout.fragment_plant_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        plantDetailedTextView = view.findViewById(R.id.plantDetailedTextView)
        plantDetailedImageView = view.findViewById(R.id.plantDetailedImageView)
        plantDetailedDescriptionEditView = view.findViewById(R.id.plantDetailedDescriptionEditView)
        plantDetailedTextView?.text = title
        Glide.with(requireContext())
            .load(image)
            .into(plantDetailedImageView!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_plant_detailed_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_post) {
            uploadPost()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadPost() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Posting...")
        progressDialog.show()
        val postId: String? =
            databaseReference!!.child(DB_POSTS).push().key
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["postId"] = postId
        hashMap["postImage"] = image
        hashMap["description"] = plantDetailedDescriptionEditView!!.text.toString()
        hashMap["publisher"] = userUid
        databaseReference!!.child(DB_POSTS).child((postId)!!)
            .setValue(hashMap).addOnSuccessListener {
                Toast.makeText(context, "Posted successfully!", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        progressDialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        plantId = ""
        title = ""
        image = ""
    }

    companion object {
        var instance: Fragment? = null
            get() {
                if (field == null) field = PlantDetailedFragment()
                return field
            }
            private set
    }
}