package ge.agabelashvili.messengerapp

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.from_me_audio.view.*
import kotlinx.android.synthetic.main.sent_from_me.view.*
import kotlinx.android.synthetic.main.sent_to_me.view.*
import kotlinx.android.synthetic.main.to_me_audio.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.media.MediaPlayer


class ChatActivity : AppCompatActivity() {

    private var mRecorder: MediaRecorder? = null
    private lateinit var mFileName: String
    private lateinit var friend: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_app_bar.setExpanded(true)


        friend = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)!!

        listenToComingMessages()

        listenToRecordButton()
    }


    fun onSendMessageClick(view: View?) {
        val txt = chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = friend.uid

        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/user-messages/$fromId/$toId").push()

        val message = MessageModel(ref.key!!, toId, fromId!!, txt, System.currentTimeMillis() / 1000, "")
        sendMessage(message)
    }



    private fun listenToChatRecycler(){
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


    private fun listenToComingMessages() {
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
        listenToChatRecycler()
        var context: Context = this

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val currentMessage = snapshot.getValue(MessageModel::class.java)
                if (currentMessage != null) {
                    val time = convertLongToTime(currentMessage.timeStamp)

                    if (currentMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatToItem(currentMessage.text, time, currentMessage.audioUrl))
                    } else {
                        adapter.add(ChatFromItem(currentMessage.text, time, currentMessage.audioUrl))
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


    private fun listenToRecordButton() {
        var mStartRecording = true
        recorder.setOnClickListener{

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) -> {

                    onRecord(mStartRecording)
                    mStartRecording = !mStartRecording
                    // You can use the API that requires the permission.
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO)
                }
            }

        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        mFileName = externalCacheDir!!.absolutePath
        mFileName += "/" + UUID.randomUUID().toString() + ".3gp"

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
        Log.e(LOG_TAG, "starting upload/$mFileName")

        val uriAudio = Uri.fromFile(File(mFileName).getAbsoluteFile())
        val ref = FirebaseStorage.getInstance().getReference("/audios").child(uriAudio.lastPathSegment!!)

        ref.putFile(uriAudio!!)
            .addOnSuccessListener {
                val f = it
                ref.downloadUrl
                    .addOnSuccessListener {
                        it.toString()
                        sendAudioAsMessage(it.toString())
                    }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Could not upload image to storage", Toast.LENGTH_SHORT).show()
            }

    }

    private fun sendMessage(message: MessageModel){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = friend.uid
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")

        val ref = database.getReference("/user-messages/$fromId/$toId").push()
        val toRef = database.getReference("/user-messages/$toId/$fromId").push()

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

    private fun sendAudioAsMessage(audioUrl: String) {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = friend.uid
        val database = Firebase.database("https://messenger-app-78b6b-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/user-messages/$fromId/$toId").push()
        val toRef = database.getReference("/user-messages/$toId/$fromId").push()

        val message = MessageModel(ref.key!!, toId, fromId!!, "", System.currentTimeMillis() / 1000, audioUrl)
        sendMessage(message)

    }

    companion object {
        private val LOG_TAG = "Record_log"
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                onRecord(true)
            } else {
                val noPermission = true
                Toast.makeText(this, "Please give application permission to use microphone", Toast.LENGTH_SHORT).show()
            }
        }

}



class ChatFromItem(val text: String, val time: String, val audioUrl : String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        var mp : MediaPlayer? = null
        var isPLAYING = false

        if(audioUrl == ""){
            viewHolder.itemView.message_to_me_txt.text = text
            viewHolder.itemView.other_message_time.text = time
        }else{
            viewHolder.itemView.other_audio_message_time.text = time

            viewHolder.itemView.buttonToMe.setOnClickListener{
                viewHolder.itemView.buttonFromMe.setOnClickListener{
                    if (!isPLAYING) {
                        isPLAYING = true
                        mp = MediaPlayer()
                        try {
                            mp!!.setDataSource(audioUrl)
                            mp!!.prepare()
                            mp!!.start()
                        } catch (e: IOException) {

                        }
                    } else {
                        isPLAYING = false
                        mp!!.release();
                        mp = null;
                    }
                }
            }
        }
    }

    override fun getLayout(): Int {
        return if(audioUrl == ""){
            R.layout.sent_to_me
        }else{
            R.layout.to_me_audio
        }

    }

}


class ChatToItem(val text: String, val time: String, val audioUrl : String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        var mp : MediaPlayer? = null
        var isPLAYING = false

        if(audioUrl == ""){
            viewHolder.itemView.message_from_me_txt.text = text
            viewHolder.itemView.my_message_time.text = time
        }else{
            viewHolder.itemView.my_audio_message_time.text = time

            viewHolder.itemView.buttonFromMe.setOnClickListener{
                if (!isPLAYING) {
                    isPLAYING = true
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(audioUrl)
                        mp!!.prepare()
                        mp!!.start()
                    } catch (e: IOException) {

                    }
                } else {
                    isPLAYING = false
                    mp!!.release();
                    mp = null;
                }

            }
        }

    }


    override fun getLayout(): Int {
        return if(audioUrl == ""){
            R.layout.sent_from_me
        }else{
            R.layout.from_me_audio
        }
    }

}