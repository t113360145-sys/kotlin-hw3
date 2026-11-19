package com.example.lab7

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding：取代 findViewById，元件型別在編譯時就確定
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarInsets()

        val items = loadItems()
        // 可購買數量：1個、2個……，數量與水果種類相同
        val counts = List(items.size) { getString(R.string.count_format, it + 1) }

        binding.spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, counts)
        // GridView 只顯示名稱（欄數已在 activity_main.xml 設定為 3）
        binding.gridView.adapter =
            MyAdapter(this, items, R.layout.adapter_vertical) { it.name }
        // ListView 顯示名稱與價格
        binding.listView.adapter =
            MyAdapter(this, items, R.layout.adapter_horizontal) {
                getString(R.string.item_price_format, it.name, it.price)
            }
    }

    // 把系統列的高度「加」在原本 16dp 的 padding 上，而不是把它蓋掉
    private fun applySystemBarInsets() {
        val root = binding.root
        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
            insets
        }
    }

    // 從 R.array.image_list 讀取圖片，產生水果資料
    private fun loadItems(): List<Item> {
        val array = resources.obtainTypedArray(R.array.image_list)
        try {
            return List(array.length()) { index ->
                Item(
                    photo = array.getResourceId(index, 0),
                    name = getString(R.string.fruit_name_format, index + 1),
                    price = PRICE_RANGE.random()
                )
            }
        } finally {
            // 不論中途是否出錯都會釋放資源
            array.recycle()
        }
    }

    companion object {
        private val PRICE_RANGE = 10..100
    }
}
