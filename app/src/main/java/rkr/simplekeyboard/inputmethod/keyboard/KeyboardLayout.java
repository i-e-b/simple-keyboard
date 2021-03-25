package rkr.simplekeyboard.inputmethod.keyboard;

import android.view.KeyEvent;

import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardCodesSet;

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
    public static final char SYM = '\uE101';
    public static final char NUM = '\uE102';
    public static final char CAP = '\uE103';
    public static final char ACC = '\uE104';
    public static final char LET = '\uE105';

    private static char[][] sLowerLetters = {
            {'t','c',nul,  'q','h','j',  nul,'b','e'},
            {'r',nul,nul,  nul,'u',nul,  nul,nul,'s'},
            {'.',nul,nul,  nul,SYM,nul,  nul,nul,','},

            {nul,nul,nul,  ARL,RET,ARR,  nul,nul,nul},
            {'i','k',nul,  NUM,' ',CAP,  nul,'w','a'},
            {'p',nul,nul,  ARU,BAK,ARD,  nul,nul,'l'},

            {nul,nul,nul,  nul,ACC,nul,  nul,nul,nul},
            {'x',nul,nul,  '!','y','?',  nul,nul,'z'},
            {'o','v',nul,  'm','n','g',  nul,'f','d'},
    };

    private static char sCurrentMode = LET;

    public static char[][] CurrentLayout(){
        // TODO: actual modes
        switch (sCurrentMode){
            case LET:
                return sLowerLetters;
            default:
                throw new IllegalStateException("Unexpected value: " + sCurrentMode);
        }
    }

    /**
     * Change current layout based on a mode character
     */
    public static void SwitchMode(char c) {
        // TODO: implement this
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
            case SYM: return "#$%";
            case CAP: return "ABC";
            case LET: return "abc";
            case NUM: return "123";
            case ACC: return "Áßç";
            case BAK: return "⇦";

            case ' ': return "⊵"; // space

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
        if (yi >= 0 && yi < 3) sQuadrantY = yi * 3;
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
        int qy = sQuadrantY;
        int qx = sQuadrantX;
        return CurrentLayout()[qy+yi][qx+xi];
    }

    private static final int[] NoKey = new int[]{-1,-1};

    /**
     * Returns a keycode at [0] and meta state at [1] for a special key
     */
    public static int[] GetSpecialKey(char c) {
        switch (c){
            case RET: return new int[]{KeyEvent.KEYCODE_ENTER, 0};
            case BAK: return new int[]{KeyEvent.KEYCODE_DEL, 0};

            case ARD: return new int[]{KeyEvent.KEYCODE_DPAD_DOWN, 0};
            case ARU: return new int[]{KeyEvent.KEYCODE_DPAD_UP, 0};
            case ARL: return new int[]{KeyEvent.KEYCODE_DPAD_LEFT, 0};
            case ARR: return new int[]{KeyEvent.KEYCODE_DPAD_RIGHT, 0};

            default: return NoKey;
        }
    }
}
