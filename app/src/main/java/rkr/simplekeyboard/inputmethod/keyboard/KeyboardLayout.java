package rkr.simplekeyboard.inputmethod.keyboard;
import static android.view.KeyEvent.*;

import java.util.Objects;

public class KeyboardLayout {

    public static final String nul = "\0";
    public static final String RET = "\n";
    public static final String BAK = "\u0008";

    // arrows, other special buttons (private use chars below 0xE100)
    public static final String ARR = "\uE001";
    public static final String ARL = "\uE002";
    public static final String ARU = "\uE003";
    public static final String ARD = "\uE004";
    public static final String SER = "\uE005"; // search action
    public static final String CPY = "\uE006"; // Copy
    public static final String PST = "\uE007"; // Paste

    // mode switches (private use chars above 0xE100)
    public static final String SYM = "\uE101"; // extra symbols
    public static final String LET = "\uE102"; // lowercase letters
    public static final String NUM = "\uE103"; // numbers and basic symbols
    public static final String CAP = "\uE104"; // Single cap (then return to lowercase)
    public static final String LOK = "\uE105"; // Lock caps
    public static final String AC1 = "\uE106"; // Accents 1
    public static final String AC2 = "\uE107"; // Accents 2
    public static final String CHM = "\uE108"; // Mode list
    public static final String EMO = "\uE109"; // Emoji


    private static final String[][] sLowerLetters = { // LET
            {"t","c",nul,  "q","h","j",  nul,"b","e"},
            {"d",".",nul,  nul,"u",nul,  nul,",","s"},
            {nul,nul,CPY,  SER,nul,nul,  PST,nul,nul},

            {"'",nul,nul,  ARL,RET,ARR,  nul,nul,"\""},
            {"i","k",nul,  NUM," ",CAP,  nul,"w","a"},
            {"p",nul,nul,  ARU,BAK,ARD,  nul,nul,"l"},

            {nul,nul,nul,  CHM,nul,nul,  "@",nul,nul},
            {"x",":",nul,  "!","y","?",  nul,"/","z"},
            {"o","v","-",  "m","n","g",  nul,"f","r"},
    };
    private static final String[][] sUpperLetters = { // CAP
            {"T","C",nul,  "Q","H","J",  nul,"B","E"},
            {"D",".",nul,  nul,"U",nul,  nul,",","S"},
            {nul,nul,CPY,  SER,nul,nul,  PST,nul,nul},

            {"'",nul,nul,  ARL,RET,ARR,  nul,nul,"\""},
            {"I","K",nul,  NUM," ",LOK,  nul,"W","A"},
            {"P",nul,nul,  ARU,BAK,ARD,  nul,nul,"L"},

            {nul,nul,nul,  CHM,nul,nul,  "@",nul,nul},
            {"X",":",nul,  "!","Y","?",  nul,"/","Z"},
            {"O","V","-",  "M","N","G",  nul,"F","R"},
    };
    private static final String[][] sCapsLockLetters = { // LOK
            {"T","C",nul,  "Q","H","J",  nul,"B","E"},
            {"D",".",nul,  nul,"U",nul,  nul,",","S"},
            {nul,nul,CPY,  SER,nul,nul,  PST,nul,nul},

            {"'",nul,nul,  ARL,RET,ARR,  nul,nul,"\""},
            {"I","K",nul,  NUM," ",LET,  nul,"W","A"},
            {"P",nul,nul,  ARU,BAK,ARD,  nul,nul,"L"},

            {nul,nul,nul,  CHM,nul,nul,  "@",nul,nul},
            {"X",":",nul,  "!","Y","?",  nul,"/","Z"},
            {"O","V","-",  "M","N","G",  nul,"F","R"},
    };
    private static final String[][] sNumeric = { // NUM
            {"1","=",nul,  nul,"3",nul,  nul,"_","5"},
            {"2",nul,nul,  nul,"4",nul,  nul,nul,"6"},
            {"#","×","%",  nul,nul,nul,  "‹","›","÷"},

            {"~",nul,nul,  ARL,RET,ARR,  nul,nul,nul},
            {"7",".",nul,  CAP," ",LET,  nul,",","0"},
            {"8",nul,nul,  ARU,BAK,ARD,  nul,nul,"9"},

            {nul,nul,nul,  CHM,nul,nul,  "@",nul,nul},
            {"[","|","&",  ":","/",";",  nul,"\\","]"},
            {"(","{","<",  "-","+","*",  ">","}",")"},
    };
    private static final String[][] sAccents1 = { // AC1
            {"à","á","â",  "è","é","ê",  "ì","í","î"},
            {"ã","ä","å",  "ë","ė","ě",  "ï","ĩ","ī"},
            {"æ","ą","ă",  "ǽ","œ","ę",  "ĭ","į","ı"},

            {"ò","ó","ô",  ARL,RET,ARR,  "ù","ú","û"},
            {"õ","ö","ø",  AC2," ",LET,  "ü","ũ","ū"},
            {"ō","ŏ","ő",  ARU,BAK,ARD,  "ŭ","ů","ű"},

            {"ń","ñ","ŋ",  "ć","ĉ","ċ",  "ķ","ĸ","Þ"},
            {"ŉ","ň","ņ",  "č","ç","đ",  "ß","ţ","ť"},
            {"ğ","ĝ","ģ",  "ĥ","ħ","ð",  "ý","ÿ","ŷ"},
    };
    private static final String[][] sAccents2 = { // AC2
            {"À","Á","Â",  "È","É","Ê",  "Ì","Í","Î"},
            {"Ã","Ä","Å",  "Ë","Ė","Ě",  "Ï","Ĩ","Ī"},
            {"Æ","Ą","Ă",  "Ǽ","Œ","Ę",  "Ĭ","Į","I"},

            {"Ò","Ó","Ô",  ARL,RET,ARR,  "Ù","Ú","Û"},
            {"Õ","Ö","Ø",  AC1," ",LET,  "Ü","Ũ","Ū"},
            {"Ō","Ŏ","Ő",  ARU,BAK,ARD,  "Ŭ","Ů","Ű"},

            {"ź","ż","ž",  "α","β","γ",  "Α","Β","Γ"},
            {"ś","ŝ","ş",  "σ","θ","λ",  "Σ","Θ","Λ"},
            {"š","ſ","ƒ",  "φ","ω","Φ",  "Φ","Ω","φ"},
    };

