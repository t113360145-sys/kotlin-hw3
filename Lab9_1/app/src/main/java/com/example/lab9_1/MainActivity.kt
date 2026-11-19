package com.example.lab9_1

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.lab9_1.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 先抵達終點的選手，null 代表比賽還沒結束
    private var winner: String? = null

    // 兔子有三分之二的機率會偷懶
    private val rabbitLazy = arrayOf(true, true, false)

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

        binding.btnStart.setOnClickListener { startRace() }
    }

    private fun startRace() {
        // 比賽中按鈕不可操作，並把進度歸零
        binding.btnStart.isEnabled = false
        binding.sbRabbit.progress = 0
        binding.sbTurtle.progress = 0
        winner = null

        // lifecycleScope：Activity 被關掉時，裡面的協程會自動取消，不會在背景繼續跑
        lifecycleScope.launch {
            // 用 launch() 同時啟動兩個協程，分別代表兔子和烏龜
            val rabbit = launch {
                runRace("兔子", binding.sbRabbit, step = 3) {
                    delay(100)                          // 每 0.1 秒跑一次
                    if (rabbitLazy.random()) delay(300) // 偷懶 0.3 秒
                }
            }
            val turtle = launch {
                runRace("烏龜", binding.sbTurtle, step = 1) {
                    delay(100)                          // 每 0.1 秒跑一次
                }
            }
            // 等兩個協程都結束後再公布結果
            joinAll(rabbit, turtle)
            showToast("${winner}勝利")
            binding.btnStart.isEnabled = true
        }
    }

    // 兔子和烏龜共用同一套跑步邏輯，只有步伐和休息方式不同
    private suspend fun runRace(name: String, seekBar: SeekBar, step: Int, rest: suspend () -> Unit) {
        var progress = 0
        while (winner == null) {
            rest()
            // 休息期間對手已經抵達，就不再前進
            if (winner != null) break
            progress = (progress + step).coerceAtMost(100)
            // 協程預設在 Main 執行緒執行，可以直接更新畫面，不需要 Handler
            seekBar.progress = progress
            if (progress == 100) winner = name
        }
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
