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


My analysis during TDD:

Roman numerals are constructed by placing a compund term left or right of a base value.
Base Values are I,V,X,L,C,D,M.

The compound term consists of an atomic value repeated specified times.

The atomic values that are allowed depend on the base value.

The rules for this are
I before V or X: IV (4), IX (9)
X before L or C: XL (40), XC (90)
C before D or M: CD (400), CM (900)

Rule for placing left or right:

e.g.
7: = baseValue 5 = V, compoundTerm I repeated 2x = II, place right => VII
9: baseValue 10 = X, compundTerm I repeated 1x = I, place left => IX
40: baseValue 10 = L, compoundTerm X repeated 1x, place left => XL

The computation of Roman numerals have an interior structure that depend on the next lesser / bigger value.
This can be captured by defining "blocks" i.e. a ranges and setting upper, middle and lower limits of the range in arabic value terms.

e.g. the M = 1000 range contains 1000, 500 and 100.

Because, if we want to convert an arabic value of 654

we see that 600 is in the upper half of the range

600 is closer to the middle limit than the lower limit of the range.

so to represent 600 we have to add an amount to the middle limit D.
The amount consists of repeated lower limit values i.e. 100 = C.
We have to add C one time
resulting in DC for 600.

50 is in the range 100, 50, 10 => 50 is the lowest number of the "upper" half i.e. the range 100 - 50.
It is exactly the middle limit of the range, so the amount to add or subtract is 0
resulting in L.

and

4 is in the range 10, 5, 1 => 4 is in the "lower" half of the range.
4 is closer to the middle limit 5, meaning we have to subtract from the middle limit
resulting in IV.

And then we need to compile all the sub expressions for 654
resulting in DC L IV = DCLIV.

So in order to compute roman numbers in the subtracting / additive way, we have to know these ranges i.e. romanNumeralRangeInArabicNumbersByMagnitude and the boundaries
at which we have to change the base value of the computation.

The class RomanNumeralRange allows to capture a range like 1000,500,100 into upperLimit = 1000, middleLimit = 500, lowerLimit =100.

It is used by convertArabicNumber.
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
    private int amount;
    private RomanNumeralRange romanNumeralRange;

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
        return romanNumeralRange = romanNumeralRangeInArabicNumbersByMagnitude.get(romanMagnitude--);
    }


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
        this.romanNumeralRange = romanNumeralRange;

        int amountFromUpperLimit       = 10 - arabicDigitValue; // u - a
        int amountFromMiddleLimitLeft  = arabicDigitValue - 5; // a - m
        int amountFromMiddleLimitRight = 5 - arabicDigitValue; // m - a
        int amountFromLowerLimit       = arabicDigitValue - 1; // a - l

        if( areWeCloserToLowerThanToUpperLimit(arabicDigitValue) ) { // we are talking about limits, ut limits are only in the scope of RomanNumeralRange, so this is a "feature envy" that we will have to refactor
            if( areWeCloserToLowerThanToMiddleLimit(arabicDigitValue) ) {
                arabicNumberConvertedToRomanNumerals = addToLowerLimit(amountFromLowerLimit); // eg add I to I -> II
            } else {
                arabicNumberConvertedToRomanNumerals = subtractFromMiddleLimit(amountFromMiddleLimitRight); // eg sub I from V -> IV
            }
        } else {// WeAreCloserToUpperLimit! ua+1 >= am: looking from u down to m
            if(areWeCloserToMiddleThanToUpperLimit(arabicDigitValue)) {// we are closer to m than to u
                arabicNumberConvertedToRomanNumerals = addToMiddleLimit(amountFromMiddleLimitLeft); // eg add I to V -> VI
            } else { // WeAreCloserToUpperLimit
                arabicNumberConvertedToRomanNumerals = subtractFromUpperLimit(amountFromUpperLimit); // eg sub I from X -> IX
            }
        }

        return arabicNumberConvertedToRomanNumerals;
    }

    // --- helper: Robert C. Martin's Clean Code Tip #12: Eliminate Boolean Arguments + #2: The Inverse Scope Law of Function Names

    // we are closer to m than to u
    private boolean areWeCloserToMiddleThanToUpperLimit(int arabicDigitValue) {
        return arabicDigitValue <= 8; // we learn here: there is an inne logic for the computations when converting arabic numbers to roman numbers that automatically ensures that we cannot add or subtract more than 3 atomic roman values from the next biiger/maller value i.e. VIIII is not possible to represent 8.
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
     * @param amount int
     * @return String roman numeral expression for one arabic digit
     */
    private String subtractFromMiddleLimit(int amount) {
        this.amount = amount;
        String termToPlaceLeftMeaningSubtract = getCompoundTerm();
        String baseRomanValue = getArabicNumberOnRangeLimit(this.romanNumeralRange.middleLimit);
        return termToPlaceLeftMeaningSubtract + baseRomanValue;
    }

    private String subtractFromUpperLimit(int amount) {
        this.amount = amount;
        String termToPlaceLeftMeaningSubtract = getCompoundTerm();
        String baseRomanValue = romanNumberToArabic.get(this.romanNumeralRange.upperLimit);
        return termToPlaceLeftMeaningSubtract + baseRomanValue;
    }

    /**
     *
     * @param amount int
     * @return String roman numeral expression for one arabic digit
     */
    private String addToMiddleLimit(int amount) {
        this.amount = amount;
        String baseRomanValue = getArabicNumberOnRangeLimit(this.romanNumeralRange.middleLimit);
        String termToPlaceRightMeaningAdd = getCompoundTerm();
        return baseRomanValue + termToPlaceRightMeaningAdd;
    }
    private String addToLowerLimit(int amount) {
        this.amount = amount;
        String baseRomanValue = getArabicNumberOnRangeLimit(this.romanNumeralRange.lowerLimit);
        String termToPlaceRightMeaningAdd = getCompoundTerm();
        return baseRomanValue + termToPlaceRightMeaningAdd;
    }

    /**
     * returns roman value string to place right or left of the base roman value string.
     * return e.g. I for adding in the compound I+V = IV
     * or for adding in the compound V + II = VII
     * @return
     */
    private String getCompoundTerm() {
        String result = "";
        for(int i = 1; i<= this.amount; i++) {
            result += getArabicNumberOnRangeLimit(this.romanNumeralRange.lowerLimit);
        }
        return result;
    }


    private String getArabicNumberOnRangeLimit(int limit) {
        return romanNumberToArabic.get(limit);
    }


    // ---
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
}

