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

Since the highest roman literal we can use is M representing 1000, the theoretical maximum we can represent using the algorithm is 3999.
Which we can
 */
public class RomanNumerals {

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
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_4, new RomanNumeralRange(-1, -1, 1000, '-', '.', 'M')); // this is the range that covers numbers 1000 - 3000. We could go upto 10000 without any change!
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_3, new RomanNumeralRange(1000, 500, 100, 'M', 'D', 'C')); // 500 middle between 1000 and 100
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_2, new RomanNumeralRange(100, 50, 10, 'C', 'L', 'X')); // 50 middle between 100 and 10
        romanNumeralRangeInArabicNumbersByMagnitude.put(ROMAN_NUMERAL_RANGE_1, new RomanNumeralRange(10, 5, 1, 'X', 'V', 'I')); // 5 middle between 10 and 1
    }

    public String convertArabicNumber(int arabicNumber) throws IllegalArgumentException {

        // contract: we operate only in the specified range
        if (arabicNumber < 1 || arabicNumber > 3999) {
            throw new IllegalArgumentException();
        }

        //convertedArabicNumber will be a String built by adding
        //          thousandsConverted
        //        + hundredsConverted
        //        + tensConverted
        //        + onesConverted
        String convertedArabicNumber = "";

        String arabicNumberString = Integer.toString(arabicNumber);

        // loop through the digits of the arabic number and convert each arabic digit into roman number

        initRomanMagnitudeCounter(arabicNumberString); // eg. 322 -> 3 digits -> magnitude 3 -> range 1000,500,100; magnitude 2 -> range 1000,500,100
        for (char arabicDigit : arabicNumberString.toCharArray()) {
            convertedArabicNumber += getNextRomanNumeralRange().convertArabicDigit(Character.getNumericValue(arabicDigit));
        }

        return convertedArabicNumber;
    }

    // find out the proper initial romanNumeralRange to use for the specified arabicNumberString
    private void initRomanMagnitudeCounter(String arabicNumberString) {
        romanMagnitude = arabicNumberString.length();
    }

    private RomanNumeralRange getNextRomanNumeralRange() {
        return romanNumeralRangeInArabicNumbersByMagnitude.get(romanMagnitude--);
    }


    // ----------------------------------------------------------

    class RomanNumeralRange {
        int upperLimit;
        int middleLimit;
        int lowerLimit;
        char upperRoman;
        char middleRoman;
        char lowerRoman;

        public RomanNumeralRange(int upper, int middle, int lower, char upperRoman, char middleRoman, char lowerRoman) {
            this.upperLimit = upper;
            this.middleLimit = middle;
            this.lowerLimit = lower;
            this.upperRoman = upperRoman;
            this.middleRoman = middleRoman;
            this.lowerRoman = lowerRoman;
        }

        public String convertArabicDigit(int arabicDigitValue) { // Robert C. Martin's Clean Code Tip of the Week #10: Avoid Too Many Arguments -> threfore I pass the object

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

            int amountFromUpperLimit = 10 - arabicDigitValue; // u - a
            int amountFromMiddleLimitLeft = arabicDigitValue - 5; // a - m
            int amountFromMiddleLimitRight = 5 - arabicDigitValue; // m - a
            int amountFromLowerLimit = arabicDigitValue - 1; // a - l

            if (this.areWeCloserToLowerThanToUpperLimit(arabicDigitValue)) { // we are talking about limits, ut limits are only in the scope of RomanNumeralRange, so this is a "feature envy" that we will have to refactor
                if (this.areWeCloserToLowerThanToMiddleLimit(arabicDigitValue)) {
                    arabicNumberConvertedToRomanNumerals = this.addToLimit(this.lowerRoman, amountFromLowerLimit); // eg add I to I -> II
                } else {
                    arabicNumberConvertedToRomanNumerals = this.subtractFromLimit(this.middleRoman, amountFromMiddleLimitRight); // eg sub I from V -> IV
                }
            } else {// WeAreCloserToUpperLimit! ua+1 >= am: looking from u down to m
                if (this.areWeCloserToMiddleThanToUpperLimit(arabicDigitValue)) {// we are closer to m than to u
                    arabicNumberConvertedToRomanNumerals = this.addToLimit(this.middleRoman, amountFromMiddleLimitLeft); // eg add I to V -> VI
                } else { // WeAreCloserToUpperLimit
                    arabicNumberConvertedToRomanNumerals = this.subtractFromLimit(this.upperRoman, amountFromUpperLimit); // eg sub I from X -> IX
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
         * @param amount int
         * @return String roman numeral expression for one arabic digit
         */
        private String subtractFromLimit(char baseRomanValue, int amount) {
            String termToPlaceLeftMeaningSubtract = getCompoundTerm(amount);
            return termToPlaceLeftMeaningSubtract + baseRomanValue;
        }

        /**
         * @param amount int
         * @return String roman numeral expression for one arabic digit
         */
        private String addToLimit(char baseRomanValue, int amount) {
            String termToPlaceRightMeaningAdd = getCompoundTerm(amount);
            return baseRomanValue + termToPlaceRightMeaningAdd;
        }

        /**
         * returns roman value string to place right or left of the base roman value string.
         * return e.g. I for adding in the compound I+V = IV
         * or for adding in the compound V + II = VII
         *
         * @return
         */
        private String getCompoundTerm(int amount) {
            String result = "";
            for (int i = 1; i <= amount; i++) {
                result += this.lowerRoman;
            }
            return result;
        }

    }
}

