package ge.agabelashvili.messengerapp.model


data class User(val uid: String, val userName: String, val profileImageUrl: String, val position : String){
    constructor() : this("", "", "", "")
}