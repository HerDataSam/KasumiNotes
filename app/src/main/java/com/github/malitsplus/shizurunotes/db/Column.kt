package com.github.malitsplus.shizurunotes.db

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val name: String)