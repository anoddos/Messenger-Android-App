package ge.agabelashvili.messengerapp.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val userName: String, val profileImageUrl: String, val position : String) : Parcelable{
    constructor() : this("", "", "", "")
}