    private static final String[][] sSymbols = { // SYM
            {"$","€","£",  "³","²","¹",  "≤","≥","≠"},
            {"¢","¥","₧",  "•","●","ⁿ",  "≈","∫","∞"},
            {"±","°","µ",  "□","▪","◊",  "∂","∆","∑"},

            {"¤","’","^",  ARL,RET,ARR,  "º","ª","®"},
            {"”","¬","¦",  NUM," ",LET,  "«","»","©"},
            {"√","·","`",  ARU,BAK,ARD,  "¿","¡","¶"},

            {"⅜","½","⅝",  "↖","↑","↗",  "⅓","⅔","⅕"},
            {"¼","∕","¾",  "←","Ω","→",  "⅖","⅗","⅘"},
            {"⅛","∛","⅞",  "↙","↓","↘",  "⅐","⅑","⅒"},
    };

    private static final String[][] sEmoji = { // SYM
            {Emoji.NoFace,Emoji.BigGrin,Emoji.Grin,          Emoji.Phew,Emoji.LoveFace,Emoji.KissFace,       Emoji.LookEyes,Emoji.SkullFace,Emoji.BigCry},
            {Emoji.BigSmile,Emoji.AngryFace,Emoji.Eyebrow,   Emoji.CowBoy,Emoji.CrossEye,Emoji.Puke,         Emoji.Ninja,Emoji.RobotFace,Emoji.FearFace},
            {Emoji.Rofl,Emoji.Smile,Emoji.Wink,              Emoji.Weary,Emoji.SwearFace,Emoji.RageFace,     Emoji.Brain,Emoji.Muscle,Emoji.ShitFace},

            {Emoji.Wrench,Emoji.Warning,Emoji.Radioactive,   ARL,RET,ARR,                                    Emoji.Toilet,Emoji.Runner,Emoji.Magician},
            {Emoji.Speech,Emoji.NoEntry,Emoji.BioHazard,     NUM,Emoji.ThumbUp,LET,                          Emoji.UpArrow,Emoji.TwoHearts,Emoji.Superhero},
            {Emoji.ThumbDown,Emoji.Denied,Emoji.WTF,         ARU,BAK,ARD,                                    Emoji.GoBack,Emoji.Zombie,Emoji.Fairy},

            {Emoji.PadLock1,Emoji.PaperClip,Emoji.Pencil,    Emoji.Camera,Emoji.FloppyDisk,Emoji.Controls,   Emoji.Landing,Emoji.TakeOff,Emoji.Rocket},
            {Emoji.PadLock2,Emoji.TrashBin,Emoji.Package,    Emoji.Bell,Emoji.Ribbon,Emoji.GiftBox,          Emoji.Planet,Emoji.Construction,Emoji.GearWheel},
            {Emoji.PadLock3,Emoji.Tools,Emoji.Calendar,      Emoji.WorldMap,Emoji.WarnLight,Emoji.Pitard,    Emoji.FuelPump,Emoji.MiniBusVan,Emoji.FastBike},
    };

