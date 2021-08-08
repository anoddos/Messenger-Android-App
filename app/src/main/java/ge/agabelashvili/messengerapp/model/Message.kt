package ge.agabelashvili.messengerapp.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(val id: String, val toId: String, val fromId: String, val text : String, val timeStamp : Long) : Parcelable{
    constructor() : this("", "", "", "", 0)
}