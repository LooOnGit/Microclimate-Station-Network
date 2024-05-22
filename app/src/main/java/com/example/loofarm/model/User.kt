package com.example.loofarm.model

data class User(
    var id: String? = "",
    var email: String? = "",
    var userName: String? = "",
    var farms: MutableList<Farm>
)