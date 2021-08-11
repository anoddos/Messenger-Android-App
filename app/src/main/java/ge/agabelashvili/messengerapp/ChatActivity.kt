package ge.agabelashvili.messengerapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.agabelashvili.messengerapp.model.MessageModel
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.sent_from_me.view.*
import kotlinx.android.synthetic.main.sent_to_me.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_app_bar.setExpanded(true)


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        listenToComingMessages(user!!)

        listenToSendMessage(user!!)
    }

    private fun listnToChatRecycler(){
        recyclerView_chat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !recyclerView.canScrollVertically(1)) {
                    //chat_app_bar.setExpanded(true)
                    //chat_app_bar.layoutParams.height += 80

                    //toolbar_img.visibility = View.VISIBLE
                   // toolbar_img.animate().translationY(toolbar_img.height.toFloat());
                    toolbar_img.animate()
                            .translationY(0F)
                            .alpha(1.0f)
                            .setDuration(100)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    toolbar_img.visibility = View.VISIBLE
                                }
                            })

                } else if (dy < 0) {
                   // toolbar_img.visibility = View.GONE
                   // toolbar_img.animate().translationY(0F);
                    toolbar_img.animate()
                            .translationY(toolbar_img.height.toFloat())
                            .alpha(0.0f)
                            .setDuration(100)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    toolbar_img.visibility = View.GONE
                                }
                            })
                    //chat_app_bar.setExpanded(false)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun listenToSendMessage(friend: User) {
        send_button.setOnClickListener{

            val txt = chat_log.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val toId = friend.uid


            val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")

            val ref = database.getReference("/user-messages/$fromId/$toId").push()
            val toRef = database.getReference("/user-messages/$toId/$fromId").push()


            val message = MessageModel(ref.key!!, toId, fromId!!, txt, System.currentTimeMillis() / 1000)

            ref.setValue(message)
                .addOnFailureListener{
                    Toast.makeText(this, "Could not send message", Toast.LENGTH_SHORT).show()
                }
            toRef.setValue(message)
                .addOnFailureListener{
                    Toast.makeText(this, "Could not receive message", Toast.LENGTH_SHORT).show()
                }


            val latestMassageRef = database.getReference("/latest-messages/$fromId/$toId")
            val latestMassageRefTo = database.getReference("/latest-messages/$toId/$fromId")

            latestMassageRef.setValue(message)
                .addOnFailureListener{
                    Toast.makeText(this, "Could not update latest message", Toast.LENGTH_SHORT).show()
                }
            latestMassageRefTo.setValue(message)
                .addOnFailureListener{
                    Toast.makeText(this, "Could not update latest message", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun listenToComingMessages(friend: User) {
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
        listnToChatRecycler()
        var context: Context = this

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val currentMessage = snapshot.getValue(MessageModel::class.java)
                if (currentMessage != null) {
                    val time = convertLongToTime(currentMessage.timeStamp)

                    if (currentMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatToItem(currentMessage.text, time))
                    } else {
                        adapter.add(ChatFromItem(currentMessage.text, time))
                    }

                    newMsg.text.clear()
                    recyclerView_chat.scrollToPosition(adapter.itemCount - 1)
                    closeSoftKeyboard(context, newMsg)
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

    fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000)
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }

}



class ChatFromItem(val text: String, val time: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.message_to_me_txt.text = text
        viewHolder.itemView.other_message_time.text = time
    }

    override fun getLayout(): Int {
        return R.layout.sent_to_me
    }

}


class ChatToItem(val text: String, val time: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.message_from_me_txt.text = text
        viewHolder.itemView.my_message_time.text = time
    }

    override fun getLayout(): Int {
        return R.layout.sent_from_me
    }

}