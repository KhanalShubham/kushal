package com.example.babybuy.repository

import android.util.Log
import com.example.babybuy.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class UserRespositoryImpl(private val auth: FirebaseAuth) : UserRespository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun createUser(
        email: String,
        password: String,
        name: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newUser = UserModel(name, email)

                    user?.let {
                        db.collection("users").document(it.uid).set(newUser)
                            .addOnSuccessListener {
                                callback(true, "User created successfully")
                            }
                            .addOnFailureListener { exception ->
                                callback(false, "Firestore error: ${exception.localizedMessage}")
                            }
                    } ?: run {
                        callback(false, "Error: User is null")
                    }

                } else {
                    callback(false, "Authentication failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, "Authentication error: ${exception.localizedMessage}")
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, "Login failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, "Login error: ${exception.localizedMessage}")
            }
    }

    override fun signInwithGoogle(idToken: String, callback: (Boolean, String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Google sign-in successful")
                } else {
                    callback(false, "Google sign-in failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, "Google sign-in error: ${exception.localizedMessage}")
            }
    }

    override fun signOut() {
        auth.signOut()
    }
}
