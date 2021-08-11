package ge.agabelashvili.messengerapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.agabelashvili.messengerapp.model.MessageModel
import ge.agabelashvili.messengerapp.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.sent_from_me.view.*
import kotlinx.android.synthetic.main.sent_to_me.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private var mRecorder: MediaRecorder? = null
    private lateinit var mFileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        /*
        var toolbar = findViewById(R.id.chat_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //supportActionBar?.setDisplayShowHomeEnabled(true);

*/
        chat_app_bar.setExpanded(true)


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        listenToComingMessages(user!!)

        listenToSendMessage(user!!)

        listenToRecordButton()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun listenToRecordButton() {
        recorder.setOnClickListener{
            var mStartRecording = true

            onRecord(mStartRecording)
        }
    }

    private fun startRecording() {
        mFileName = externalCacheDir!!.absolutePath
        mFileName += "/" + UUID.randomUUID().toString() + ".3gp"
        val YOUR_REQUEST_CODE = 200

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){



        }
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
        uploadAudio()
    }

    private fun uploadAudio() {
        val uriAudio = Uri.fromFile(File(mFileName).getAbsoluteFile())

        val ref = FirebaseStorage.getInstance().getReference("/audios/$mFileName")
        FirebaseAuth.getInstance().uid
        ref.putFile(uriAudio!!)
            .addOnSuccessListener {
                FirebaseAuth.getInstance().uid
                ref.downloadUrl
                    .addOnSuccessListener {
                        it.toString()
                    }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Could not upload image to storage", Toast.LENGTH_SHORT).show()
            }

    }

    companion object {
        private val LOG_TAG = "Record_log"
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