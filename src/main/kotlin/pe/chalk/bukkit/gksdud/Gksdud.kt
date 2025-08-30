package pe.chalk.bukkit.gksdud

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.EventPriority
import org.bukkit.plugin.java.JavaPlugin

val INITIAL_JAMOS = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
val MEDIAL_JAMOS = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"
val FINAL_JAMOS = " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ"
val ALPHABETS = "QqWwEeRrTtYyUuIiOoPpAaSsDdFfGgHhJjKkLlZzXxCcVvBbNnMm"
val HANGEUL_JAMOS = "ㅃㅂㅉㅈㄸㄷㄲㄱㅆㅅㅛㅛㅕㅕㅑㅑㅒㅐㅖㅔㅁㅁㄴㄴㅇㅇㄹㄹㅎㅎㅗㅗㅓㅓㅏㅏㅣㅣㅋㅋㅌㅌㅊㅊㅍㅍㅠㅠㅜㅜㅡㅡ"
val HANGEUL_SYLLABLE = "([ㄱㄲㄴㄷ-ㄹㅁ-ㅃㅅ-ㅎ])([ㅏ-ㅖㅛㅠㅣ]|ㅗ[ㅏㅐㅣ]?|ㅜ[ㅓㅔㅣ]?|ㅡㅣ?)(?:([ㄲㄷㅁㅅ-ㅈㅊ-ㅎ]|ㄱㅅ?|ㄴ[ㅈㅎ]?|ㄹ[ㄱㅁㅂㅅㅌ-ㅎ]?|ㅂㅅ?)(?![ㅏ-ㅣ]))?".toRegex()
val COMPLEX_JAMOS = mapOf(
    "ㅗㅏ" to "ㅘ", "ㅗㅐ" to "ㅙ", "ㅗㅣ" to "ㅚ", "ㅜㅓ" to "ㅝ", "ㅜㅔ" to "ㅞ", "ㅜㅣ" to "ㅟ", "ㅡㅣ" to "ㅢ",
    "ㄱㅅ" to "ㄳ", "ㄴㅈ" to "ㄵ", "ㄴㅎ" to "ㄶ", "ㄹㄱ" to "ㄺ", "ㄹㅁ" to "ㄻ", "ㄹㅂ" to "ㄼ", "ㄹㅅ" to "ㄽ", "ㄹㅌ" to "ㄾ", "ㄹㅍ" to "ㄿ", "ㄹㅎ" to "ㅀ", "ㅂㅅ" to "ㅄ",
)
val CHECKS = mapOf('가'..'힣' to Int.MAX_VALUE, 'ㄱ'..'ㅎ' to 2, 'ㅏ'..'ㅣ' to 2)

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2017-01-20 15:26
 */
class Gksdud : JavaPlugin(), Listener {
    companion object {
        fun isAlphabet(str: Char) = ALPHABETS.contains(str)
        fun toHangeul(str: Char) = HANGEUL_JAMOS[ALPHABETS.indexOf(str)]
        fun replaceAlphabets(str: String) = str.map { if (isAlphabet(it)) toHangeul(it) else it }.joinToString("")
        fun stack(i: String, m: String, f: String = " ") = (44032 + FINAL_JAMOS.indexOf(f) + MEDIAL_JAMOS.indexOf(m) * 28 + INITIAL_JAMOS.indexOf(i) * 588).toChar().toString()
        fun gksdud(str: String) = HANGEUL_SYLLABLE.replace(replaceAlphabets(str), { it.destructured.let { (i, m, f) -> stack(i, COMPLEX_JAMOS[m] ?: m, COMPLEX_JAMOS[f] ?: f) } })
        fun check(str: String) = CHECKS.any { (range, count) -> str.filter { it in range }.let { it.length / str.length.toDouble() >= 0.75 && it.toList().distinct().size <= count } }
    }

    override fun onEnable() = server.pluginManager.registerEvents(this, this)

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteract(event: AsyncPlayerChatEvent) {
        if (event.isCancelled) return
        event.message = when {
            event.message.contains(",") -> event.message.split(",").mapIndexed { i, part -> if (i % 2 == 1) gksdud(part) else part }.joinToString("")
            else -> event.message.split(" ").map { gksdud(it).takeIf { check(it.filter { c -> CHECKS.any { c in it.key } }) } ?: it }.joinToString(" ")
        }
    }
}
