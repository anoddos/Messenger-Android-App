package ge.agabelashvili.messengerapp

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

        listenToComingMessages(user!!)

        listenToSendMessage(user!!)
    }

    private fun listenToSendMessage( friend: User) {
        send_button.setOnClickListener{

            val txt = chat_log.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val toId = friend.uid


            val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")

            val ref = database.getReference("/user-messages/$fromId/$toId").push()
            val toRef = database.getReference("/user-messages/$toId/$fromId").push()


            val message = Message(ref.key!!, toId, fromId!!, txt, System.currentTimeMillis()/1000 )
            ref.setValue(message)
                .addOnSuccessListener {
                }
                .addOnFailureListener{
                }

            toRef.setValue(message)
                .addOnSuccessListener {
                }
                .addOnFailureListener{
                }


            val latestMassageRef = database.getReference("/latest-messages/$fromId/$toId")
            val latestMassageRefTo = database.getReference("/latest-messages/$toId/$fromId")

            latestMassageRef.setValue(message)
            latestMassageRefTo.setValue(message)
        }
    }

    private fun listenToComingMessages( friend: User) {
        val fromId =FirebaseAuth.getInstance().uid
        val toId = friend.uid
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/user-messages/$fromId/$toId")


        val adapter =  GroupAdapter<GroupieViewHolder>()
        var layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView_chat.layoutManager = layoutManager
        val newMsg: EditText =  findViewById(R.id.chat_log)

        recyclerView_chat.adapter = adapter
        var context: Context = this
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currentMessage = snapshot.getValue(Message::class.java)
                if(currentMessage != null){
                    Log.d("sdf", currentMessage.toString() )
                    if(currentMessage.fromId == FirebaseAuth.getInstance().uid && currentMessage.toId == friend.uid){
                        adapter.add(ChatToItem(currentMessage.text))
                    }else if(currentMessage.fromId ==  friend.uid && currentMessage.toId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(currentMessage.text))
                    }
                    newMsg.text.clear()
                    recyclerView_chat.scrollToPosition(adapter.itemCount - 1)
                    closeSoftKeyboard(context,newMsg)
                }
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

    private fun closeSoftKeyboard(context: Context, v: View) {
        val iMm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
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