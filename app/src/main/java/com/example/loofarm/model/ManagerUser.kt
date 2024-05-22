package com.example.loofarm.model

object ManagerUser {
    private lateinit var user:User

    private var position:Int = 0

    fun createUser() {
        user = User(id = "", email = "", userName = "", farms = mutableListOf())
    }

    fun setMail(email: String){
        user.email = email
    }
    fun setName(name:String){
        user.userName = name
    }

    fun setId(id: String){
        user.id = id
    }

    fun getName(): String? {
        return user.userName
    }

    fun getId(): String? {
        return user.id
    }

    fun addFarm(farm: Farm){
        user.farms.add(farm)
    }

    fun getFarm(position: Int):Farm?{
        return user.farms[position]
    }

    fun getUser():User{
        return user
    }

    fun getFarmsSize():Int?{
        return user.farms.size
    }

    fun getFarmName(position: Int): String{
        return user.farms[position].name
    }

    fun getDeviceName(positionFarm: Int, positionDevice: Int): String? {
        return user.farms[positionFarm].devices[positionDevice].name
    }

    fun getDeviceValue(positionFarm: Int, positionDevice: Int): Int? {
        return user.farms[positionFarm].devices[positionDevice].valueDevice
    }

    fun getDate(positionFarm: Int): String? {
        return user.farms[positionFarm].startDay
    }

    fun setPosition(positionSet: Int){
        position = positionSet
    }

    fun getPosition():Int{
        return position
    }

    fun setDeviceValue(positionFarm: Int, positionDevice: Int, value: Int){
        user.farms[positionFarm].devices[positionDevice].valueDevice = value
    }
}