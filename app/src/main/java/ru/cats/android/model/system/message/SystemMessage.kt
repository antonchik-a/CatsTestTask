package ru.cats.android.model.system.message

data class SystemMessage(
    val text: String,
    val type: SystemMessageType = SystemMessageType.TOAST,
    val title: String? = null
)

