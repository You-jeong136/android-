package com.study.base.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.study.base.R
import com.study.base.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavController()
        initToolBar()
        initNaviDrawer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_friends -> {

            }
            R.id.menu_noti -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initNaviDrawer(){
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.tbMain,
            R.string.nav_drawer_open, R.string.nav_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initToolBar(){
        setSupportActionBar(binding.tbMain)
    }

    private fun initNavController() {
        binding.bnvMain.run{
            setOnItemSelectedListener { item->
                when(item.itemId){
                    R.id.nav_main -> {

                        true
                    }
                    R.id.nav_add -> {
                        true
                    }
                    R.id.nav_calendar -> {

                        true
                    }
                    else -> {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                }

            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_main, fragment)
            .commit()
    }
}