package com.example.taskmovie.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taskmovie.R
import com.example.taskmovie.SearchActivityview
import com.example.taskmovie.databinding.ActivityMainBinding
import com.example.taskmovie.ui.popular.FragmentPoup
import com.example.taskmovie.ui.toprated.FragmentTopRated
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter
     lateinit var FireBae : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Popular"
                1 -> tab.text = "Top Rated"
                else -> {
                    tab.view
                }


            }


        }.attach()
        openSearchActivity()



    }

    fun openSearchActivity(){
        binding.searchicon.setOnClickListener({
            val intent = Intent(this, SearchActivityview::class.java)
            startActivity(intent)
        })

    }



    class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            Log.d("ViewPagerAdapter", "getItemCount called")
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FragmentPoup()
                1 -> FragmentTopRated()
                else -> FragmentPoup()
            }
        }
    }


}
