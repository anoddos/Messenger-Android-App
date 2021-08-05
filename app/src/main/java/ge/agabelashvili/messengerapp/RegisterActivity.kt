package ge.agabelashvili.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

    }


    fun register(view: View) {
        val email = Name.text.toString()
        val password = Pass.text.toString()
        val possition = what_I_Do.text.toString()
        if( email.isEmpty() || password.isEmpty() || possition.isEmpty() ){
            Toast.makeText(this, "Please fill in forms", Toast.LENGTH_SHORT)
            return@register
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

            }
    }
}