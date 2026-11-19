package com.example.lab8

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab8.databinding.ActivitySecBinding

class SecActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySecBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSend.setOnClickListener { submit() }
    }

    private fun submit() {
        // trim() 去掉前後空白，只輸入空白鍵也會被當成沒輸入
        val name = binding.edName.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        when {
            name.isEmpty() -> showError(binding.edName, "請輸入姓名")
            phone.isEmpty() -> showError(binding.edPhone, "請輸入電話")
            else -> {
                val intent = Intent()
                    .putExtra(EXTRA_NAME, name)
                    .putExtra(EXTRA_PHONE, phone)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    // 錯誤訊息直接顯示在輸入框旁邊，並把游標移過去
    private fun showError(editText: EditText, msg: String) {
        editText.error = msg
        editText.requestFocus()
    }

    companion object {
        // 兩個 Activity 共用同一組 key，避免字串打錯拿不到資料
        const val EXTRA_NAME = "name"
        const val EXTRA_PHONE = "phone"
    }
}
