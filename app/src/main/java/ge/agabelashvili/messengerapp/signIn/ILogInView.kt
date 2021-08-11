package ge.agabelashvili.messengerapp.signIn

interface ILogInView {
    fun showFailReason(reason: String)
    fun showSuccess()
}