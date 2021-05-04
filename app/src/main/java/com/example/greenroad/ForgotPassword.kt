package com.example.greenroad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        CuentaOlvidada()
        actionBar()
    }
    private fun actionBar(){
    val actionBar = supportActionBar
    actionBar!!.title = "Cuenta Olvidada"
    actionBar.setDisplayHomeAsUpEnabled(true)
    }


    private fun CuentaOlvidada(){
        buttonConfirm.setOnClickListener {
            val Correo: String = EmailForgot.text.toString().trim { it <= ' '}
            if (Correo.isEmpty()){
                Toast.makeText(this@ForgotPassword,"Por favor introducir el Email",Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(Correo)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(this@ForgotPassword,"Se ha Enviado al Email tu password ",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else{
                            Toast.makeText(this@ForgotPassword,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }



}