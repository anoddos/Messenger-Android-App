package ge.agabelashvili.messengerapp.register

import ge.agabelashvili.messengerapp.model.User

interface IRegisterView {
    fun showFailReason(reason: String)
    fun showSuccess(registeredUsed: User)
}