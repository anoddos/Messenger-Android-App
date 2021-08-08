package ge.agabelashvili.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val adapter =  GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())

        recyclerView_chat.adapter = adapter
    }
}



class ChatFromItem: Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
    }

    override fun getLayout(): Int {
        return R.layout.sent_to_me
    }

}


class ChatToItem: Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
    }

    override fun getLayout(): Int {
        return R.layout.sent_from_me
    }

}