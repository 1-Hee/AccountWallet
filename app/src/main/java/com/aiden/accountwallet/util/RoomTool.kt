package com.aiden.accountwallet.util

object RoomTool {

    inline fun <reified T> checkInstance(repository: Any):Boolean {
         return repository is T
    }
}