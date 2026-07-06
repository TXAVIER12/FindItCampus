package com.ulk.findcampus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ulk.findcampus.databinding.ActivityOfficeInfoBinding

class OfficeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfficeInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfficeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
