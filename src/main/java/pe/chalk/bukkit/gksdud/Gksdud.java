package pe.chalk.bukkit.gksdud;

import org.bstats.bukkit.Metrics;
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

    private final static Pattern HANGEUL_SYLLABLE = Pattern.compile("([ㄱㄲㄴㄷ-ㄹㅁ-ㅃㅅ-ㅎ])([ㅏ-ㅖㅛㅠㅣ]|ㅗ[ㅏㅐㅣ]?|ㅜ[ㅓㅔㅣ]?|ㅡㅣ?)(?:([ㄲㄷㅁㅅ-ㅈㅊ-ㅎ]|ㄱㅅ?|ㄴ[ㅈㅎ]?|ㄹ[ㄱㅁㅂㅅㅌ-ㅎ]?|ㅂㅅ?)(?![ㅏ-ㅣ]))?");
    private final static Map<String, String> COMPLEX_JAMOS = new HashMap<String, String>(){{
        put("ㅗㅏ", "ㅘ"); put("ㅗㅐ", "ㅙ"); put("ㅗㅣ", "ㅚ"); put("ㅜㅓ", "ㅝ"); put("ㅜㅔ", "ㅞ"); put("ㅜㅣ", "ㅟ"); put("ㅡㅣ", "ㅢ");
        put("ㄱㅅ", "ㄳ"); put("ㄴㅈ", "ㄵ"); put("ㄴㅎ", "ㄶ"); put("ㄹㄱ", "ㄺ"); put("ㄹㅁ", "ㄻ"); put("ㄹㅂ", "ㄼ"); put("ㄹㅅ", "ㄽ"); put("ㄹㅌ", "ㄾ"); put("ㄹㅍ", "ㄿ"); put("ㄹㅎ", "ㅀ"); put("ㅂㅅ", "ㅄ");
    }};

    private static boolean isAlphabet(int str) {
        return ALPHABETS.indexOf(str) >= 0;
    }

    private static char toHangeul(int str) {
        return HANGEUL_JAMOS.charAt(ALPHABETS.indexOf(str));
    }

    private static String replaceAlphabets(String str) {
        return str.chars().mapToObj(c -> String.valueOf((char) (isAlphabet(c) ? toHangeul(c) : c))).collect(Collectors.joining());
    }

    private static String stack(String i, String m, String f) {
        if (Objects.isNull(f)) f = " ";
        return new String(Character.toChars(44032 + FINAL_JAMOS.indexOf(f) + MEDIAL_JAMOS.indexOf(m) * 28 + INITIAL_JAMOS.indexOf(i) * 588));
    }

    private static String gksdud(String str) {
        final StringBuffer buffer = new StringBuffer();
        final Matcher matcher = HANGEUL_SYLLABLE.matcher(replaceAlphabets(str));

        while (matcher.find()) {
            final String i = matcher.group(1), m = matcher.group(2), f = matcher.group(3);
            matcher.appendReplacement(buffer, stack(i, COMPLEX_JAMOS.getOrDefault(m, m), COMPLEX_JAMOS.getOrDefault(f, f)));
        }

        return matcher.appendTail(buffer).toString();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        new Metrics(this, 17525);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerInteract(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        final String[] parts = event.getMessage().split(",");
        final String origin = String.join("", parts);

        for (int i = 1; i < parts.length; i += 2) parts[i] = gksdud(parts[i]);
        final String converted = String.join("", parts);

        final String suffix = origin.equals(converted) ? "" : ChatColor.DARK_GRAY + " (변환됨)";
        event.setMessage(converted + suffix);
    }
}
