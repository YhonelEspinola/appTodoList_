package com.todolist.apptodolist_.view.activity.viewmodel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel:ViewModel() {

    private  lateinit var  firebaseAuth: FirebaseAuth

    val userRegisterStatus = MutableLiveData<Boolean>()
    val usuarioError = MutableLiveData<String>()
    val dniError = MutableLiveData<String>()
    val correoError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val cPasswordError = MutableLiveData<String>()
    val mensajeError = MutableLiveData<String>()

    fun validarInformacion(usuario:String,dni:String,correo: String,password: String, cPassword:String) {
        if (usuario.isEmpty()){
            usuarioError.value = "Ingrese un Usuario"
            userRegisterStatus.value = false
        }else if (dni.isEmpty()){
            dniError.value = "Ingrese DNI"
            userRegisterStatus.value = false
        }else if (dni.length != 8 || !dni.all { it.isDigit() }){
            dniError.value = "DNI Incorrecto, verifique por favor!"
            userRegisterStatus.value = false
        }else if (correo.isEmpty()){
            correoError.value = "Ingrese su Correo"
            userRegisterStatus.value = false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            correoError.value = "Correo no valido"
            userRegisterStatus.value = false
        }else if (password.isEmpty()){
            passwordError.value = "Ingrese una contraseña"
            userRegisterStatus.value = false
        }else if (password.length < 6){
            passwordError.value = "Contraseña muy corta, ingrese 6 o mas caracteres"
            userRegisterStatus.value = false
        }else if (!contraseñaSegura(password)) {
            passwordError.value = "La contraseña debe contener al menos una mayúscula, un número y un carácter especial"
            userRegisterStatus.value = false
        }else if (cPassword.isEmpty()){
            cPasswordError.value = "Repita la Contraseña"
            userRegisterStatus.value = false
        }else if (password != cPassword){
            cPasswordError.value = "Contraseña no coincide"
            userRegisterStatus.value = false
        }else{
            registrarUsuario(usuario,dni,correo,password)
        }
    }

    private fun contraseñaSegura(password: String): Boolean {
        val mayuscula = password.any { it.isUpperCase() }
        val numero = password.any { it.isDigit() }
        val caracterEspecial = password.any { "!@#$%^&*()_+-=[]|,./?><".contains(it) }

        return mayuscula && numero && caracterEspecial
    }

    private fun sugerenciaNombre(usuario: String): String {
        val randomNumber = (1000..9999).random()
        return "$usuario$randomNumber"
    }

    private fun registrarUsuario(usuario: String,dni: String,correo: String, password: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").whereEqualTo("nombre",usuario)
            .get().addOnSuccessListener { userdocuments ->
                if (!userdocuments.isEmpty){
                    val sugerenciaUsuario = sugerenciaNombre(usuario)
                    usuarioError.value = "El nombre de usuario '$usuario' no esta disponible. ¿Qué tal '$sugerenciaUsuario'?"
                    userRegisterStatus.value = false
                }else {
                    db.collection("usuarios").whereEqualTo("dni",dni)
                        .get().addOnSuccessListener { dnidocuments ->
                            if (!dnidocuments.isEmpty){
                                dniError.value = "El DNI ingresado ya se encuentra registrado."
                                userRegisterStatus.value = false
                            } else{
                                firebaseAuth = FirebaseAuth.getInstance()
                                firebaseAuth.createUserWithEmailAndPassword(correo,password)
                                    .addOnCompleteListener {task ->
                                        if (task.isSuccessful){
                                            insertarInfoBD(usuario,dni,correo)
                                        }else{
                                            correoError.value = "Email ya se encuentra registrado."
                                            userRegisterStatus.value = false
                                        }
                                    }
                            }
                        }
                }
            }

    }

    private fun insertarInfoBD(usuario: String,dni: String, correo: String) {

        val db = FirebaseFirestore.getInstance()

        val uidBD = firebaseAuth.uid

        val datosAdministrador = hashMapOf(
            "uid" to uidBD,
            "nombre" to usuario,
            "dni" to dni,
            "correo" to correo,
        )

        if (uidBD != null) {
            db.collection("usuarios").document(uidBD).set(datosAdministrador)
                .addOnSuccessListener {
                    userRegisterStatus.value = true
                }
                .addOnFailureListener { e ->
                    userRegisterStatus.value = false
                    mensajeError.value = "Fallo el registro en la Base de Datos debido a ${e.message}"
                }
        } else {
            userRegisterStatus.value = false
            mensajeError.value = "Error al obtener UID del usuario."
        }
    }

}