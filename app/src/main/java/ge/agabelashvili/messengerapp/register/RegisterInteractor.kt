package ge.agabelashvili.messengerapp.register

import android.net.Uri
import android.os.AsyncTask
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.agabelashvili.messengerapp.model.User
import java.util.*

class RegisterInteractor(val presenter: IRegisterPresenter) {

    fun registerUser(name: String, password: String, position: String, imageUri: Uri?) {
        if( name.isEmpty() || password.isEmpty() || position.isEmpty() ){
            presenter.showAppropriateToast("Please fill in forms")
            return
        }
        if(password.length < 6){
            presenter.showAppropriateToast("\"Password length must be at least 6\"")
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(name, password)
                .addOnSuccessListener {
                    FirebaseAuth.getInstance().uid
                    uploadImageToFirebase(imageUri, position,name)
                }
                .addOnFailureListener{
                    presenter.showAppropriateToast("Could not create user")
                }
    }




    private fun uploadImageToFirebase(imageUri: Uri?, position: String, name: String){

        if(imageUri == null){
            saveUserToDb("", position, name)
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
                                saveUserToDb(it.toString(), position, name)
                            }
                }
                .addOnFailureListener{
                    presenter.showAppropriateToast("Could not upload image to storage")

                }
    }

    private fun saveUserToDb(profileImageUrl: String, position: String, name: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("/users/$uid")

        val user = User(uid, name,  profileImageUrl, position)
        myRef.setValue(user)
                .addOnSuccessListener {
                    presenter.onUserRegistered()
                }
                .addOnFailureListener{
                    presenter.showAppropriateToast("Could not save user information in database")
                }
    }


}