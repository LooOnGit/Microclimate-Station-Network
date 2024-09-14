package com.example.loofarm.model

object ManagerUser {
    private lateinit var user:User

    private var position:Int = 0

    private var sensorCurrent:Int = 0

    private var link: String = ""

    fun createUser() {
        user = User(userName = "", pass = " ",farms = mutableListOf())
    }

    fun setName(name:String){
        user.userName = name
    }
    fun setPass(pass:String){
        user.pass = pass
    }

//    fun setId(id: String){
//        user.id = id
//    }

    fun getName(): String? {
        return user.userName
    }

    fun getPass(): String? {
        return user.pass
    }

//    fun getId(): String? {
//        return user.id
//    }

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

    fun getDeviceValue(positionFarm: Int, positionDevice: Int): Number {
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

    fun setSensorCurrent(position: Int){
        sensorCurrent = position
    }

    fun getSensorCurrent(): Int?{
        return sensorCurrent
    }

    fun setLink(http: String){
        link = http
    }

    fun getLink(): String{
        return link
    }

}