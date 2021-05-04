package com.example.greenroad

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var gpsStatus: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(1800)
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setup()
        forgetPassword()
        entrarSinLogin()
    }


    private fun actionBar(){
        val actionBar = supportActionBar
        actionBar!!.title = "Bienvenidos"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }


    //btn - entrar sin registrarse
    private fun entrarSinLogin(){
        buttonSinLogIn.setOnClickListener {
            //val intent = Intent(this, Mapa::class.java)
            //startActivity(intent)
            PermisoRequestLocation()
        }
    }

    //  btn - Olvidaste la contra?
    public fun forgetPassword(){
        forgot_password.setOnClickListener {
            startActivity(Intent(this@MainActivity, ForgotPassword::class.java))
        }
    }


    private fun setup(){
        var tittle = "Autentificacion"

        RegisterBotton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() &&  PasswordeditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString()
                    ,PasswordeditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        MostrarHome()
                    }
                    else{
                        mostrarError()
                    }
                }
            }
        }

        AccessButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() &&  PasswordeditText.text.isNotEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString()
                    ,PasswordeditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        MostrarHome()
                    }
                    else{
                        mostrarError()
                    }
                }
            }
        }
    }

    //Notificacion de error para el log in erroneo
    private fun mostrarError(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("ha ocurrido un error, no se ha podido autenticar el usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Entrar cuando el log in este bien
    private fun MostrarHome(){
        val homeIntent = Intent(this, Mapa::class.java).apply {
        }
        startActivity(homeIntent)
    }

    private fun locacionPersonaPermiso() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun PermisoRequestLocation() {
        //if (!::map.isInitialized) return
        if (locacionPersonaPermiso()) {
            //si
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this,"acepta los permisos y activa la localizacion", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
            //map.isMyLocationEnabled = true
            //val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            //Log.d("FABBIANI50", ""+gpsStatus+"");
        } else {
            //no
            resquestLocationPermission()
        }
    }
    private fun resquestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Cambia en ajustes  los permisos de android", Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {when(requestCode){
        0 -> if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //Toast.makeText(this,"acepta los permisos y activa la localizacion", Toast.LENGTH_SHORT).show()
                return
            }
            //Log.d("FABBIANI88", "Ok");
            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
            //map.isMyLocationEnabled =true
        }else{
            Toast.makeText(this,"acepta los permisos y activa la localizacion", Toast.LENGTH_SHORT).show()
        }
    }}
}