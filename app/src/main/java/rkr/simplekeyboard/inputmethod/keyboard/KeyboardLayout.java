package rkr.simplekeyboard.inputmethod.keyboard;
import static android.view.KeyEvent.*;

public class KeyboardLayout {

    public static final char nul = '\0';
    public static final char RET = '\n';
    public static final char BAK = '\u0008';

    // arrows (private use chars below 0xE100)
    public static final char ARR = '\uE001';
    public static final char ARL = '\uE002';
    public static final char ARU = '\uE003';
    public static final char ARD = '\uE004';

    // mode switches (private use chars above 0xE100)
    public static final char SYM = '\uE101'; // extra symbols
    public static final char LET = '\uE102'; // lowercase letters
    public static final char NUM = '\uE103'; // numbers and basic symbols
    public static final char CAP = '\uE104'; // Single cap (then return to lowercase)
    public static final char LOK = '\uE105'; // Lock caps
    public static final char AC1 = '\uE106'; // Accents 1
    public static final char AC2 = '\uE107'; // Accents 2
    public static final char CHM = '\uE108'; // Mode list


    private static final char[][] sLowerLetters = { // LET
            {'t','c',nul,  'q','h','j',  nul,'b','e'},
            {'d','.',nul,  nul,'u',nul,  nul,',','s'},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {'\'',nul,nul, ARL,RET,ARR,  nul,nul,'"'},
            {'i','k',nul,  NUM,' ',CAP,  nul,'w','a'},
            {'p',nul,nul,  ARU,BAK,ARD,  nul,nul,'l'},

            {nul,nul,nul,  nul,CHM,nul,  '@',nul,nul},
            {'x',':',nul,  '!','y','?',  nul,'/','z'},
            {'o','v','-',  'm','n','g',  nul,'f','r'},
    };
    private static final char[][] sUpperLetters = { // CAP
            {'T','C',nul,  'Q','H','J',  nul,'B','E'},
            {'D','.',nul,  nul,'U',nul,  nul,',','S'},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {'\'',nul,nul, ARL,RET,ARR,  '"',nul,nul},
            {'I','K',nul,  NUM,' ',LOK,  nul,'W','A'},
            {'P',nul,nul,  ARU,BAK,ARD,  nul,nul,'L'},

            {nul,nul,nul,  nul,CHM,nul,  '@',nul,nul},
            {'X',':',nul,  '!','Y','?',  nul,'/','Z'},
            {'O','V','-',  'M','N','G',  nul,'F','R'},
    };
    private static final char[][] sCapsLockLetters = { // LOK
            {'T','C',nul,  'Q','H','J',  nul,'B','E'},
            {'D','.',nul,  nul,'U',nul,  nul,',','S'},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {'\'',nul,nul, ARL,RET,ARR,  '"',nul,nul},
            {'I','K',nul,  NUM,' ',LET,  nul,'W','A'},
            {'P',nul,nul,  ARU,BAK,ARD,  nul,nul,'L'},

            {nul,nul,nul,  nul,CHM,nul,  '@',nul,nul},
            {'X',':',nul,  '!','Y','?',  nul,'/','Z'},
            {'O','V','-',  'M','N','G',  nul,'F','R'},
    };
    private static final char[][] sNumeric = { // NUM
            {'1','=',nul,  nul,'3',nul,  nul,'_','5'},
            {'2',nul,nul,  nul,'4',nul,  nul,nul,'6'},
            {'#','×','%',  nul,nul,nul,  '‹','›','÷'},

            {'~',nul,nul,  ARL,RET,ARR,  nul,nul,nul},
            {'7','.',nul,  CAP,' ',LET,  nul,',','0'},
            {'8',nul,nul,  ARU,BAK,ARD,  nul,nul,'9'},

            {nul,nul,nul,  nul,nul,nul,  '@',nul,nul},
            {'[','|','&',  ':','/',';',  nul,'\\',']'},
            {'(','{','<',  '-','+','*',  '>','}',')'},
    };
    private static final char[][] sAccents1 = { // AC1
            {'à','á','â',  'è','é','ê',  'ì','í','î'},
            {'ã','ä','å',  'ë','ė','ě',  'ï','ĩ','ī'},
            {'æ','ą','ă',  'ǽ','œ','ę',  'ĭ','į','ı'},

            {'ò','ó','ô',  ARL,RET,ARR,  'ù','ú','û'},
            {'õ','ö','ø',  AC2,' ',LET,  'ü','ũ','ū'},
            {'ō','ŏ','ő',  ARU,BAK,ARD,  'ŭ','ů','ű'},

            {'ń','ñ','ŋ',  'ć','ĉ','ċ',  'ķ','ĸ','Þ'},
            {'ŉ','ň','ņ',  'č','ç','đ',  'ß','ţ','ť'},
            {'ğ','ĝ','ģ',  'ĥ','ħ','ð',  'ý','ÿ','ŷ'},
    };
    private static final char[][] sAccents2 = { // AC2
            {'À','Á','Â',  'È','É','Ê',  'Ì','Í','Î'},
            {'Ã','Ä','Å',  'Ë','Ė','Ě',  'Ï','Ĩ','Ī'},
            {'Æ','Ą','Ă',  'Ǽ','Œ','Ę',  'Ĭ','Į','I'},

            {'Ò','Ó','Ô',  ARL,RET,ARR,  'Ù','Ú','Û'},
            {'Õ','Ö','Ø',  AC1,' ',LET,  'Ü','Ũ','Ū'},
            {'Ō','Ŏ','Ő',  ARU,BAK,ARD,  'Ŭ','Ů','Ű'},

            {'ź','ż','ž',  'α','β','γ',  'Α','Β','Γ'},
            {'ś','ŝ','ş',  'σ','θ','λ',  'Σ','Θ','Λ'},
            {'š','ſ','ƒ',  'φ','ω','Φ',  'Φ','Ω','φ'},
    };

