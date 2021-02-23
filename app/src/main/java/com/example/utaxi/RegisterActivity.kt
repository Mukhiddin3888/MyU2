package com.example.utaxi

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.utaxi.Models.DriverInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var database:FirebaseDatabase
    lateinit var driverInfoReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        database = FirebaseDatabase.getInstance()
        driverInfoReference = database.getReference("DriverInfo")

        if (FirebaseAuth.getInstance().currentUser!!.phoneNumber != null){
            txv_phone_num.text = FirebaseAuth.getInstance().currentUser!!.phoneNumber

            btn_submit.setOnClickListener {

                savetoFirebase()

            }
        }



    }

    private fun savetoFirebase() {

        val firstName = edt_name.text.toString().trim()
        val lastName = edt_lastName.text.toString().trim()
        val phone_num = txv_phone_num.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty()){
            val ref = FirebaseDatabase.getInstance().getReference("DriversInfo")
            val driverInfo = DriverInfoModel( firstName,lastName,phone_num)

            ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(driverInfo)
                    .addOnSuccessListener {
                        val intent = Intent(this.applicationContext,DriverHomeActivity::class.java)
                        startActivity(intent)
                        RegisterActivity().finishActivity(0)
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "on Failure" , Toast.LENGTH_SHORT).show()

                    }


        }else{
            Toast.makeText(this.applicationContext, "Please Enter all information" , Toast.LENGTH_SHORT).show()
        }

    }
}