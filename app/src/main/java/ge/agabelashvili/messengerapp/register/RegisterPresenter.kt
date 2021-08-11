package ge.agabelashvili.messengerapp.register

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.agabelashvili.messengerapp.ProfileActivity
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterPresenter(var view: IRegisterView) : IRegisterPresenter{

    private val interactor = RegisterInteractor(this)


    override fun onUserRegistered() {
        view?.showSuccess()
    }

    override fun showAppropriateToast(toast: String){
        view?.showFailReason(toast)
    }

    override fun registerUser(name: String, password: String, position: String, imageUri: Uri?) {
        interactor.registerUser(name, password, position, imageUri)
    }






}