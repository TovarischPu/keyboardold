package com.example.keyboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.keyboard.design.BackdropDemoScaffold
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth
    private var currentUser by mutableStateOf<FirebaseUser?>(null)

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUser = auth.currentUser
                    }
                }
        } catch (e: ApiException) {
            android.util.Log.e("GoogleSignIn", "Status: ${e.statusCode}, Msg: ${e.message}")
            Toast.makeText(this, "Ошибка входа: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        signInLauncher.launch(client.signInIntent)
    }

    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut()
        auth.signOut()
        currentUser = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth.addAuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }

        setContent {
            MaterialTheme {
                AppContent(
                    user = currentUser,
                    onSignIn = ::startGoogleSignIn,
                    onSignOut = ::signOut
                )
            }
        }
    }
}

@Composable
fun AppContent(
    user: FirebaseUser?,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    BackdropDemoScaffold { backdrop ->
        Navigation1(backdrop = backdrop, user = user, onSignIn = onSignIn, onSignOut = onSignOut)
    }
}