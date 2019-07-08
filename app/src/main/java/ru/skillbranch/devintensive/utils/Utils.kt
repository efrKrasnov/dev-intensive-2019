package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {

        val parts: List<String>? = fullName?.split(" ")

        var firstName = parsePartName(parts?.getOrNull(0))
        var lastName = parsePartName(parts?.getOrNull(1))

        return firstName to lastName
    }

    private fun parsePartName(name: String?): String? {
        val temp: String? = name?.trim()
        return when (temp?.trim()) {
            null -> null
            "" -> null
            else -> temp
        }
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var firstInitial: String? = getFirstUpcaseLetterOrNull(firstName)
        var secondInitial: String? = getFirstUpcaseLetterOrNull(lastName)

        if (firstInitial == null)
            return secondInitial

        if (secondInitial == null) {
            return firstInitial
        }

        return firstInitial + secondInitial
    }

    private fun getFirstUpcaseLetterOrNull(string: String?): String? {
        var tmp: Char? = string?.trim()?.getOrNull(0)
        if (tmp != null) {
            return tmp.toUpperCase().toString()
        }
        return tmp
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var result:String = ""
        for (char in payload)   {
            result += if(char == ' ') {
                divider
            } else    {
                toTransliterate(char)
            }
        }
        return result
    }

    private fun toTransliterate(char: Char): String? {
        val isUpCase: Boolean = char.run { isUpperCase() }

        return when (val tempChar: String? = transliterationTable[char?.toLowerCase()]) {
            null -> {
                char.toString()
            }
            else -> {
                if (isUpCase) {
                    tempChar.capitalize()
                } else {
                    tempChar
                }
            }
        }
    }

    private var transliterationTable: Map<Char?, String?> = mapOf(
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya"
    )
}