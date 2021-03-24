package rkr.simplekeyboard.inputmethod.keyboard;

public class KeyboardLayout {

    public static final char nul = '\0';
    public static final char RET = '\n';
    public static final char BAK = '\u0008';

    // mode switches (private use chars)
    public static final char SYM = '\uE001';
    public static final char NUM = '\uE002';
    public static final char CAP = '\uE003';
    public static final char ACC = '\uE004';
    public static final char LET = '\uE005';
    // arrows (private use chars)
    public static final char ARR = '\uE006';
    public static final char ARL = '\uE007';
    public static final char ARU = '\uE008';
    public static final char ARD = '\uE009';
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

    public static String Visualise(char c){
        if (c > 32 && c < 0xE000) return ""+c;

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

}
