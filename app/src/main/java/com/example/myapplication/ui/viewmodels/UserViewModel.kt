package com.example.myapplication.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class UserData(
    val nome: String = "",
    val email: String = "",
    val turma: String = "",
    val matricula: String = "",
    val periodo: String = ""
)

class UserViewModel : ViewModel() {
    var userData by mutableStateOf<UserData?>(null)
        private set

    fun loadUserData(data: UserData) {
        userData = data
    }

    fun clearUserData() {
        userData = null
    }
}
