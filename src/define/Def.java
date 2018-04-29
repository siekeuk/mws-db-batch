package define;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Def {

    public static final Map<String, String> EXP_MAP = new LinkedHashMap<String, String>() {
        {
            put("T16", "テスト2016");
            put("T17", "テスト2017");
            put("T18", "テスト2018");
        }
    };

    public static final List<String> MSE_PROP_LIST = new ArrayList<String>() {
        {
            add("Card Name");
            add("Card Color");
            add("Mana Cost");
            add("Type & Class");
            add("Pow/Tou");
            add("Card Text");
            add("Flavor Text");
            add("Artist");
            add("Rarity");
            add("Card #");
        }
    };

    public static final List<String> CARD_TYPE_LIST = new ArrayList<String>() {
        {
            add("土地");
            add("クリーチャー");
            add("エンチャント");
            add("アーティファクト");
            add("インスタント");
            add("ソーサリー");
            add("部族");
            add("プレインズウォーカー");
        }
    };

    public static final List<String> SUPERTYPE_LIST = new ArrayList<String>() {
        {
            add("伝説の");
            add("基本");
        }
    };

    public static final Map<String, String> COLOR_MAP = new LinkedHashMap<String, String>() {
        {
            put("W", "白");
            put("U", "青");
            put("B", "黒");
            put("R", "赤");
            put("G", "緑");
        }
    };

    public static final Map<String, String> RARITY_MAP = new LinkedHashMap<String, String>() {
        {
            put("C", "コモン");
            put("U", "アンコモン");
            put("R", "レア");
            put("M", "神話レア");
        }
    };

    public static final Map<Integer, List<String>> COLOR_SORT = new LinkedHashMap<Integer, List<String>>() {
        {
            int sortIdx = 0;
            put(sortIdx++, new ArrayList<String>() {{
                add("");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("L");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("U");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("B");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("G");
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("B");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("U");
                add("B");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("B");
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
                add("R");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
                add("G");
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("G");
                add("W");
                add("U");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("B");
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("R");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
                add("G");
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
                add("W");
                add("U");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("G");
                add("U");
                add("B");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("U");
                add("B");
                add("R");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("U");
                add("B");
                add("R");
                add("G");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("B");
                add("R");
                add("G");
                add("W");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("R");
                add("G");
                add("W");
                add("U");
            }});
            put(sortIdx++, new ArrayList<String>() {{
                add("W");
                add("U");
                add("B");
                add("R");
                add("G");
            }});
        }
    };

}
