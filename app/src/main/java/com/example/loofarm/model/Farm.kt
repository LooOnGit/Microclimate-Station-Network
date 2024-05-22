package com.example.loofarm.model

import java.util.Date

data class Farm(var name:String, var devices: MutableList<Device>, var startDay:String)