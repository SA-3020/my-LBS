package com.example.notify_around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_get_phone_no.*

class GetPhoneNoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_phone_no)

        ccp.registerCarrierNumberEditText(et_phone_no)

        btn_next.setOnClickListener {
            val intent = Intent(applicationContext, OtpAuthActivity::class.java)
            intent.putExtra("mobileno", ccp.fullNumberWithPlus.replace(" ", ""))
            startActivity(intent)
        }

        btn_continue_fb.setOnClickListener {
//            val m_intent = Intent(applicationContext, FacebookSignUpActivity::class.java)
//            startActivity(m_intent)
        }
    }
}