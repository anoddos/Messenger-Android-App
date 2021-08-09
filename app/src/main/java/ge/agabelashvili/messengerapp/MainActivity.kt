package ge.agabelashvili.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    fun signIn(view: View) {
        val email = Name.text.toString()
        val password = Password.text.toString()
        if( email.isEmpty() || password.isEmpty() ){
            Toast.makeText(this, "Please fill in forms", Toast.LENGTH_SHORT).show()
            return@signIn
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Log in failed, please try again", Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }
    }


    fun signUp(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}