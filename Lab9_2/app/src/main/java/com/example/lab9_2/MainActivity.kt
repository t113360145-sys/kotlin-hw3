package com.example.lab9_2

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.lab9_2.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 計算結果的資料結構
    private data class BodyResult(val standWeight: Double, val bodyFat: Double, val bmi: Double)

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

        binding.btnCalculate.setOnClickListener { onCalculateClick() }
    }

    private fun onCalculateClick() {
        // 在開始檢測前就把輸入讀出來並檢查，不在背景讀取畫面元件
        val height = readPositive(binding.edHeight, "身高") ?: return
        val weight = readPositive(binding.edWeight, "體重") ?: return
        val age = readPositive(binding.edAge, "年齡") ?: return
        val isBoy = binding.btnBoy.isChecked
        startCheck(height, weight, age, isBoy)
    }

    // 讀取輸入框的數字；沒輸入、不是數字、或小於等於 0 都回傳 null
    // 原本直接 toDouble()，只輸入「-」或「.」時 App 會閃退
    private fun readPositive(editText: EditText, label: String): Double? {
        val text = editText.text.toString().trim()
        val value = text.toDoubleOrNull()
        return when {
            text.isEmpty() -> { showToast("請輸入$label"); null }
            value == null || value <= 0 -> { showToast("請輸入正確的$label"); null }
            else -> value
        }
    }

    // 用協程模擬檢測過程
    private fun startCheck(height: Double, weight: Double, age: Double, isBoy: Boolean) {
        showResult(null)
        binding.progressBar.progress = 0
        binding.tvProgress.text = "0%"
        binding.llProgress.visibility = View.VISIBLE

        // lifecycleScope：Activity 被關掉時協程會自動取消，不會更新已經不存在的畫面
        lifecycleScope.launch {
            // 迴圈一百次，每次 delay 50ms，共約五秒
            for (progress in 1..100) {
                delay(50) // 暫停協程但不會卡住 Main 執行緒，畫面照常運作
                // 協程預設在 Main 執行緒，直接更新畫面，不需要 runOnUiThread
                binding.progressBar.progress = progress
                binding.tvProgress.text = "$progress%"
            }
            binding.llProgress.visibility = View.GONE
            showResult(calculate(height, weight, age, isBoy))
        }
    }

    // 計算公式獨立成函式，跟畫面分開，比較好閱讀與測試
    private fun calculate(height: Double, weight: Double, age: Double, isBoy: Boolean): BodyResult {
        val bmi = weight / (height / 100).pow(2)
        val standWeight = if (isBoy) (height - 80) * 0.7 else (height - 70) * 0.6
        val bodyFat = if (isBoy) 1.39 * bmi + 0.16 * age - 19.34 else 1.39 * bmi + 0.16 * age - 9
        return BodyResult(standWeight, bodyFat, bmi)
    }

    // 顯示結果；傳入 null 時顯示「無」
    private fun showResult(result: BodyResult?) {
        binding.tvWeightResult.text = "標準體重\n${result?.standWeight.format()}"
        binding.tvFatResult.text = "體脂肪\n${result?.bodyFat.format()}"
        binding.tvBmiResult.text = "BMI\n${result?.bmi.format()}"
    }

    private fun Double?.format(): String = this?.let { "%.2f".format(it) } ?: "無"

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
