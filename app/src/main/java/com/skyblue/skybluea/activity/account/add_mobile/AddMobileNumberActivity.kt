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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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
    private var mPhoneCode: String? = null
    private var mCountryName: String? = null
    private var mobileFullNo: String? = null
    private var eventName: String? = null
    private var pDialog: ProgressDialog? = null
    private var progressbar: Dialog? = null
    private var verificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    private var sendingOtpDialog: Dialog? = null
    var apiInterface: APIInterface? = null

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-retrieval or instant verification has succeeded
            sendingOtpDialog!!.dismiss()
            val code: String? = credential.getSmsCode()
            if (code != null) {
                binding.editTextCode.setText(code)
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            sendingOtpDialog!!.dismiss()
            Toast.makeText(context, "onVerificationFailed $e", Toast.LENGTH_SHORT).show()
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, "Invalid Request $e", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(
                    context,
                    "The SMS quota for the project has been exceeded $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onCodeSent(
            s: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent. Save the verification ID and the token to use later
            sendingOtpDialog!!.dismiss()
            binding.enterPhoneLayout.setVisibility(View.GONE)
            binding.otpVerifyLayout.setVisibility(View.VISIBLE)
            if (verificationId != null){
                Log.e(TAG, "success")
                verificationId = s
                settimer()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_mobile_number)
        eventName = intent.getStringExtra("event_name")
        mAuth = FirebaseAuth.getInstance()

        if (eventName != null) {
            if (eventName.equals("")  || eventName!!.isEmpty()) {
                return
            }

            binding.enterPhoneLayout.setVisibility(View.VISIBLE)
            binding.eventName.text = eventName
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

           startOTPVerification(mobileFullNo!!)
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
            verifyCode(code)
        }
    }

    private fun startOTPVerification(mobileFullNo: String) {
        binding.eventName.text = "Verify OTP"
        binding.mobile2.text = mobileFullNo
        sendVerificationCode(mobileFullNo)
        initSendingOtpDialog()
        sendingOtpDialog!!.show()
        initProgressBar()
    }

    private fun initProgressBar() {
        progressbar = Dialog(context)
        progressbar!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbar!!.setContentView(R.layout.m_loading_pls_wait)
        progressbar!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressbar!!.setCancelable(false)
    }

    private fun sendVerificationCode(mobileFullNo: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(mobileFullNo)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        displayLoader()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    pDialog!!.dismiss()
                    Toast.makeText(context, "otp verification success", Toast.LENGTH_LONG).show()
                    checkUser()
                } else {
                    pDialog!!.dismiss()
                    Toast.makeText(
                        context,
                        task.exception.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun checkUser() {
        progressbar!!.show()

        val apiInterface = APIClient.getClient().create(APIInterface::class.java)
        val call = apiInterface.check_user(mobileFullNo)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                progressbar!!.dismiss()
                if (response.isSuccessful) {
                    if ("1" == response.body()) {
                        saveNumber()
                       Toast.makeText(context, "New user", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Already registered this number try a new number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Utils.showMessageInSnackbar(context, resources.getString(R.string.server_error))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, t.toString())
                progressbar!!.dismiss()
            }
        })
    }

    private fun saveNumber() {

    }

    private fun displayLoader() {
        pDialog = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        pDialog!!.setMessage(resources.getString(R.string.checking_otp_please_wait))
        pDialog!!.setIndeterminate(false)
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    private fun initSendingOtpDialog() {
        sendingOtpDialog = Dialog(context)
        sendingOtpDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sendingOtpDialog!!.setContentView(R.layout.m_send_otp_wait)
        Objects.requireNonNull(sendingOtpDialog!!.getWindow())
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sendingOtpDialog!!.setCancelable(false)
    }

    private fun settimer() {
        binding.reSend.setVisibility(View.GONE)
        binding.textTimer.setVisibility(View.VISIBLE)

        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val remainedSecs = millisUntilFinished / 1000
                binding.textTimer.text = "" + remainedSecs / 60 + ":" + remainedSecs % 60
            }

            override fun onFinish() {
                binding.reSend.setVisibility(View.VISIBLE)
                binding.textTimer.setVisibility(View.GONE)
            }
        }.start()
    }
}