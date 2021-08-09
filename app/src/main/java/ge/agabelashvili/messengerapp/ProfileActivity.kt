package ge.agabelashvili.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.navigation.*

class ProfileActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation)


        val mainPageFragment = MainPageFragment()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(mainPageFragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationMenuWrap)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isEnabled = false

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(mainPageFragment)
                R.id.settings->setCurrentFragment(settingsFragment)
            }
            true
        }

        floatingAddButton.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

    }


    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.currentFragment,fragment)
            commit() }


}