    private static final char[][] sSymbols = { // SYM
            {'$','€','£',  '³','²','¹',  '≤','≥','≠'},
            {'¢','¥','₧',  '•','●','ⁿ',  '≈','∫','∞'},
            {'±','°','µ',  '□','▪','◊',  '∂','∆','∑'},

            {'¤','’','^',  ARL,RET,ARR,  'º','ª','®'},
            {'”','¬','¦',  NUM,' ',LET,  '«','»','©'},
            {'√','·','`',  ARU,BAK,ARD,  '¿','¡','¶'},

            {'⅜','½','⅝',  '↖','↑','↗',  '⅓','⅔','⅕'},
            {'¼','∕','¾',  '←','Ω','→',  '⅖','⅗','⅘'},
            {'⅛','∛','⅞',  '↙','↓','↘',  '⅐','⅑','⅒'},
    };
    private static final char[][] sChangeMode = { // CHM
            {SYM,nul,nul,  nul,AC1,nul,  nul,nul,AC2},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {LOK,nul,nul,  nul,LET,nul,  nul,nul,CAP},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {nul,nul,nul,  nul,NUM,nul,  nul,nul,nul},
    };


    // Note: we could maybe do a 'Unicode pages' mode that moves the base forward/back and just scrolls through everything

    private static char sCurrentMode = LET;

    public static char[][] CurrentLayout(){
        switch (sCurrentMode){
            case LET: return sLowerLetters;
            case CAP: return sUpperLetters;
            case LOK: return sCapsLockLetters;
            case NUM: return sNumeric;

            case CHM: return sChangeMode;
            case AC1: return sAccents1;
            case AC2: return sAccents2;
            case SYM: return sSymbols;

            default:// any wrong modes, flip back to default
                sCurrentMode = LET;
                return sLowerLetters;
        }
    }

    /**
     * Change current layout based on a mode character
     */
    public static void SwitchMode(char c) {
        if (!IsInternal(c)) return;
        sCurrentMode = c;
    }

    /**
     * Returns true if the key is a single character key
     */
    public static boolean IsSimple(char c) {
        return (c > 31 && c < 0xE000);
    }

    /**
     * Returns true if the character is a mode-change character (no keyboard output)
     */
    public static boolean IsInternal(char c) {
        return (c >= 0xE100);
    }

    public static String Visualise(char c){
        if (IsSimple(c)) return ""+c;

        switch (c){
            case nul: return "";

            case RET: return "↲";
            case SYM: return "∑$µ";
            case CAP: return "Abc";
            case LOK: return "ABC";
            case LET: return "abc";
            case NUM: return "123";
            case AC1: return "äŋç";
            case AC2: return "ÃΣØ";
            case CHM: return "mode";
            case BAK: return "⇦";

            case ARD: return "↓";
            case ARU: return "↑";
            case ARL: return "←";
            case ARR: return "→";

            default: return "⁇";
        }
    }

    /**
     * register a press start at a given location index
     * @param xi x index 0..2
     * @param yi y index 0..2
     */
    public static void TouchDown(int xi, int yi)
    {
        if (xi >= 0 && xi < 3) sQuadrantX = xi * 3;
        else if (xi >= 3) sQuadrantX = 9; // allow running off the end?
        else sQuadrantX = 0;


        if (yi >= 0 && yi < 3) sQuadrantY = yi * 3;
        else if (yi >= 3) sQuadrantX = 9;
        else sQuadrantY = 0;
    }

    public static int sQuadrantY = 0;
    public static int sQuadrantX = 0;

    /**
     * register a release of a touch at a given location index.
     * This may change the current layout, and it may return a character
     * @param xi x index 0..2
     * @param yi y index 0..2
     */
    public static char TouchUp(int xi, int yi){
        if (sQuadrantX < 0) return nul;
        if (sQuadrantY < 0) return nul;

        // try limiting rather than rejecting?
        if (xi<0) xi=0; if (xi >=3) xi=2;
        if (yi<0) yi=0; if (yi >=3) yi=2;

        int qy = sQuadrantY;
        int qx = sQuadrantX;
        char result = CurrentLayout()[qy+yi][qx+xi];

        if (sCurrentMode == CAP) sCurrentMode = LET; // drop out of single cap mode

        return result;
    }

    private static final int[] NoKey = new int[]{-1,-1};

    /**
     * Returns a keycode at [0] and meta state at [1] for a special key
     */
    public static int[] GetSpecialKey(char c) {
        switch (c){
            case RET: return new int[]{KEYCODE_ENTER, 0};
            case BAK: return new int[]{KEYCODE_DEL, 0};

            case ARD: return new int[]{KEYCODE_DPAD_DOWN, 0};
            case ARU: return new int[]{KEYCODE_DPAD_UP, 0};
            case ARL: return new int[]{KEYCODE_DPAD_LEFT, 0};
            case ARR: return new int[]{KEYCODE_DPAD_RIGHT, 0};

            default: return NoKey;
        }
    }
}
