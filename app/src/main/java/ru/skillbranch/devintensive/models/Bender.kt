package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extensions.isContainsOnlyChars
import ru.skillbranch.devintensive.extensions.isContainsOnlyDigits

class Bender(
    var status: Status = Status.NORMAL,
    var question: Question = Question.NAME
) {
    private var previousQuestion = Question.NAME

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun resetBender() {
        status = Status.NORMAL
        question = Question.NAME
        previousQuestion = Question.NAME
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val validatedString: String = question.isValidated(answer)
        if (validatedString == "") {
            if (question != Question.IDLE) {
                return if (question.answers.contains(answer.toLowerCase())) {
                    question = question.nextQuestion()
                    "Отлично - ты справился\n${question.question}" to status.color
                } else {
                    var tempStatus: Status = status
                    status = status.nextStatus()
                    if (tempStatus != Status.CRITICAL) {
                        "Это неправильный ответ\n${question.question}" to status.color
                    } else {
                        resetBender()
                        "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                    }
                }
            } else {
                return question.question to status.color
            }
        }
        else {
            return "$validatedString\n${question.question}" to status.color
        }
    }

    enum class Status(var color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 255, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun isValidated(string: String): String {
                return when (string.getOrNull(0)?.isUpperCase()) {
                    null, false -> "Имя должно начинаться с заглавной буквы"
                    true -> ""
                }
            }

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun isValidated(string: String): String {
                return when (string.getOrNull(0)?.isLowerCase()) {
                    null, false -> "Профессия должна начинаться со строчной буквы"
                    true -> ""
                }
            }

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun isValidated(string: String): String {
                return if (string.isContainsOnlyChars()) ""
                else "Материал не должен содержать цифр"
            }

            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun isValidated(string: String): String {
                return if (string.isContainsOnlyDigits()) ""
                else "Год моего рождения должен содержать только цифры"
            }

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun isValidated(string: String): String {
                var validateResult: String = "Серийный номер содержит только цифры, и их 7"
                if (string.length != 7) {
                    return validateResult
                } else {
                    return if (string.isContainsOnlyDigits()) ""
                    else validateResult
                }
            }

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом всё, вопросов больше нет", listOf()) {
            override fun isValidated(string: String): String = ""

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
        abstract fun isValidated(string: String): String
    }
}