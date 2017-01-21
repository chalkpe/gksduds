package pe.chalk.bukkit.gksdud;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2017-01-20 15:26
 */
public class Gksdud extends JavaPlugin implements Listener {
    private final static String INITIAL_JAMOS = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
    private final static String MEDIAL_JAMOS = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
    private final static String FINAL_JAMOS = " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
    private final static String ALPHABETS = "QqWwEeRrTtYyUuIiOoPpAaSsDdFfGgHhJjKkLlZzXxCcVvBbNnMm";
    private final static String HANGEUL_JAMOS = "ㅃㅂㅉㅈㄸㄷㄲㄱㅆㅅㅛㅛㅕㅕㅑㅑㅒㅐㅖㅔㅁㅁㄴㄴㅇㅇㄹㄹㅎㅎㅗㅗㅓㅓㅏㅏㅣㅣㅋㅋㅌㅌㅊㅊㅍㅍㅠㅠㅜㅜㅡㅡ";

    private final static Pattern HANGEUL_SYLLABLE = Pattern.compile("([ㄲㄷㅁㅅㅆㅇㅈㅊㅋㅌㅍㅎ]|ㅅ?ㄱ|[ㅈㅎ]?ㄴ|[ㄱㅁㅂㅅㅌㅍㅎ]?ㄹ|ㅅ?ㅂ)?([ㅏㅐㅑㅒㅓㅔㅕㅖㅛㅠㅣ]|[ㅏㅐㅣ]?ㅗ|[ㅓㅔㅣ]?ㅜ|ㅣ?ㅡ)([ㄱ-ㅎ])");
    private final static Map<String, String> COMPLEX_MEDIAL_JAMOS = new HashMap<String, String>(){{ put("ㅗㅏ", "ㅘ"); put("ㅗㅐ", "ㅙ"); put("ㅗㅣ", "ㅚ"); put("ㅜㅓ", "ㅝ"); put("ㅜㅔ", "ㅞ"); put("ㅜㅣ", "ㅟ"); put("ㅡㅣ", "ㅢ"); }};
    private final static Map<String, String> COMPLEX_FINAL_JAMOS = new HashMap<String, String>(){{ put("ㄱㅅ", "ㄳ"); put("ㄴㅈ", "ㄵ"); put("ㄴㅎ", "ㄶ"); put("ㄹㄱ", "ㄺ"); put("ㄹㅁ", "ㄻ"); put("ㄹㅂ", "ㄼ"); put("ㄹㅅ", "ㄽ"); put("ㄹㅌ", "ㄾ"); put("ㄹㅍ", "ㄿ"); put("ㄹㅎ", "ㅀ"); put("ㅂㅅ", "ㅄ"); }};

    private static String reverse(String str){
        return Objects.isNull(str) ? " " : new StringBuilder(str).reverse().toString();
    }

    private static boolean isAlphabet(int str){
        return ALPHABETS.indexOf(str) >= 0;
    }

    private static char toHangeulJamo(int str){
        return HANGEUL_JAMOS.charAt(ALPHABETS.indexOf(str));
    }

    private static String replaceAlphabets(String str){
        return str.chars().mapToObj(c -> String.valueOf((char) (isAlphabet(c) ? toHangeulJamo(c) : c))).collect(Collectors.joining());
    }

    private static String stackHangeulJamos(String initialJamo, String medialJamo, String finalJamo){
        return new String(Character.toChars(44032 + FINAL_JAMOS.indexOf(finalJamo) + MEDIAL_JAMOS.indexOf(medialJamo) * 28 + INITIAL_JAMOS.indexOf(initialJamo) * 588));
    }

    private static String gksdud(String str){
        final StringBuffer buffer = new StringBuffer();
        final Matcher matcher = HANGEUL_SYLLABLE.matcher(reverse(replaceAlphabets(str)));

        while(matcher.find()){
            final String finalJamo = reverse(matcher.group(1)), medialJamo = reverse(matcher.group(2)), initialJamo = reverse(matcher.group(3));
            matcher.appendReplacement(buffer, stackHangeulJamos(initialJamo, COMPLEX_MEDIAL_JAMOS.getOrDefault(medialJamo, medialJamo), COMPLEX_FINAL_JAMOS.getOrDefault(finalJamo, finalJamo)));
        }

        matcher.appendTail(buffer);
        return reverse(buffer.toString());
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerInteract(AsyncPlayerChatEvent event){
        if(event.isCancelled()) return;

        final String message = event.getMessage();
        if(message.startsWith(",")) event.setMessage(gksdud(message.substring(1)) + ChatColor.DARK_GRAY + " (번역됨)");
    }
}
