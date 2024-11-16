package com.todolist.apptodolist_.view.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.todolist.apptodolist_.databinding.ActivityRegisterBinding
import com.todolist.apptodolist_.view.activity.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= ViewModelProvider(this)[RegisterViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere profavor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegister.setOnClickListener{
            val usuario = binding.txtNombre.text.toString().trim()
            val dni = binding.txtDNI.text.toString().trim()
            val correo = binding.txtEmail.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()
            val cPassword = binding.txtRPassword.text.toString().trim()

            progressDialog.setMessage("Registrando Informacion...")
            progressDialog.show()

            viewModel.validarInformacion(usuario,dni, correo, password, cPassword)

        }

        observeLiveData()

    }

    private fun observeLiveData() {
        viewModel.userRegisterStatus.observe(this) { status ->
            if (status) {
                progressDialog.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                progressDialog.dismiss()
            }
        }

        viewModel.usuarioError.observe(this) { errorMessage ->
            binding.txtNombre.error = errorMessage
        }

        viewModel.dniError.observe(this) { errorMessage ->
            binding.txtDNI.error = errorMessage
        }

        viewModel.correoError.observe(this) { errorMessage ->
            binding.txtEmail.error = errorMessage
        }

        viewModel.passwordError.observe(this) { errorMessage ->
            binding.txtPassword.error = errorMessage
        }

        viewModel.cPasswordError.observe(this) { errorMessage ->
            binding.txtRPassword.error = errorMessage
        }

        viewModel.mensajeError.observe(this) { errorMessage ->
            if (errorMessage != null) {
                progressDialog.dismiss()
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}