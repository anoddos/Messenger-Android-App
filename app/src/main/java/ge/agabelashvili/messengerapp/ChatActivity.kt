package ge.agabelashvili.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.agabelashvili.messengerapp.model.Message
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.sent_from_me.view.*
import kotlinx.android.synthetic.main.sent_to_me.view.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)


        fetchDataFromDataBase()

        listenToMessages()

        listenToSendMessage(user!!)


    }

    private fun listenToSendMessage( user: User) {
        send_button.setOnClickListener{

            val txt = chat_log.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val toId = user.uid


            val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
            val ref = database.getReference("/messages").push()

            val message = Message(ref.key!!, toId, fromId!!, txt, System.currentTimeMillis() )
            ref.setValue(message)
                .addOnSuccessListener {

                }
                .addOnFailureListener{

                }
        }
    }

    private fun listenToMessages() {

        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/messages").push()

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun fetchDataFromDataBase() {
        val adapter =  GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem("sdfsdfasd"))
        adapter.add(ChatFromItem("dfsfdasdsadfg"))

        recyclerView_chat.adapter = adapter



    }
}



class ChatFromItem(val text : String): Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.message_to_me_txt.text = text
    }

    override fun getLayout(): Int {
        return R.layout.sent_to_me
    }

}


class ChatToItem(val text : String): Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.message_from_me_txt.text = text
    }

    override fun getLayout(): Int {
        return R.layout.sent_from_me
    }

}