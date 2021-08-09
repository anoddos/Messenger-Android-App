package ge.agabelashvili.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
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
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

class NewMessageActivity : AppCompatActivity() {

    lateinit var userList : ArrayList<UserItem>
    lateinit var tempUserList : ArrayList<UserItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        userList = ArrayList<UserItem>()
        tempUserList = ArrayList<UserItem>()

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
                        var userItem = UserItem(user)
                        adapter.add(userItem)
                        userList.add(userItem)
                        tempUserList.add(userItem)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_filter, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Type here to search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                tempUserList.clear()
                var searchText  = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty() && searchText.length >= 3){
                    userList.forEach{
                        if(it.user.userName.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempUserList.add(it)
                        }
                    }
                    //new_message_recyclerView.adapter?.notifyDataSetChanged()
                }else{
                    tempUserList.clear()
                    tempUserList.addAll((userList))
                    //new_message_recyclerView.adapter?.notifyDataSetChanged()

                }
                return false
            }
        })
        return false
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