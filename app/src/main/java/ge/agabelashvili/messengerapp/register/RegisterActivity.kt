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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.agabelashvili.messengerapp.ProfileActivity
import ge.agabelashvili.messengerapp.R
import ge.agabelashvili.messengerapp.model.User
import ge.agabelashvili.messengerapp.register.IRegisterPresenter
import ge.agabelashvili.messengerapp.register.IRegisterView
import ge.agabelashvili.messengerapp.register.RegisterPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.Name
import kotlinx.android.synthetic.main.activity_register.ProfilePicture
import java.util.*

class RegisterActivity : AppCompatActivity(), IRegisterView {

    lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var registerPresenter : RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerPresenter = RegisterPresenter(this)

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
        val nickname = Name.text.toString()
        val password = Pass.text.toString()
        val position = what_I_Do.text.toString()
        registerPresenter.registerUser(nickname,password,position,imageUri)
    }


    override fun showFailReason(reason: String) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
