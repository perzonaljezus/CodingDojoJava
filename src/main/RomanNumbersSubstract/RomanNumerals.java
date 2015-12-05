package RomanNumbersSubstract;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/*
The Romans wrote numbers using letters - I, V, X, L, C, D, M. (notice these letters have lots of straight lines and are hence easy to hack into stone tablets)
The Kata says you should write a function to convert from normal numbers to Roman Numerals: eg
     1 --> I
     10 --> X
     7 --> VII
etc.

There is no need to be able to convert numbers larger than about 3000. (The Romans themselves didn't tend to go any higher)

Note that you can't write numerals like "IM" for 999.
Wikipedia says: Modern Roman numerals ...
are written by expressing each digit separately
starting with the left most digit
and skipping any digit with a value of zero.
To see this in practice, consider the ...
example of 1990. In Roman numerals 1990
is rendered: 1000=M, 900=CM, 90=XC; resulting in MCMXC.
2008 is written as 2000=MM, 8=VIII;
or MMVIII.


German wikipedia
Subtract Algo
 Die Subtraktionsregel in ihrer Normalform besagt,
 dass die Zahlzeichen I, X und C einem ihrer beiden jeweils
 nächstgrößeren Zahlzeichen vorangestellt werden dürfen
 und dann in ihrem Zahlwert von dessen Wert abzuziehen sind:

I vor V oder X: IV (4), IX (9)
X vor L oder C: XL (40), XC (90)
C vor D oder M: CD (400), CM (900)

Zahlzeichen der Fünferbündelung (V, L, D)
werden generell nicht in subtraktiver Stellung
einem größeren Zeichen vorangestellt.
 */
public class RomanNumerals {

    private static final TreeMap<Integer,String> romanNumberToArabic;
    static {
        romanNumberToArabic = new TreeMap<Integer,String>();
        //
        romanNumberToArabic.put(1000,"M");
        romanNumberToArabic.put(500,"D"); // middle between 1000 and 100
        romanNumberToArabic.put(100,"C");
        romanNumberToArabic.put(50,"L"); // middle between 100 and 10
        romanNumberToArabic.put(10,"X");
        romanNumberToArabic.put(5,"V"); // middle between 10 and 1
        romanNumberToArabic.put(1,"I");
    }
    private Map<Integer, RomanNumeralRange> romanNumeralRangeInArabicNumbersByMagnitude;
    // constants: the values are important for convertArabicNumber. It references the proper range by value.
    public static final int ROMAN_NUMERAL_RANGE_1 = 1;
    public static final int ROMAN_NUMERAL_RANGE_2 = 2;
    public static final int ROMAN_NUMERAL_RANGE_3 = 3;
    public static final int ROMAN_NUMERAL_RANGE_4 = 4;


    private String arabicNumberString;
    private int romanMagnitude;

