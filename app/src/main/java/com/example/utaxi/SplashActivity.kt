package com.example.utaxi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.get
import com.example.utaxi.Models.DriverInfoModel
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import kotlinx.android.synthetic.main.register.*
import kotlinx.android.synthetic.main.register.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    companion object {
        private val LOGIN_REQUEST_CODE = 7171
    }

    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener


    override fun onStart() {
        super.onStart()

        delaySplash()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (firebaseAuth != null && listener != null) firebaseAuth.addAuthStateListener(listener)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progressbar)

        init()

    }

    private fun delaySplash() {

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe({
                    firebaseAuth.addAuthStateListener { listener }
                })


        Toast.makeText(this@SplashActivity, "Splash is gone", Toast.LENGTH_SHORT).show()

    }

    private fun init() {


        providers = Arrays.asList(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()

        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null) {

                val intent = Intent(this,DriverHomeActivity::class.java)
                startActivity(intent)
                    finish()
            } else {
                showLoginLayout()
            }
        }
    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.log_in)
                .setPhoneButtonId(R.id.btn_login_phone)
                .setGoogleButtonId(R.id.btn_login_google)
                .build()



        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setTheme(R.style.DialogTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(), LOGIN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this.applicationContext, " " + response!!.error!!.message, Toast.LENGTH_SHORT).show()

            }

        }
    }

}
