package com.example.hilaltaxi

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.hilaltaxi.databinding.ScreenSplashBinding
import com.example.hilaltaxi.model.DriverInfoModel
import com.example.hilaltaxi.ui.homeActivity.DriverHomeActivity
import com.example.hilaltaxi.ui.registrationDialog.RegistrationFragment
import com.example.hilaltaxi.util.Constraints.CURRENT_USER
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreen : AppCompatActivity(), RegistrationFragment.Continue {
    lateinit var binding: ScreenSplashBinding

    @Inject
    lateinit var providers: List<AuthUI.IdpConfig>

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var database: FirebaseDatabase

    @Inject
    lateinit var driverInfoRef: DatabaseReference
    lateinit var listener: FirebaseAuth.AuthStateListener
    lateinit var resultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val response = IdpResponse.fromResultIntent(it.data)
                if (it.resultCode == Activity.RESULT_OK) {
                    val user = FirebaseAuth.getInstance().currentUser
                } else {
                    Toast.makeText(
                        this,
                        "" + response!!.error!!.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuth != null && listener != null) {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    fun delaySplashScreen() {
//        Completable.timer(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//            .subscribe {
//            }
        firebaseAuth.addAuthStateListener(listener)
    }

    private fun init() {
        listener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                checkUserFromFirebase()
            } else {
                showLoginLayout()
            }
        }
    }

    private fun checkUserFromFirebase() {
        driverInfoRef.child(firebaseAuth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.progresbar.visibility = View.GONE
                        val driverInfo = snapshot.getValue(DriverInfoModel::class.java)
                        goHomeActivity(driverInfo)
                    } else {
                        showRegisterLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SplashScreen, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showRegisterLayout() {
        val dialog = RegistrationFragment()
        dialog.show(supportFragmentManager, "Registration")
    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.layout_sign_in)
            .setPhoneButtonId(R.id.btnSignInPhone)
            .setGoogleButtonId(R.id.btnSignInGoogle)
            .build()

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(authMethodPickerLayout)
            .setTheme(R.style.LoginTheme)
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(true)
            .build()
        resultLauncher.launch(intent)
    }

    override fun onClick(fistName: String, lastName: String, phoneNumber: String) {
        val driverInfo =
            DriverInfoModel(firstName = fistName, lastName = lastName, phoneNumber = phoneNumber)
        driverInfoRef.child(firebaseAuth.currentUser!!.uid).setValue(driverInfo)
            .addOnFailureListener {
                binding.progresbar.isVisible = false
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                binding.progresbar.isVisible = false
                goHomeActivity(driverInfo)
            }
    }

    private fun goHomeActivity(driverInfo: DriverInfoModel?) {
        CURRENT_USER = driverInfo
        startActivity(Intent(this, DriverHomeActivity::class.java))
        finish()
    }
}