package com.study.aos

enum class NotificationStyle(val title : String, val id : Int) {
    NORMAL("일반 알림", 0),
    BIG_TEXT("확장형 알림_글자", 1),
    INBOX("인박스 알림", 3),
    CUSTOM("커스텀 알림", 4)

}