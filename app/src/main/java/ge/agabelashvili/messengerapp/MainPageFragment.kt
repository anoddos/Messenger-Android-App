package ge.agabelashvili.messengerapp

import android.content.Intent
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.agabelashvili.messengerapp.model.User
import ge.agabelashvili.messengerapp.model.MessageModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.activity_new_message.new_message_recyclerView
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.message_preview.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class MainPageFragment : Fragment() {

    lateinit var adapter :  GroupAdapter<GroupieViewHolder>
    lateinit var userList : ArrayList<UserMessagePreviewItem>
    lateinit var tempUserList : ArrayList<UserMessagePreviewItem>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main_page, container, false)
        //val navBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationMenuWrap)
        userList = ArrayList<UserMessagePreviewItem>()
        tempUserList = ArrayList<UserMessagePreviewItem>()
        adapter =  GroupAdapter<GroupieViewHolder>()

        listenForLatestMessages()

        return root
    }

    private fun listenForLatestMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/latest-messages/$fromId")


        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val currentMessage = snapshot.getValue(MessageModel::class.java)
                if(currentMessage != null){
                    adapter.add(UserMessagePreviewItem(currentMessage))
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


}
class UserMessagePreviewItem(val message: MessageModel): Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
        var id : String = message.toId
        if (message.fromId != FirebaseAuth.getInstance().uid){
            id = message.fromId
        }
        viewHolder.itemView.preview_username.text = id
        viewHolder.itemView.preview_message_txt.text = message.text
        viewHolder.itemView.preview_time.text = message.timeStamp.toString()

        /*
        if(user.profileImageUrl != ""){
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_new_message)
        }

         */
    }

    override fun getLayout(): Int {
        return R.layout.message_preview
    }

}