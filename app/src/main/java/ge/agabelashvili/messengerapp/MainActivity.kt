package ge.agabelashvili.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import ge.agabelashvili.messengerapp.register.RegisterActivity
import ge.agabelashvili.messengerapp.register.RegisterPresenter
import ge.agabelashvili.messengerapp.signIn.ILogInView
import ge.agabelashvili.messengerapp.signIn.LogInPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.Name
import kotlinx.android.synthetic.main.activity_register.*

class MainActivity : AppCompatActivity(), ILogInView {
    private lateinit var loginPresenter : LogInPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginPresenter = LogInPresenter(this)

        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    fun signIn(view: View) {
        var nickname = Name.text.toString()
        val email = nickname + "@gmail.com"
        val password = Password.text.toString()

        loginPresenter.singInUser(email,password)
    }


    fun signUp(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun showFailReason(reason: String) {
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}