package com.example.scanbot

import android.app.Activity
import com.example.scanbot.usecases.*
import kotlin.reflect.KClass

enum class UseCase(val activityClass: KClass<out Activity>) {
    DETECTION_ON_THE_IMAGE(DetectionOnTheImageActivity::class),
    AR_FIND_AND_PICK(AR_FindAndPickActivity::class),
}


sealed class ViewType(val type: Int) {
    class Header(val title: String) : ViewType(0)
    class Option(val useCase: UseCase, val title: String) : ViewType(1)
    class Support : ViewType(2)
}