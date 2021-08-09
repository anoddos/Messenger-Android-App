package ge.agabelashvili.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        fetchUsers()
    }


    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val adapter =  GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user!= null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{ item, view ->
                    val curUser = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra(USER_KEY, curUser.user)
                    startActivity(intent)
                    finish()
                }
                new_message_recyclerView.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }

        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind (viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.userName.text = user.userName
        viewHolder.itemView.new_message_position.text = user.position
        if(user.profileImageUrl != ""){
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_new_message)
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}