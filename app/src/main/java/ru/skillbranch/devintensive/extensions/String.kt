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