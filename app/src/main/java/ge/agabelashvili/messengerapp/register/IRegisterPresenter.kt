package ge.agabelashvili.messengerapp.register

import android.net.Uri
import ge.agabelashvili.messengerapp.model.User

interface IRegisterPresenter {
    abstract fun onUserRegistered()

    abstract fun showAppropriateToast(toast: String)

    abstract fun registerUser(name: String, password: String, position: String, imageUri: Uri?)
}