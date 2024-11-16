package com.todolist.apptodolist_.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.todolist.apptodolist_.databinding.ActivityLoginBinding
import com.todolist.apptodolist_.view.activity.viewmodel.LoginViewModel

class LoginActivity:AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    private lateinit var  progressDialog : ProgressDialog
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLogin.setOnClickListener{
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            progressDialog.setMessage("Ingresando...")
            progressDialog.show()

            viewModel.validarInfo(email, password)
        }
        binding.txtRegistrarse.setOnClickListener{
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }

        observerLiveData()

    }

    private  fun observerLiveData(){
        viewModel.userLoginStatus.observe(this){status ->
            if(status){
                progressDialog.dismiss()
                startActivity(Intent(this, MenuActivity::class.java))
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            }else{
                progressDialog.dismiss()
                viewModel.masajeError.value?.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.emailError.observe(this){errorMessage ->
            binding.txtEmail.error = errorMessage
            if(errorMessage.isEmpty()){
                binding.txtEmail.error = null
            }
        }

        viewModel.passwordError.observe(this){errorMessage ->
            binding.txtPassword.error = errorMessage
            if(errorMessage != null){
                binding.txtPassword.requestFocus()
            }
        }

        viewModel.masajeError.observe(this){errorMessage ->
            if(errorMessage != null){
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

}