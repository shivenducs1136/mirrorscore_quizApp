package com.bitwisor.mirrorquiz.data

data class Result(
    val category: String? = null,
    val correct_answer: String? = null,
    val difficulty: String? = null,
    val incorrect_answers: List<String>? = null,
    val question: String? = null,
    val type: String? = null
)