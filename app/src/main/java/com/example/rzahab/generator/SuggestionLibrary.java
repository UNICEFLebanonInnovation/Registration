package com.example.rzahab.generator;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rzahab on 6/2/2017.
 */

public class SuggestionLibrary {

    private Map<String, String> arabicLib;
    private String TAG;

    public SuggestionLibrary() {
        Log.d("SuggestionLibrary", "initializing");
        setLib();
        TAG = this.getClass().getSimpleName();
        //_testing_transliterate("أحمد");
        //_testing_getSimilarity();
    }

    public double getDifferenceRate(Map<String, String> filled_fields, HashMap<String, String> currentUser) {


        double rate = 0;
        int different_fields = 0;

        for (Map.Entry<String, String> entry : filled_fields.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            String equiv_key = key + "_equiv";

            String user_val = currentUser.containsKey(equiv_key) ? currentUser.get(equiv_key) : currentUser.get(key);
            Log.d(TAG, key + ", " + equiv_key + " : (" + value + "==" + user_val + ") = " );

            Double current_rate = this.getDifference(value, user_val);
            Log.d(TAG, key + ", " + equiv_key + " : (" + value + "==" + user_val + ") = " + current_rate);

            if (current_rate > 0)
                different_fields++;
            if (current_rate > 50)
                return current_rate;

            rate = rate + current_rate;
        }

        return (different_fields == 0) ? rate : rate / different_fields;
    }

    public double getDifference(String s, String t) {
        //Only convert arabic content
        //Log.d(TAG,"Comp: "+s+"=="+t+" => "+getDistancePercentage(s,t) );
        s = transliterate(s);
        t = transliterate(t);

        return getDistancePercentage(s, t);
    }

    public boolean requireTrans(String word) {
        return !word.matches("^[A-z ]*$");
    }

    public String transliterate(String word) {

        if (requireTrans(word))
            return word;

        String newWord = "";
        for (int i = 0; i < word.length(); i++) {
            newWord += transliterateLetter(word.charAt(i));
        }
        return newWord;
    }

    public String transliterateLetter(char letter) {
        String equiv = Integer.toHexString(letter | 0x10000).substring(1);
        return arabicLib.containsKey(equiv.toUpperCase()) ? arabicLib.get(equiv.toUpperCase()) : letter + "";
    }

    double getDistancePercentage(String s, String t) {
        int distance = getLVDistance(s, t);
        int maxLength = s.length() < t.length() ? t.length() : s.length();
        return (100 * distance) / maxLength;
    }

    int getLVDistance(String s, String t) {
        // degenerate cases
        if (s.equals(t)) return 0;
        if (s.length() == 0) return t.length();
        if (t.length() == 0) return s.length();

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++)
            v0[i] = i;

        for (int i = 0; i < s.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < t.length(); j++) {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = Math.min(v1[j] + 1, Math.min(v0[j + 1] + 1, v0[j] + cost));
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            for (int j = 0; j < v0.length; j++)
                v0[j] = v1[j];
        }

