package com.example.babybuy.Dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.babybuy.AppConstant
import com.example.babybuy.Fragments.HomeFragment
import com.example.babybuy.Fragments.ProfileFragment
import com.example.babybuy.Fragments.ShopFragment
import com.example.babybuy.R
import com.example.babybuy.databinding.ActivityDashboardBinding
import com.example.babybuy.model.User


class Dashboard : AppCompatActivity() {
    private val tag = "Dashboard"
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private val homeFragment= HomeFragment.newInstance()
    private val shopFragment= ShopFragment.newInstance()
    private val profileFragment= ProfileFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)

        val receivedIntent = intent
        val receivedLoginData = receivedIntent
            .getParcelableExtra<User>(AppConstant.KEY_LOGIN_DATA)

        Log.i(tag, "Received Email ::: "
            .plus(receivedLoginData?.email))
        Log.i(tag, "Received Password ::: "
            .plus(receivedLoginData?.password))
        setUpViews()
    }

    private fun setUpViews() {
        setUpFragmentContainerView()
        setUpBottomNavigationView()
    }
    private fun setUpFragmentContainerView(){
        loadFragmentInFragmentContainerView(homeFragment)
    }

    private fun setUpBottomNavigationView(){
        dashboardBinding.bnvDashboard.setOnItemSelectedListener{
            when(it.itemId){
                R.id.item_home ->{
                    loadFragmentInFragmentContainerView(homeFragment)
                    true
                }
                R.id.item_shopping ->{
                    loadFragmentInFragmentContainerView(shopFragment)
                    true
                }
                else -> {
                    loadFragmentInFragmentContainerView(profileFragment)
                    true
                }

            }
        }
    }
    private fun loadFragmentInFragmentContainerView(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(dashboardBinding.fcvDashboard.id, fragment)
            .commit()
    }
}