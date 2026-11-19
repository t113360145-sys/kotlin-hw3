package com.example.lab8

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myAdapter: MyAdapter

    // 處理 SecActivity 回傳的聯絡人資料
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = result.data ?: return@registerForActivityResult
        // 沒有拿到資料就不新增，避免出現空白的聯絡人
        val name = data.getStringExtra(SecActivity.EXTRA_NAME) ?: return@registerForActivityResult
        val phone = data.getStringExtra(SecActivity.EXTRA_PHONE) ?: return@registerForActivityResult
        myAdapter.add(Contact(name, phone))
        // 捲動到剛新增的聯絡人
        binding.recyclerView.scrollToPosition(myAdapter.itemCount - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        myAdapter = MyAdapter(viewModel.contacts)
        // LinearLayoutManager 預設就是垂直排列
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = myAdapter

        binding.btnAdd.setOnClickListener {
            startForResult.launch(Intent(this, SecActivity::class.java))
        }
    }
}
