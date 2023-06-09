package com.example.breakingblock

class WorldrankUser {
    private var profile: String = ""
    private var id: String = ""
    private var pw: Int = 0
    private var userName: String = ""


    fun getProfile(): String {
        return profile
    }

    fun setProfile(profile: String) {
        this.profile = profile
    }

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getPw(): Int {
        return pw
    }

    fun setPw(pw: Int) {
        this.pw = pw
    }

    fun getUserName(): String {
        return userName
    }
}
