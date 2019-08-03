package ru.skillbranch.devintensive.extensions

fun String.truncate(truncateLength: Int = 16): String {
    val trimmedString: String = this.trim()
    if (trimmedString.length <= truncateLength) {
        return trimmedString
    }

    val tempString: String = trimmedString.substring(0 until truncateLength).trim()
    return "$tempString..."
}

fun String.stripHtml(): String {
    var noTagString = Regex("\\<.*?\\>").replace(this, "")
    var noSpaceString =Regex("\\s+").replace(noTagString, " ")
    return Regex("&\\\\w+;|&#[0-9]+;|&#[xX][a-fA-F0-9]+;").replace(noSpaceString, "")
}

fun String.isContainsOnlyDigits():Boolean    {
    for(char in this) {
        if (!char.isDigit()) {
            return false
        }
    }
    return true
}
fun String.isContainsOnlyChars():Boolean    {
    for(char in this) {
        if (char.isDigit()) {
            return false
        }
    }
    return true
}

fun String.isValidRepository(): Boolean {
    if(this=="") {
        return true
    }
    var strArr = this.split("/").toMutableList()
    if (strArr[0] != "https:") {
        strArr.add(0, "https:")
        strArr.add(1, "")
    }
    if (strArr.size == 4) {
        return if (strArr[0] == "https:") {
            if (strArr[1] == "") {
                if (strArr[2] == "github.com" || strArr[2] == "www.github.com") {
                    strArr[3] !in repositoryExceptions
                } else {
                    false
                }
            } else {
                true
            }
        } else {
            false
        }
    }
    else    {
        return false
    }
}

private var repositoryExceptions: List<String> = listOf(
    "enterprise",
    "features",
    "topics",
    "collections",
    "trending",
    "events",
    "marketplace",
    "pricing",
    "nonprofit",
    "customer-stories",
    "security",
    "login",
    "join",
    ""
)