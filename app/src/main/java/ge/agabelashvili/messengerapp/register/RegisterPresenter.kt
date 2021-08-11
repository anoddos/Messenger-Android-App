package ge.agabelashvili.messengerapp.register

import android.net.Uri
import ge.agabelashvili.messengerapp.model.User

class RegisterPresenter(var view: RegisterActivity) : IRegisterPresenter{

    private val interactor = RegisterInteractor(this)


    override fun onUserRegistered(user: User) {
        view?.showSuccess(user)
    }

    override fun registerUser(name: String, password: String, position: String, imageUri: Uri?) {
        TODO("Not yet implemented")
    }


}