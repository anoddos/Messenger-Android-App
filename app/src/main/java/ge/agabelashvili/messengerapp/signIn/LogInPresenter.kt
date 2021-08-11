package ge.agabelashvili.messengerapp.signIn

import ge.agabelashvili.messengerapp.register.RegisterInteractor


class LogInPresenter(var view: ILogInView) : ILogInPresenter {
    private val interactor = LogInInteractor(this)

    override fun onUserSignedIn() {
        view?.showSuccess()
    }

    override fun showAppropriateToast(toast: String) {
        view?.showFailReason(toast)
    }

    override fun singInUser(name: String, password: String) {
        interactor.singInUser(name, password)
    }
}