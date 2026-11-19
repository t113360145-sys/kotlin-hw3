package com.example.lab8

import androidx.lifecycle.ViewModel

// 聯絡人資料放在 ViewModel，旋轉螢幕時 Activity 重建，資料也不會消失
class ContactViewModel : ViewModel() {
    val contacts = mutableListOf<Contact>()
}
