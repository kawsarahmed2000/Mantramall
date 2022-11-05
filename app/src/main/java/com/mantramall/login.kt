package com.mantramall

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mantramall.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class login : AppCompatActivity() {

    lateinit var dialog: Dialog

    lateinit var sharedPrefference: SharedPreferences
    var phnNo=""
    var vId=""
    var tkn=""
    var clickState=0
    private var mAuth: FirebaseAuth? = null
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editTextInputFocusing()

        sharedPrefference=getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            if (clickState==0) {
                if (binding.phnNumberET.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT)
                        .show()
                } else if (binding.phnNumberET.text.toString().trim().length !== 10) {
                    Toast.makeText(this, "Type valid Phone Number of 10 digit", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    clickState=1
                    otpSend()
                }
            }else if (clickState ==1){

                run {
                    binding.loadingLayout.visibility = View.VISIBLE;

                    if (binding.etC1.text.toString().trim().isEmpty() ||
                        binding.etC2.text.toString().trim().isEmpty() ||
                        binding.etC3.text.toString().trim().isEmpty() ||
                        binding.etC4.text.toString().trim().isEmpty() ||
                        binding.etC5.text.toString().trim().isEmpty() ||
                        binding.etC6.text.toString().trim().isEmpty()
                    ) {
                        Toast.makeText(applicationContext, "OTP is not Valid!", Toast.LENGTH_SHORT)
                            .show();
                    } else {
                        if (vId != null) {
                            var code:String?=null
                            code = binding.etC1.text.toString().trim() +
                                    binding.etC2.text.toString().trim() +
                                    binding.etC3.text.toString().trim() +
                                    binding.etC4.text.toString().trim() +
                                    binding.etC5.text.toString().trim() +
                                    binding.etC6.text.toString().trim();

                            val credential = PhoneAuthProvider.getCredential(
                                vId!!, code
                            )
                            FirebaseAuth
                                .getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val editor: SharedPreferences.Editor = sharedPrefference.edit()
                                        editor.putString("phnNumber", phnNo)
                                        editor.putString("authenticated", "true")
                                        editor.apply()
                                        binding.loadingLayout.visibility = View.GONE
                                        Toast.makeText(applicationContext, "Welcome...",Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(applicationContext, MainActivity::class.java)

                                        startActivity(intent)

                                    } else {
                                        binding.loadingLayout.visibility = View.GONE
                                        Toast.makeText(
                                            applicationContext,
                                            "OTP is not Valid!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                        }
                    }
                }
            }
        }
    }

    private fun otpSend() {
        binding.loadingLayout.visibility = View.VISIBLE
        val phnNumber = binding.phnNumberET.text.toString().trim()

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {
                binding.loadingLayout.visibility = View.GONE
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
                clickState=0
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.loadingLayout.visibility = View.GONE
                Toast.makeText(applicationContext, "OTP is successfully send.", Toast.LENGTH_SHORT).show()

                phnNo = binding.phnNumberET.text.toString().trim()
                vId = verificationId
                tkn = token.toString()
                binding.otpLayout.visibility=View.VISIBLE
                binding.divider.visibility=View.VISIBLE
                binding.etC1.requestFocus()
                binding.etC1.showSoftKeyboard()
            }
        }
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91" + binding.phnNumberET.text.toString().trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks as PhoneAuthProvider.OnVerificationStateChangedCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun EditText.showSoftKeyboard() {
        (this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun editTextInputFocusing() {
        binding.etC1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etC1.length()>0){
                    binding.etC2.requestFocus()
                }else{
                    binding.etC1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etC2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etC2.length()>0){
                    binding.etC3.requestFocus()
                }else{
                    binding.etC1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etC3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etC3.length()>0){
                    binding.etC4.requestFocus()
                }else{
                    binding.etC2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etC4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etC4.length()>0){
                    binding.etC5.requestFocus()
                }else{
                    binding.etC3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etC5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etC5.length()>0){
                    binding.etC6.requestFocus()
                }else{
                    binding.etC4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etC6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etC6.length()>0){
//                    binding.etC6.clearFocus()
                    binding.btnLogin.performClick()
                }else{
                    binding.etC5.requestFocus()
                }
//                hideSoftKeyboard(binding.etC6)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    protected fun hideSoftKeyboard(input: EditText) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }
    private fun dialogView(title:String,message:String,btn:String){

        dialog = Dialog(this, R.style.BottomSheetDialogTheme)
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(false)
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialoge_layout_design)
        val titleTV = dialog.findViewById<TextView>(R.id.headerTVDialog)
        val messageTV = dialog.findViewById<TextView>(R.id.messageDialog)
        val actionBtn = dialog.findViewById<TextView>(R.id.actionBtnDialog)
        titleTV.text=title.toString()
        messageTV.text=message.toString()
        actionBtn.text=btn.toString()

        actionBtn.setOnClickListener {
            dialog.hide()
        }
        dialog.show()

    }
}