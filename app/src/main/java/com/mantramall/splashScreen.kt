package com.mantramall

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.mantramall.databinding.ActivitySplashScreenBinding
import java.util.*
import kotlin.concurrent.timerTask

class splashScreen : AppCompatActivity() {

    lateinit var dialog: Dialog

    private lateinit var binding: ActivitySplashScreenBinding
    lateinit var sharedPrefference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefference = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)


        startsecondactivity()


    }

    private fun dialogView(title: String, message: String, btn: String) {

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
        titleTV.text = title.toString()
        messageTV.text = message.toString()
        actionBtn.text = btn.toString()

        actionBtn.setOnClickListener {

            val intent = Intent(applicationContext, login::class.java)
            startActivity(intent)
            finish()
            dialog.hide()
        }
        dialog.show()

    }

    fun startsecondactivity() {
        if (!isDestroyed) {

            val tmtask = timerTask {
                if (!isDestroyed) {

                    val authenticated = sharedPrefference.getString("authenticated", "false")

                    if (authenticated == "true") {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        dialogView(
                            "Terms and conditions",
                            "This game involve an element of finalcial risk and may be addictive please play responsibly and at your own risk",
                            "Good"
                        )

                    }
                }
            }
            val timer = Timer()
            timer.schedule(tmtask, 1800)

        }
    }
}