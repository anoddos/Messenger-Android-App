package ge.agabelashvili.messengerapp.signIn

import android.net.Uri

interface ILogInPresenter {
    abstract fun onUserSignedIn()

    abstract fun showAppropriateToast(toast: String)

    abstract fun singInUser(name: String, password: String)
}