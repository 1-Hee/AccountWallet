package com.aiden.accountwallet.base.listener

interface ProgressListener {

    abstract var isInitView:Boolean

    // 다이얼로그가 준비 되었는지 리턴하는 함수
    fun getIsInitView():Boolean

    // 다이얼로그 진행율 반영 함수
    fun setDialogProgress(progress:Int)

    // 다이얼로그 상태 값 변경
    fun setDialogStatus(msg:String)

    // 종료 알림 함수
    fun notifyFinishTask(sleep:Long = 0)

}