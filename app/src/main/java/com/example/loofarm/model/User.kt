package com.example.loofarm.model

data class User(
    var pass: String? = "",
    var userName: String? = "",
    var farms: MutableList<Farm>?=null
)
