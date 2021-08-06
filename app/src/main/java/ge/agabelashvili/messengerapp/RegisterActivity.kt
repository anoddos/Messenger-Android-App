package ge.agabelashvili.messengerapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


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
        val email = Name.text.toString()
        val password = Pass.text.toString()
        val possition = what_I_Do.text.toString()
        if( email.isEmpty() || password.isEmpty() || possition.isEmpty() ){
            Toast.makeText(this, "Please fill in forms", Toast.LENGTH_SHORT)
            return@register
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{

            }
        uploadImageToFirebase()

    }

    private fun uploadImageToFirebase(){
        if(imageUri == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                Log.d("Register", "saved image")
            }
    }


  /*  service firebase.storage {
        match /b/{bucket}/o {
            match /{allPaths=**} {
                allow read, write: if request.auth != null;
            }
        }
    }*/
}