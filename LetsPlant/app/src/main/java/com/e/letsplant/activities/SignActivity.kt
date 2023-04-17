package com.e.letsplant.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.e.letsplant.R
import com.e.letsplant.data.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class SignActivity : MainActivity() {
    private var signTextView: TextView? = null
    private var usernameRelativeLayout: RelativeLayout? = null
    private var confirmPasswordRelativeLayout: RelativeLayout? = null
    private var forgetPasswordLinearLayout: LinearLayout? = null
    private var signButton: Button? = null
    private var orLinearLayout: LinearLayout? = null
    private var backToTextView: TextView? = null
    private var viewSignIn: Boolean = true
    private var username: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var progressBar: ProgressBar? = null
    var userUid: String? = null
    private var userViewModel: UserViewModel? = null
    override var firebaseAuth: FirebaseAuth? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        firebaseAuth = FirebaseAuth.getInstance()
        initialize()
        checkWhatToShowOnLayout()
    }

    private fun initialize() {
        signTextView = findViewById(R.id.signTextView)
        usernameRelativeLayout = findViewById(R.id.usernameRelativeLayout)
        confirmPasswordRelativeLayout = findViewById(R.id.confirmPasswordRelativeLayout)
        forgetPasswordLinearLayout = findViewById(R.id.forgetPasswordLinearLayout)
        signButton = findViewById(R.id.signButton)
        orLinearLayout = findViewById(R.id.orLinearLayout)
        backToTextView = findViewById(R.id.backToTextView)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        confirmPassword = findViewById(R.id.confirmPassword)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar_cyclic)
        if (firebaseAuth!!.currentUser != null) {
            startActivity(Intent(applicationContext, SecondActivity::class.java))
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkWhatToShowOnLayout() {
        signTextView!!.setOnClickListener {
            if (viewSignIn) {
                usernameRelativeLayout!!.visibility = View.VISIBLE
                confirmPasswordRelativeLayout!!.visibility = View.VISIBLE
                forgetPasswordLinearLayout!!.visibility = View.GONE
                signButton!!.text = "Sign Up"
                orLinearLayout!!.visibility = View.GONE
                backToTextView!!.visibility = View.VISIBLE
                signTextView!!.text = "Sign In"
                viewSignIn = false
            } else {
                usernameRelativeLayout!!.visibility = View.GONE
                confirmPasswordRelativeLayout!!.visibility = View.GONE
                forgetPasswordLinearLayout!!.visibility = View.VISIBLE
                signButton!!.text = "Sign In"
                orLinearLayout!!.visibility = View.VISIBLE
                backToTextView!!.visibility = View.GONE
                signTextView!!.text = "Sign Up"
                viewSignIn = true
            }
        }
    }

    fun showHidePass(view: View) {
        if (view.id == R.id.hideImageView) {
            if ((password!!.transformationMethod == PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.drawable.ic_eye)
                //Show Password
                password!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                ((view) as ImageView).setImageResource(R.drawable.ic_hide)
                //Hide Password
                password!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    fun onSignButtonPress() {
        val textSignButton: String = signButton!!.text.toString()
        val email: String = email!!.text.toString().trim { it <= ' ' }
        val password: String = password!!.text.toString().trim { it <= ' ' }
        val username: String = username!!.text.toString().trim { it <= ' ' }
        if (isFormValid) {
            if ((textSignButton == "Sign In")) {
                logIn(email, password)
            } else {
                signUp(email, password, username)
            }
        }
    }

    private fun logIn(email: String, password: String) {
        if (firebaseAuth!!.currentUser != null) userUid =
            (firebaseAuth!!.currentUser)!!.uid
        firebaseAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("Users").child(
                            firebaseAuth!!.currentUser!!.uid
                        )
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val intent =
                                Intent(this@SignActivity, SecondActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            progressBar!!.visibility = View.GONE
                        }
                    })
                } else {
                    errorAtSign(task)
                }
            }
    }

    private fun signUp(email: String, password: String, username: String) {
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val userUid: String = Objects.requireNonNull(
                        firebaseAuth!!.currentUser
                    )!!.uid
                    val user = User(
                        userUid, email, "", 0.0, 0.0, "",
                        "https://firebasestorage.googleapis.com/v0/b/let-s-plant-f845c.appspot.com/o/placeholder_profileImage.png?alt=media&token=ad5ae128-f579-40e8-ad90-0fccaeda16c7",
                        username
                    )
                    userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
                    userViewModel!!.getUserMutableLiveData().observe(
                        this
                    ) { userViewModel!!.setUserMutableLiveData(user) }
                    databaseReference!!.child(DB_USERS).child(userUid)
                        .setValue(user).addOnCompleteListener {
                            val intent =
                                Intent(applicationContext, SecondActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                } else {
                    errorAtSign(task)
                }
            }
    }

    private val isFormValid: Boolean
        get() {
            val email: String = email!!.text.toString().trim { it <= ' ' }
            val password: String = password!!.text.toString().trim { it <= ' ' }
            val username: String = username!!.text.toString().trim { it <= ' ' }
            val confirmPassword: String = confirmPassword!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                this.email!!.error = "Email is required!"
                return false
            }
            if (TextUtils.isEmpty(password)) {
                this.password!!.error = "Password is required!"
                return false
            }
            if (password.length < 6) {
                this.password!!.error = "Password must be >= 6  characters"
                return false
            }
            if (TextUtils.isEmpty(username) && usernameRelativeLayout!!.visibility == View.VISIBLE) {
                this.username!!.error = "Username is required!"
                return false
            }
            if (username.length < 4 && usernameRelativeLayout!!.visibility == View.VISIBLE) {
                this.username!!.error = "Username must be >= 4  characters"
                return false
            }
            if (confirmPassword != password && confirmPasswordRelativeLayout!!.visibility == View.VISIBLE) {
                this.confirmPassword!!.error = "Password does not match!"
                return false
            }
            progressBar!!.visibility = View.VISIBLE
            return true
        }

    private fun errorAtSign(task: Task<AuthResult?>) {
        Toast.makeText(
            this@SignActivity,
            Objects.requireNonNull(task.exception)!!.message,
            Toast.LENGTH_SHORT
        ).show()
        progressBar!!.visibility = View.GONE
    }

    fun onClickForgotPassword(v: View) {
        val resetMail = EditText(v.context)
        val passwordResetDialog: AlertDialog.Builder = AlertDialog.Builder(v.context)
        passwordResetDialog.setTitle("Reset password?")
        passwordResetDialog.setMessage("Enter your email to receive reset link")
        passwordResetDialog.setView(resetMail)
        passwordResetDialog.setPositiveButton(
            "Yes"
        ) { _: DialogInterface?, _: Int ->
            val mail: String = resetMail.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(mail)) {
                resetMail.error = "Email is required!"
                return@setPositiveButton
            }
            firebaseAuth!!.sendPasswordResetEmail(mail)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@SignActivity,
                        "Reset link sent to your email",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this@SignActivity,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }.setNegativeButton(
            "No"
        ) { _: DialogInterface?, _: Int -> }
        passwordResetDialog.create().show()
    }
}