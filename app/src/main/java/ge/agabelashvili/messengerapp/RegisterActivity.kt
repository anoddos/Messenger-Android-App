package ge.agabelashvili.messengerapp

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
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.Name
import kotlinx.android.synthetic.main.activity_register.ProfilePicture
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

        var nickname = Name.text.toString()
        val email = nickname + "@gmail.com"
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

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                FirebaseAuth.getInstance().uid
                uploadImageToFirebase()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Could not create user", Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImageToFirebase(){
        if(imageUri == null){
            saveUserToDb("")
            return
        }

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        FirebaseAuth.getInstance().uid
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                FirebaseAuth.getInstance().uid
                ref.downloadUrl
                    .addOnSuccessListener {
                    it.toString()
                    saveUserToDb(it.toString())
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Could not upload image to storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToDb(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("/users/$uid")

        val position =  what_I_Do.text.toString() ?: ""
        val name =  Name.text.toString()
        val user = User(uid, name,  profileImageUrl, position)

        myRef.setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Could not save user information in database", Toast.LENGTH_SHORT).show()
            }

    }

}