    public RomanNumerals() {

        // the reverse order is important, the algorithm in convertArabicNumber expects it descending!
        romanNumeralRangeInArabicNumbersByMagnitude = new TreeMap<>(Collections.reverseOrder());
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_4, new RomanNumeralRange(10000, 5000, 1000)); // this is the range that covers numbers 1000 - 3000. We could go upto 10000 without any change!
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_3, new RomanNumeralRange(1000, 500, 100));
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_2, new RomanNumeralRange(100, 50, 10));
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_1, new RomanNumeralRange(10, 5, 1));
    }

    // ****
    // main
    // ****
    public String convertArabicNumber(int arabicNumber) throws IllegalArgumentException {

        // contract: we operate only in the specified range
        if( arabicNumber < 1 || arabicNumber > 4000) {
            throw new IllegalArgumentException();
        }

        //convertedArabicNumber will be a String built by adding
        //          thousandsConverted
        //        + hundredsConverted
        //        + tensConverted
        //        + onesConverted
        String convertedArabicNumber = "";

        // loop through the digits of the arabic number and convert each arabic digit into roman number

        String arabicNumberString = new Integer(arabicNumber).toString();

        initRomanMagnitudeCounter(arabicNumberString); // eg. 322 -> 3 digits -> magnitude 3 -> range 1000,500,100; magnitude 2 -> range 1000,500,100

        for(char arabicDigit: arabicNumberString.toCharArray()) {
            convertedArabicNumber += convertArabicDigit(Character.getNumericValue(arabicDigit), getNextRomanNumeralRange());
        }

        return convertedArabicNumber;
    }

    private void initRomanMagnitudeCounter(String arabicNumberString) {
        romanMagnitude = arabicNumberString.length();
    }

    private RomanNumeralRange getNextRomanNumeralRange() {
        return romanNumeralRangeInArabicNumbersByMagnitude.get(romanMagnitude--);
    }

    // ---------------------------------------------------------

    // business logic / domain method names

    // these are only used in test -> if we remove the corresponding tests, we can remove these methods. do we need them?

    /**
     * returns the roman numeral representing "thousands * 1000"
     * e.g. thousands = 2 => 2*1000 => MM
     *
     * @para thousands int
     * @return String
     */
    public String convertThousands(int thousands) {
        RomanNumeralRange b = getBlock1000();
        return convertArabicDigit(thousands, b);
    }

    public String convertHundreds(int hundreds) {
        RomanNumeralRange b = getBlock100();
        return convertArabicDigit(hundreds, b);
    }

    public String convertTens(int tens) {
        RomanNumeralRange b = getBlock10();
        return convertArabicDigit(tens, b);
    }

    public String convertOnes(int ones) {
        RomanNumeralRange b = getBlock1();
        return convertArabicDigit(ones, b);
    }
    // discussion: make these helper methods protected, so that the main useful method is the only public method (convertArabicNumber)
    // or make them private, but then we can't test directly


    // ----------------------------------------------------------

    // non domain -> implementation logic

    // main: most important method, doing the important things!
    private String convertArabicDigit(int arabicDigitValue, RomanNumeralRange romanNumeralRange) { // Robert C. Martin's Clean Code Tip of the Week #10: Avoid Too Many Arguments -> threfore I pass the object

        if (arabicDigitValue == 0) {
            return "";
        }

        // Within a roman numeral range we have 3 explicit points 10, 5 and 1 (= Higher / Middle / Lower limit))
        // that we need to identify respective to the arabicNumber (for the romanMagnitude)
        // and then we can decide wheter to add or subtract from the upper/middle/lower roman numeral
        //
        //  |     |     |
        //  |--a--|--a--|
        //  u     m     l
        // 10     5     1
        //
        // This basic structure repeats itself in order of magnitudes
        //
        //  magnitude 10:   X     V     I  => RomanNumeralRange u = 10/X,    m = 5/V,   l = 1/I
        //  magnitude 100:  C     L     X  => RomanNumeralRange u = 100/C,   m = 50/L,  l = 10/X
        //  magnitude 1000: M     D     C  => RomanNumeralRange u = 10000/M, m = 500/D, l = 100/C

        String arabicNumberConvertedToRomanNumerals = "";

        int amountFromUpperLimit       = 10 - arabicDigitValue; // U - a
        int amountFromMiddleLimitLeft  = arabicDigitValue - 5; // a - M
        int amountFromMiddleLimitRight = 5 - arabicDigitValue; // M - a
        int amountFromLowerLimit       = arabicDigitValue - 1; // a - L

        if( areWeCloserToLowerThanToUpperLimit(arabicDigitValue) ) {
            if( areWeCloserToLowerThanToMiddleLimit(arabicDigitValue) ) {
                arabicNumberConvertedToRomanNumerals = addToLowerLimit(romanNumeralRange, amountFromLowerLimit); // eg add I to I -> II
            } else {
                arabicNumberConvertedToRomanNumerals = subtractFromMiddleLimit(romanNumeralRange, amountFromMiddleLimitRight); // eg sub I from V -> IV
            }
        } else {// WeAreCloserToUpperLimit! ua+1 >= iM: looking from U down to M
            if(areWeCloserToMiddleThanToUpperLimit(arabicDigitValue)) {// we are closer to M than to U
                arabicNumberConvertedToRomanNumerals = addToMiddleLimit(romanNumeralRange, amountFromMiddleLimitLeft); // eg add I to V -> VI
            } else { // WeAreCloserToUpperLimit
                arabicNumberConvertedToRomanNumerals = subtractFromUpperLimit(romanNumeralRange, amountFromUpperLimit); // eg sub I from X -> IX
            }
        }

        return arabicNumberConvertedToRomanNumerals;
    }

    // --- helper: Robert C. Martin's Clean Code Tip #12: Eliminate Boolean Arguments + #2: The Inverse Scope Law of Function Names

    // we are closer to m than to u
    private boolean areWeCloserToMiddleThanToUpperLimit(int arabicDigitValue) {
        return arabicDigitValue <= 8;
    }

    // al <= ma : we are closer to l than to m
    private boolean areWeCloserToLowerThanToMiddleLimit(int arabicDigitValue) {
        return arabicDigitValue <= 3;
    }

    // am <= ma : looking from l upto m
    private boolean areWeCloserToLowerThanToUpperLimit(int arabicDigitValue) {
        return arabicDigitValue <= 5;
    }

    // --- helper: compute methods for adding and subtracting; Robert C. Martin's Clean Code Tip of the Week #2: The Inverse Scope Law of Function Names

    /**
     *
     * @param r RomanNumeralRange
     * @param amount int
     * @return String roman numeral expression for one arabic digit
     */
    private String subtractFromMiddleLimit(RomanNumeralRange r, int amount) {
        String termToPlaceLeftMeaningSubtract = getTermToPlaceLeftOrRightForAddingOrSubtracting(r, amount);
        String baseRomanValue = getArabicNumber(r.middleLimit);
        return termToPlaceLeftMeaningSubtract + baseRomanValue;
    }

    private String subtractFromUpperLimit(RomanNumeralRange r, int amount) {
        String termToPlaceLeftMeaningSubtract = getTermToPlaceLeftOrRightForAddingOrSubtracting(r, amount);
        String baseRomanValue = romanNumberToArabic.get(r.upperLimit);
        return termToPlaceLeftMeaningSubtract + baseRomanValue;
    }

    /**
     *
     * @param r RomanNumeralRange
     * @param amount int
     * @return String roman numeral expression for one arabic digit
     */
    private String addToMiddleLimit(RomanNumeralRange r, int amount) {
        String baseRomanValue = getArabicNumber(r.middleLimit);
        String termToPlaceRightMeaningAdd = getTermToPlaceLeftOrRightForAddingOrSubtracting(r, amount);
        return baseRomanValue + termToPlaceRightMeaningAdd;
    }

    private String getArabicNumber(int limit) {
        return romanNumberToArabic.get(limit);
    }

    private String addToLowerLimit(RomanNumeralRange r, int amount) {
        String baseRomanValue = getArabicNumber(r.lowerLimit);
        String termToPlaceRightMeaningAdd = getTermToPlaceLeftOrRightForAddingOrSubtracting(r, amount);
        return baseRomanValue + termToPlaceRightMeaningAdd;
    }

    /**
     * returns roman value string to place right or left of the base roman value string.
     * return e.g. I for adding in the compound I+V = IV
     * or for adding in the compound V + II = VII
     * @param romanNumeralRange
     * @param amount
     * @return
     */
    private String getTermToPlaceLeftOrRightForAddingOrSubtracting(RomanNumeralRange romanNumeralRange, int amount) {
        String result = "";
        for(int i = 1; i<= amount; i++) {
            result += getArabicNumber(romanNumeralRange.lowerLimit);
        }
        return result;
    }

    // ---

    /**
     * Roman numerals have an interior structure of "blocks" i.e. a range
     *
     * e.g. the 1000 block contains 1000, 500 and 100.
     *
     * Because, if we want to convert an arabic value of 654 we see that 600 is in the upper half
     * of the block (600 is in the range 1000 - 500).
     *
     * 50 is in the block 100, 50, 10 => 50 is the lowest number of the "upper" half i.e. the range 100 - 50.
     *
     * and
     *
     * 4 is in the block 10, 5, 1 => 4 is in the "lower" half of the block.
     *
     * To convert e.g. 321, we see that 300 is in the range 500 - 100, the "lower" half of the block 1000,500,100.
     *
     * In order to compute roman numbers in the subtracting / additive way, we have to know these blocks i.e. romanNumeralRangeInArabicNumbersByMagnitude and the boundaries
     * at which we have to change the base value of the computation.
     *
     * In the example above: 654 has the baseValue 500, and we add 100 to compute the roman representation.
     */
    class RomanNumeralRange {
        int upperLimit;
        int middleLimit;
        int lowerLimit;

        public RomanNumeralRange(int upper, int middle, int lower) {
            this.upperLimit  = upper;
            this.middleLimit = middle;
            this.lowerLimit  = lower;

        }
    }

    private RomanNumeralRange getBlock1000() {
        return this.romanNumeralRangeInArabicNumbersByMagnitude.get(ROMAN_NUMERAL_RANGE_4); // should we rename this to ROMAN_NUMERAL_RANGE_1000?
    }
    private RomanNumeralRange getBlock100() {
        return this.romanNumeralRangeInArabicNumbersByMagnitude.get(ROMAN_NUMERAL_RANGE_3);
    }
    private RomanNumeralRange getBlock10() {
        return this.romanNumeralRangeInArabicNumbersByMagnitude.get(ROMAN_NUMERAL_RANGE_2);
    }
    private RomanNumeralRange getBlock1() {
        return this.romanNumeralRangeInArabicNumbersByMagnitude.get(ROMAN_NUMERAL_RANGE_1);
    }
}

