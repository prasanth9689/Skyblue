package com.skyblue.skybluea.activity.account.add_mobile

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.skyblue.skybluea.R
import com.skyblue.skybluea.databinding.ActivityAddMobileNumberBinding
import com.skyblue.skybluea.helper.Utils
import com.skyblue.skybluea.retrofit.APIClient
import com.skyblue.skybluea.retrofit.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects
import java.util.concurrent.TimeUnit

class AddMobileNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMobileNumberBinding
    private var context: Context = this@AddMobileNumberActivity
    private val TAG = "otp_"
    private var eventName: String? = null
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mPhoneCode: String? = null
    private var mCountryName: String? = null
    private var mobileFullNo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_mobile_number)


        eventName = intent.getStringExtra("event_name")

        if (eventName != null) {
            if (eventName.equals("")  || eventName!!.isEmpty()) {
                return
            }

            binding.enterPhoneLayout.setVisibility(View.VISIBLE)
            binding.eventName.text = eventName
        }

        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)

                val code: String? = credential.getSmsCode()

                if (code  != null){
                    binding.editTextCode.setText(code)
                    verifyPhoneNumberWithCode(storedVerificationId, code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                binding.enterPhoneLayout.setVisibility(View.GONE)
                binding.otpVerifyLayout.setVisibility(View.VISIBLE)
            }
        }
        onClick()
    }

    private fun onClick() {
       binding.getOtp.setOnClickListener{
           val mMobile = binding.mobile.text.toString().trim()

           if ("" == mMobile || mMobile.isEmpty()) {
               binding.mobile.error = "Enter valid mobile no"
               binding.mobile.requestFocus()
               return@setOnClickListener
           }

           mPhoneCode = binding.ccp.selectedCountryCodeWithPlus
           mCountryName = binding.ccp.selectedCountryName
           mobileFullNo = mPhoneCode + mMobile

           binding.mobile2.text = mobileFullNo
           startPhoneNumberVerification(mobileFullNo!!)
       }

        binding.back.setOnClickListener{
            finish()
        }

        binding.edit.setOnClickListener{
            binding.otpVerifyLayout.setVisibility(View.GONE)
            binding.enterPhoneLayout.setVisibility(View.VISIBLE)

            binding.eventName.text = eventName
            binding.mobile.text = null
            binding.mobile.requestFocus()
        }

        binding.verify.setOnClickListener{
            val code = binding.editTextCode.text.toString().trim()

            if (code.isEmpty() || code.length < 6) {
                binding.editTextCode.error = resources.getString(R.string.enter_code)
                binding.editTextCode.requestFocus()
                return@setOnClickListener
            }
         //   verifyCode(code)
            verifyPhoneNumberWithCode(storedVerificationId, code)
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }

}