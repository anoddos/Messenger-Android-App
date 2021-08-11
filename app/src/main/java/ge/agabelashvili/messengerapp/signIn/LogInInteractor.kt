package ge.agabelashvili.messengerapp.signIn

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.agabelashvili.messengerapp.ProfileActivity
import ge.agabelashvili.messengerapp.model.User
import ge.agabelashvili.messengerapp.register.IRegisterPresenter
import ge.agabelashvili.messengerapp.register.RegisterInteractor
import java.util.*


class LogInInteractor(val presenter: ILogInPresenter) {
    fun singInUser(name: String, password: String){
       // presenter.showAppropriateToast( "Please fill in forms")
        
        if( name.isEmpty() || password.isEmpty() ){
            presenter.showAppropriateToast( "Please fill in forms")
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(name, password)
                .addOnSuccessListener {
                    presenter.onUserSignedIn()
                }
                .addOnFailureListener{
                    presenter.showAppropriateToast("Log in failed, please try again")
                }


    }
}