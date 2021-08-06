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


    }

    fun sign_in(view: View) {
        val email = Name.text.toString()
        val password = Password.text.toString()
        if( email.isEmpty() || password.isEmpty() ){
            Toast.makeText(this, "Please fill in forms", Toast.LENGTH_SHORT)
            return@sign_in
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) {
                    Toast.makeText(this, "warum", Toast.LENGTH_SHORT)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "nnnnooo", Toast.LENGTH_SHORT)
                return@addOnFailureListener
            }

        Toast.makeText(this, "yay", Toast.LENGTH_SHORT)

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }


    fun sign_up(view: View) {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}