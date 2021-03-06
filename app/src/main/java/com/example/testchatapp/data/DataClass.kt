package com.example.testchatapp.data

data class ChatDataClass(var senderId : String? = null, var receiverId : String? = null, var message : String? = null)


data class UserDetail(
    var userId : String? = "" ,// primary key,
    var emailR : String? = "",
    var usernameR : String? = "",
    var phoneNumberR : String? = "",
    var status : String? = "",
    var designation : String? ="",
    var city : String? ="",
    var forgetPassQues : String? ="",
    var forgetPassAns: String? =""
)

data class FriendsList(
    val FriendId : String? = ""
)
data class JokesCategory(
    val value : String? = null
)