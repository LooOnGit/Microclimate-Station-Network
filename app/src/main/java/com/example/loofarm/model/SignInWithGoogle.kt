package com.example.loofarm.model

import android.content.Context
import android.content.Intent
import com.example.loofarm.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

data class SignInWithGoogle(val context: Context){

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val signInIntent:Intent = googleSignInClient.signInIntent
    fun logOut(){
        googleSignInClient.signOut()
    }

}