    private static final String[][] sChangeMode = { // CHM
            {SYM,nul,nul,  nul,AC1,nul,  nul,nul,AC2},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {LOK,nul,nul,  nul,LET,nul,  nul,nul,CAP},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},

            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {nul,nul,nul,  nul,nul,nul,  nul,nul,nul},
            {EMO,nul,nul,  nul,NUM,nul,  nul,nul,nul},
    };


    // Note: we could maybe do a 'Unicode pages' mode that moves the base forward/back and just scrolls through everything

    private static String sCurrentMode = LET;

    public static String[][] CurrentLayout(){
        switch (sCurrentMode){
            case LET: return sLowerLetters;
            case CAP: return sUpperLetters;
            case LOK: return sCapsLockLetters;
            case NUM: return sNumeric;

            case CHM: return sChangeMode;
            case AC1: return sAccents1;
            case AC2: return sAccents2;
            case SYM: return sSymbols;
            case EMO: return sEmoji;

            default:// any wrong modes, flip back to default
                sCurrentMode = LET;
                return sLowerLetters;
        }
    }

    /**
     * Change current layout based on a mode character
     */
    public static void SwitchMode(String c) {
        if (!IsInternal(c)) return;
        sCurrentMode = c;
    }

    /**
     * Returns true if the key is a single character key
     */
    public static boolean IsSimple(String s) {
        char c = s.charAt(0);
        return (c > 31 && c < 0xE000);
    }

    /**
     * Returns true if the character is a mode-change character (no keyboard output)
     */
    public static boolean IsInternal(String s) {
        char c = s.charAt(0);
        return (c >= 0xE100);
    }

    public static String Visualise(String c){
        if (IsSimple(c)) return c;

        switch (c){
            case nul: return "";

            case RET: return "↲";
            case SYM: return "Sym";
            case CAP: return "Abc";
            case LOK: return "ABC";
            case LET: return "abc";
            case NUM: return "123";
            case AC1: return "äŋç";
            case AC2: return "ÃΣØ";
            case CHM: return "mode";
            case EMO: return "\uD83E\uDD28"; // smile face
            case BAK: return "⇦";
            case SER: return "\uD83D\uDD0D"; // magnifying glass icon
            case CPY: return " ⎘"; // next-page icon
            case PST: return "\uD83D\uDCCB"; // clipboard icon

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
    public static String TouchUp(int xi, int yi){
        if (sQuadrantX < 0) return nul;
        if (sQuadrantY < 0) return nul;

        // try limiting rather than rejecting?
        if (xi<0) xi=0; if (xi >=3) xi=2;
        if (yi<0) yi=0; if (yi >=3) yi=2;

        int qy = sQuadrantY;
        int qx = sQuadrantX;
        String result = CurrentLayout()[qy+yi][qx+xi];

        // Drop out of single-cap mode, unless we just pressed space
        if (Objects.equals(sCurrentMode, CAP) && (!Objects.equals(result, " "))) sCurrentMode = LET;

        return result;
    }

    private static final int[] NoKey = new int[]{-1,-1};

    /**
     * Returns a keycode at [0] and meta state at [1] for a special key
     */
    public static int[] GetSpecialKey(String c) {
        switch (c){
            case RET: return new int[]{KEYCODE_ENTER, 0};
            case BAK: return new int[]{KEYCODE_DEL, 0};

            case SER: return new int[]{KEYCODE_SEARCH, 0}; // Some inputs won't accept return.
            case CPY: return new int[]{278/*KEYCODE_COPY*/, 0}; // copy
            case PST: return new int[]{279/*KEYCODE_PASTE*/, 0}; // paste
            /* KEYCODE_CUT = 277 */

            case ARD: return new int[]{KEYCODE_DPAD_DOWN, 0};
            case ARU: return new int[]{KEYCODE_DPAD_UP, 0};
            case ARL: return new int[]{KEYCODE_DPAD_LEFT, 0};
            case ARR: return new int[]{KEYCODE_DPAD_RIGHT, 0};

            default: return NoKey;
        }
    }
}
