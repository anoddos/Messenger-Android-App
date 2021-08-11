package ge.agabelashvili.messengerapp.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageModel(val id: String, val toId: String, val fromId: String, val text : String, val timeStamp : Long, val audioUrl : String) : Parcelable{
    constructor() : this("", "", "", "", -1,"")
}