        return v1[t.length()];
    }

    private void setLib() {
        this.arabicLib = new HashMap<String, String>();
        arabicLib.put("0600", " ");
        arabicLib.put("0601", " ");
        arabicLib.put("0602", " ");
        arabicLib.put("0603", " ");
        arabicLib.put("0604", " ");
        arabicLib.put("0605", " ");
        arabicLib.put("0606", " ");
        arabicLib.put("0607", " ");
        arabicLib.put("0608", " ");
        arabicLib.put("0609", " ");
        arabicLib.put("060A", " ");
        arabicLib.put("060B", " ");
        arabicLib.put("060C", " ");
        arabicLib.put("060D", " ");
        arabicLib.put("060E", " ");
        arabicLib.put("060F", " ");
        arabicLib.put("0610", " ");
        arabicLib.put("0611", " ");
        arabicLib.put("0612", " ");
        arabicLib.put("0613", " ");
        arabicLib.put("0614", " ");
        arabicLib.put("0615", " ");
        arabicLib.put("0616", " ");
        arabicLib.put("0617", " ");
        arabicLib.put("0618", " ");
        arabicLib.put("0619", " ");
        arabicLib.put("061A", " ");
        arabicLib.put("061B", " ");
        arabicLib.put("061C", " ");
        arabicLib.put("061E", " ");
        arabicLib.put("061F", " ");
        arabicLib.put("0620", " ");
        arabicLib.put("0621", "a");
        arabicLib.put("0622", "a");
        arabicLib.put("0623", "a");
        arabicLib.put("0624", "a");
        arabicLib.put("0625", "a");
        arabicLib.put("0626", "a");
        arabicLib.put("0627", "a");
        arabicLib.put("0628", "b");
        arabicLib.put("0629", "t");
        arabicLib.put("062A", "t");
        arabicLib.put("062B", "th");
        arabicLib.put("062C", "j");
        arabicLib.put("062D", "h");
        arabicLib.put("062E", "k");
        arabicLib.put("062F", "d");
        arabicLib.put("0630", "zh");
        arabicLib.put("0631", "r");
        arabicLib.put("0632", "z");
        arabicLib.put("0633", "s");
        arabicLib.put("0634", "sh");
        arabicLib.put("0635", "S");
        arabicLib.put("0636", "D");
        arabicLib.put("0637", "T");
        arabicLib.put("0638", "Z");
        arabicLib.put("0639", "a");
        arabicLib.put("063A", "gh");
        arabicLib.put("063B", "k");
        arabicLib.put("063C", "k");
        arabicLib.put("063D", "y");
        arabicLib.put("063E", "y");
        arabicLib.put("063F", "y");
        arabicLib.put("0640", "a");
        arabicLib.put("0641", "f");
        arabicLib.put("0642", "K");
        arabicLib.put("0643", "k");
        arabicLib.put("0644", "l");
        arabicLib.put("0645", "m");
        arabicLib.put("0646", "n");
        arabicLib.put("0647", "h");
        arabicLib.put("0648", "w");
        arabicLib.put("0649", "y");
        arabicLib.put("064A", "y");
        arabicLib.put("064B", " ");
        arabicLib.put("064C", " ");
        arabicLib.put("064D", " ");
        arabicLib.put("064E", " ");
        arabicLib.put("064F", " ");
        arabicLib.put("0650", " ");
        arabicLib.put("0651", " ");
        arabicLib.put("0652", " ");
        arabicLib.put("0653", " ");
        arabicLib.put("0654", " ");
        arabicLib.put("0655", " ");
        arabicLib.put("0656", " ");
        arabicLib.put("0657", " ");
        arabicLib.put("0658", " ");
        arabicLib.put("0659", " ");
        arabicLib.put("065A", " ");
        arabicLib.put("065B", " ");
        arabicLib.put("065C", " ");
        arabicLib.put("065D", " ");
        arabicLib.put("065E", " ");
        arabicLib.put("065F", " ");
        arabicLib.put("066E", " ");
        arabicLib.put("066F", " ");
        arabicLib.put("0670", " ");
        arabicLib.put("0671", "a");
        arabicLib.put("0672", "a");
        arabicLib.put("0673", "a");
        arabicLib.put("0674", "a");
        arabicLib.put("0675", "a");
        arabicLib.put("0676", "o");
        arabicLib.put("0677", "o");
        arabicLib.put("0678", "a");
        arabicLib.put("0679", "t");
        arabicLib.put("067A", "t");
        arabicLib.put("067B", "b");
        arabicLib.put("067C", "t");
        arabicLib.put("067D", "t");
        arabicLib.put("067E", "p");
        arabicLib.put("067F", "t");
        arabicLib.put("0680", "b");
        arabicLib.put("0681", "h");
        arabicLib.put("0682", "h");
        arabicLib.put("0683", "h");
        arabicLib.put("0684", "h");
        arabicLib.put("0685", "h");
        arabicLib.put("0686", "h");
        arabicLib.put("0687", "h");
        arabicLib.put("0688", "d");
        arabicLib.put("0689", "d");
        arabicLib.put("068A", "d");
        arabicLib.put("068B", "d");
        arabicLib.put("068C", "d");
        arabicLib.put("068D", "d");
        arabicLib.put("068E", "d");
        arabicLib.put("068F", "d");
        arabicLib.put("0690", "d");
        arabicLib.put("0691", "r");
        arabicLib.put("0692", "r");
        arabicLib.put("0693", "r");
        arabicLib.put("0694", "r");
        arabicLib.put("0695", "r");
        arabicLib.put("0696", "r");
        arabicLib.put("0697", "r");
        arabicLib.put("0698", "r");
        arabicLib.put("0699", "r");
        arabicLib.put("069A", "s");
        arabicLib.put("069B", "s");
        arabicLib.put("069C", "s");
        arabicLib.put("069D", "S");
        arabicLib.put("069E", "D");
        arabicLib.put("069F", "ZH");
        arabicLib.put("06A0", "a");
        arabicLib.put("06A1", "f");
        arabicLib.put("06A2", "f");
        arabicLib.put("06A3", "f");
        arabicLib.put("06A4", "f");
        arabicLib.put("06A5", "f");
        arabicLib.put("06A6", "f");
        arabicLib.put("06A7", "K");
        arabicLib.put("06A8", "K");
        arabicLib.put("06A9", "k");
        arabicLib.put("06AA", "k");
        arabicLib.put("06AB", "k");
        arabicLib.put("06AC", "k");
        arabicLib.put("06AD", "k");
        arabicLib.put("06AE", "k");
        arabicLib.put("06AF", "k");
        arabicLib.put("06B0", "k");
        arabicLib.put("06B1", "k");
        arabicLib.put("06B2", "k");
        arabicLib.put("06B3", "k");
        arabicLib.put("06B4", "k");
        arabicLib.put("06B5", "l");
        arabicLib.put("06B6", "l");
        arabicLib.put("06B7", "l");
        arabicLib.put("06B8", "l");
        arabicLib.put("06B9", "n");
        arabicLib.put("06BA", "n");
        arabicLib.put("06BB", "n");
        arabicLib.put("06BC", "n");
        arabicLib.put("06BD", "n");
        arabicLib.put("06BE", "h");
        arabicLib.put("06BF", "g");
        arabicLib.put("06C0", "h");
        arabicLib.put("06C1", "h");
        arabicLib.put("06C2", "h");
        arabicLib.put("06C3", "t");
        arabicLib.put("06C4", "w");
        arabicLib.put("06C5", "w");
        arabicLib.put("06C6", "w");
        arabicLib.put("06C7", "w");
        arabicLib.put("06C8", "w");
        arabicLib.put("06C9", "w");
        arabicLib.put("06CA", "w");
        arabicLib.put("06CB", "w");
        arabicLib.put("06CC", "i");
        arabicLib.put("06CD", "i");
        arabicLib.put("06CE", "i");
        arabicLib.put("06CF", "w");
        arabicLib.put("06D0", "i");
        arabicLib.put("06D1", "i");
        arabicLib.put("06D2", "i");
        arabicLib.put("06D3", "i");
        arabicLib.put("06D4", ".");
        arabicLib.put("06D5", "a");
        arabicLib.put("06D6", "S");
        arabicLib.put("06D7", "K");
        arabicLib.put("06D8", "m");
        arabicLib.put("06D9", "l");
        arabicLib.put("06DA", "j");
        arabicLib.put("06DB", " ");
        arabicLib.put("06DC", "s");
        arabicLib.put("06DD", " ");
        arabicLib.put("06DE", " ");
        arabicLib.put("06DF", " ");
        arabicLib.put("06E0", " ");
        arabicLib.put("06E1", " ");
        arabicLib.put("06E2", " ");
        arabicLib.put("06E3", " ");
        arabicLib.put("06E4", " ");
        arabicLib.put("06E5", " ");
        arabicLib.put("06E6", " ");
        arabicLib.put("06E7", " ");
        arabicLib.put("06E8", " ");
        arabicLib.put("06E9", " ");
        arabicLib.put("06EA", " ");
        arabicLib.put("06EB", " ");
        arabicLib.put("06EC", " ");
        arabicLib.put("06ED", "v");
        arabicLib.put("06EE", "v");
        arabicLib.put("06EF", "v");
        arabicLib.put("06F0", "0");
        arabicLib.put("06F1", "1");
        arabicLib.put("06F2", "2");
        arabicLib.put("06F3", "3");
        arabicLib.put("06F4", "4");
        arabicLib.put("06F5", "5");
        arabicLib.put("06F6", "6");
        arabicLib.put("06F7", "7");
        arabicLib.put("06F8", "8");
        arabicLib.put("06F9", "9");
        arabicLib.put("06FA", "sh");
        arabicLib.put("06FB", "D");
        arabicLib.put("06FC", "gh");
        arabicLib.put("06FD", "a");
        arabicLib.put("06FE", "m");
        arabicLib.put("06FF", "h");
    }

    public void _testing_transliterateLetter() {
        String[] letters = new String[]{"؀", "؁", "؂", "؃", "؄", "؅", "؆", "؇", "؈", "؉", "؊", "؋", "،", "؍",
                "؎", "؏", "ؐ", "ؑ", "ؒ", "ؓ", "ؔ", "ؕ", "ؖ", "ؗ", "ؘ", "ؙ", "ؚ", "؛", "؜", "؞", "؟", "ؠ", "ء", "آ", "أ", "ؤ", "إ", "ئ"
                , "ا", "ب", "ة", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ",
                "ػ", "ؼ", "ؽ", "ؾ", "ؿ", "ـ", "ف", "ق", "ك", "ل", "م", "ن", "ه", "و", "ى", "ي", "ً", "ٌ", "ٍ", "َ", "ُ", "ِ",
                "ّ", "ْ", "ٓ", "ٔ", "ٕ", "ٖ", "ٗ", "٘", "ٙ", "ٚ", "ٛ", "ٜ", "ٝ", "ٞ", "ٟ", "ٮ", "ٯ", "ٰ", "ٱ", "ٲ", "ٳ", "ٴ", "ٵ", "ٶ", "ٷ",
                "ٸ", "ٹ", "ٺ", "ٻ", "ټ", "ٽ", "پ", "ٿ", "ڀ", "ځ", "ڂ", "ڃ", "ڄ", "څ", "چ", "ڇ", "ڈ", "ډ", "ڊ", "ڋ", "ڌ"
                , "ڍ", "ڎ", "ڏ", "ڐ", "ڑ", "ڒ", "ړ", "ڔ", "ڕ", "ږ", "ڗ", "ژ", "ڙ", "ښ", "ڛ", "ڜ", "ڝ", "ڞ", "ڟ", "ڠ",
                "ڡ", "ڢ", "ڣ", "ڤ", "ڥ", "ڦ", "ڧ", "ڨ", "ک", "ڪ", "ګ", "ڬ", "ڭ", "ڮ", "گ", "ڰ", "ڱ", "ڲ", "ڳ", "ڴ",
                "ڵ", "ڶ", "ڷ", "ڸ", "ڹ", "ں", "ڻ", "ڼ", "ڽ", "ھ", "ڿ", "ۀ", "ہ", "ۂ", "ۃ", "ۄ", "ۅ", "ۆ", "ۇ", "ۈ",
                "ۉ", "ۊ", "ۋ", "ی", "ۍ", "ێ", "ۏ", "ې", "ۑ", "ے", "ۓ", "۔", "ە", "ۖ", "ۗ", "ۘ", "ۙ", "ۚ", "ۛ", "ۜ", "۝", "۞",
                "۟", "۠", "ۡ", "ۢ", "ۣ", "ۤ", "ۥ", "ۦ", "ۧ", "ۨ", "۩", "۪", "۫", "۬", "ۭ", "ۮ", "ۯ", "۰", "۱", "۲", "۳", "۴", "۵", "۶",
                "۷", "۸", "۹", "ۺ", "ۻ", "ۼ", "۽", "۾", "ۿ"};

        for (int i = 0; i < letters.length; i++) {
            Log.d("Convert", letters[i] + " -> " + transliterateLetter(letters[i].charAt(0)));
        }
    }

    public void _testing_getLVDistance() {
        String first = "الأء";
        String second = "ألاء";
        Log.d("Testing", "Conv : " + first + " -> " + transliterate(first));
        int diff = this.getLVDistance(first, second);
        Log.d("Testing", "Diff is :" + diff);
        double perc = getDistancePercentage(first, second);
        Log.d("Testing", "Perc is :" + perc);

        //After conversion
        perc = getDifference(first, second);
        Log.d("Testing", "Perc after conv is :" + perc);
    }

    public void _testing_transliterate(String word) {
        String newWord = transliterate(word);
        Log.d("Testing", "Transliterate : " + word + " -> " + newWord);
    }

    public void _testing_getSimilarity() {
        String s = "الآء";// "raya";//
        String t = "ألاء";//"rayan";
        Log.d("Testing", " Simil: " + getDifference(s, t));
    }
}
