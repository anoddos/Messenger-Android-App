package ge.agabelashvili.messengerapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {


    lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var root : View
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_settings, container, false)

        showDataFromFirebase()

        listenToUpdateButto()

        listenToLogoutButton()

        imageView = root.findViewById(R.id.ProfilePicture)
        imageView.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        return root
    }

    private fun listenToLogoutButton() {
        val signOutButton : Button = root.findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }
    }

    private fun listenToUpdateButto() {
        val updateOutButton : Button = root.findViewById(R.id.update_button)

        updateOutButton.setOnClickListener{
            uploadImageToFirebase()

            val userName =  root.findViewById<TextView>(R.id.userName).text.toString()
            val position =  root.findViewById<TextView>(R.id.position).text.toString()

            val uid = FirebaseAuth.getInstance().uid
            val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
            database.getReference("/users/$uid/position").setValue(position)
                .addOnSuccessListener {
                    //TODO print
                }
                .addOnFailureListener{
                    //TODO print

                }
            database.getReference("/users/$uid/userName").setValue(userName)
                .addOnSuccessListener {
                    //TODO print

                }
                .addOnFailureListener{
                    //TODO print

                }
        }
    }

    private fun showDataFromFirebase() {
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")

        val uid = FirebaseAuth.getInstance().uid ?: ""
        ref.child(uid).get().addOnSuccessListener {
            val pic = it.child("profileImageUrl").value.toString()
            if(pic != ""){
                Picasso.get().load(pic).into( root.findViewById<ImageView>(R.id.ProfilePicture))
            }
            val name = it.child("userName").value.toString()
            root.findViewById<TextView>(R.id.userName).text = name

            val position = it.child("position").value.toString()
            root.findViewById<TextView>(R.id.position).text = position

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data")
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }


    private fun uploadImageToFirebase(){
        if(imageUri == null)return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        val uid = FirebaseAuth.getInstance().uid
                        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
                        database.getReference("/users/$uid/profileImageUrl").setValue(it.toString())
                        Log.d("uploadImage", it.toString())
                    }
                    .addOnFailureListener{
                        Log.d("uploadImage", it.toString())
                    }
            }
            .addOnFailureListener{
                Log.d("uploadImage", it.toString())
            }
    }
}