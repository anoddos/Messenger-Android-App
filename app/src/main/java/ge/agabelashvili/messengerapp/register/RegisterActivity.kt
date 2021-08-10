package ge.agabelashvili.messengerapp.register

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ge.agabelashvili.messengerapp.R
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {


    lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var presenter: RegisterPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_register)

        listenToImageClick()
        presenter = RegisterPresenter(this)



    }

    private fun listenToImageClick() {
        imageView = ProfilePicture as ImageView
        imageView.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }


    fun register(view: View) {
        val email = Name.text.toString() + "@gmail.com"
        val password = Pass.text.toString()
        val position = what_I_Do.text.toString()
        if( email.isEmpty() || password.isEmpty() || position.isEmpty() ){
            Toast.makeText(this, "Please fill in forms", Toast.LENGTH_SHORT).show()
            return@register
        }
        if(password.length < 6){
            Toast.makeText(this, "Password length must be at least 6", Toast.LENGTH_SHORT).show()
            return@register
        }

        presenter.registerUser(email, password, position, imageUri)


    }

    fun showSuccess(user: User) {

    }


}