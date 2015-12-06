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

So in order to compute roman numbers in the subtracting / additive way, we have to know these ranges i.e. romanNumeralRangesForArabicNumbersOfLength and the boundaries
at which we have to change the base value of the computation.

The class RomanNumeralRange allows to capture a range like 1000,500,100 into upperLimit = 1000, middleLimit = 500, lowerLimit =100.

It is used by convertArabicNumber.

Since the highest roman literal we can use is M representing 1000, the theoretical maximum we can represent using the algorithm is 3999.
 */
public class RomanNumerals {

    private Map<Integer, RomanNumeralRange> romanNumeralRangesForArabicNumbersOfLength;
    public static final int ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_1 = 1;
    public static final int ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_2 = 2;
    public static final int ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_3 = 3;
    public static final int ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_4 = 4;

    private int romanMagnitude;

    public RomanNumerals() {
        // the reverse order is important, the algorithm in convertArabicNumber expects it descending!
        romanNumeralRangesForArabicNumbersOfLength = new TreeMap<>(Collections.reverseOrder());
        romanNumeralRangesForArabicNumbersOfLength.put(ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_4, new RomanNumeralRange(-1, -1, 1000, '-', '.', 'M')); // this is the range that covers numbers 1000 - 3000. We could go upto 10000 without any change!
        romanNumeralRangesForArabicNumbersOfLength.put(ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_3, new RomanNumeralRange(1000, 500, 100, 'M', 'D', 'C')); // 500 middle between 1000 and 100
        romanNumeralRangesForArabicNumbersOfLength.put(ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_2, new RomanNumeralRange(100, 50, 10, 'C', 'L', 'X')); // 50 middle between 100 and 10
        romanNumeralRangesForArabicNumbersOfLength.put(ROMAN_NUMERAL_RANGE_FOR_ARABIC_DIGITS_OF_LENGTH_1, new RomanNumeralRange(10, 5, 1, 'X', 'V', 'I')); // 5 middle between 10 and 1
    }

    /**
     *
     * @param arabicNumber
     * @return
     * @throws IllegalArgumentException
     */
    public String convertArabicNumber(int arabicNumber) throws IllegalArgumentException {

        if (!isArabicNumerInValidRange(arabicNumber)) {
            throw new IllegalArgumentException();
        }

        String convertedArabicNumber = "";
        String arabicNumberString = "" + arabicNumber;

        // loop through the digits of the arabic number and convert each arabic digit into roman number
        // convertedArabicNumber will be a String built by adding
        //          thousandsConverted
        //        + hundredsConverted
        //        + tensConverted
        //        + onesConverted

        initializeByFindingTheProperRomanNumeralRangeToStartWithForTheSpecifiedArabicNumber(arabicNumberString);
        for (char arabicDigit : arabicNumberString.toCharArray()) {
            convertedArabicNumber += getNextLowerRomanNumeralRangeForArabicDigit().convertArabicDigit(Character.getNumericValue(arabicDigit));
        }

        return convertedArabicNumber;
    }

    private boolean isArabicNumerInValidRange(int arabicNumber) {
        return arabicNumber >= 1 && arabicNumber <= 3999;
    }

    // eg. arabicNumberString "322" -> length = 3 -> "magnitude" 3 -> range 1000,500,100
    private void initializeByFindingTheProperRomanNumeralRangeToStartWithForTheSpecifiedArabicNumber(String arabicNumberString) {
        romanMagnitude = arabicNumberString.length();
    }

    private RomanNumeralRange getNextLowerRomanNumeralRangeForArabicDigit() {
        return romanNumeralRangesForArabicNumbersOfLength.get(romanMagnitude--);
    }

    // ----------------------------------------------------------

    class RomanNumeralRange {
        int upperLimit;
        int middleLimit;
        int lowerLimit;
        char upperRomanBaseValue;
        char middleRomanBaseValue;
        char lowerRomanBaseValue;


        private int distanceFromUpperLimit;
        private int distanceFromMiddleLimit;
        private int distanceFromLowerLimit;
        private int arabicDigitValue;

        private final int arabicDigitUpperLimit = 10;
        private final int arabicDigitMiddleLimit = 5;
        private final int arabicDigitLowerLimit = 1;

        public RomanNumeralRange(int upper, int middle, int lower, char upperRomanBaseValue, char middleRomanBaseValue, char lowerRomanBaseValue) {
            this.upperLimit = upper;
            this.middleLimit = middle;
            this.lowerLimit = lower;
            this.upperRomanBaseValue = upperRomanBaseValue;
            this.middleRomanBaseValue = middleRomanBaseValue;
            this.lowerRomanBaseValue = lowerRomanBaseValue;
        }

        /**
         * An arabicDigitValue is a number in the range 0 ... 10
         *
         * Within a roman numeral range we have 3 explicit points 10, 5 and 1 (= Higher / Middle / Lower limit))
         * that we need to identify respective to the arabicNumber (for the romanMagnitude)
         * and then we can decide wheter to add or subtract from the upper/middle/lower roman numeral
         *
         *  |     |     |
         *  |--a--|--a--|
         *  u     m     l
         * 10     5     1
         *
         * This basic structure repeats itself in order of magnitudes
         *
         *  magnitude 10:   X     V     I  => RomanNumeralRange u = 10/X,    m = 5/V,   l = 1/I
         *  magnitude 100:  C     L     X  => RomanNumeralRange u = 100/C,   m = 50/L,  l = 10/X
         *  magnitude 1000: M     D     C  => RomanNumeralRange u = 10000/M, m = 500/D, l = 100/C
         *
         *  We have to compute the distances of the arabic digit value from the upper/middle/lower limit and decide
         *  when to add or subtract from the base roman value (representing u,m or l).
         *
         * @param arabicDigitValue
         * @return
         */
        public String convertArabicDigit(int arabicDigitValue) {
            this.arabicDigitValue = arabicDigitValue;

            if (arabicDigitValueIsZeroWeDontNeedToConvert()) {
                return "";
            }

            String arabicNumberConvertedToRomanNumerals = "";

            initializeByComputingDistancesOfArabicDigitValueFromLimits();
            
            if (arabicDigitValueIsClosestToTheHigherLimit())
                arabicNumberConvertedToRomanNumerals = subtractFromRomanBaseValue(upperRomanBaseValue, distanceFromUpperLimit);
            else
            if (arabicDigitValueIsBetweenUpperAndMiddleLimit())
                arabicNumberConvertedToRomanNumerals = addToRomanBaseValue(middleRomanBaseValue, distanceFromMiddleLimit);
            else
            if (arabicDigitValueIsBetweenMiddleAndLowerLimit())
                arabicNumberConvertedToRomanNumerals = subtractFromRomanBaseValue(middleRomanBaseValue, distanceFromMiddleLimit);
            else
            //if (arabicDigitValueIsClosestToTheLowerLimit())
                arabicNumberConvertedToRomanNumerals = addToRomanBaseValue(lowerRomanBaseValue, distanceFromLowerLimit);
            //else
            //    throw new IllegalArgumentException("Shouldn't have arrived here!");

            return arabicNumberConvertedToRomanNumerals;
        }

        // Zeros don't need to be converted.
        private boolean arabicDigitValueIsZeroWeDontNeedToConvert() {
            return arabicDigitValue == 0;
        }

        // --- helper: Robert C. Martin's Clean Code Tip #12: Eliminate Boolean Arguments + #2: The Inverse Scope Law of Function Names

        private boolean arabicDigitValueIsBetweenMiddleAndLowerLimit() {
            return arabicDigitValue > 3 && arabicDigitValue <= 5; // I found these boundaries by TDD, see earlier versions of the code. I didn't have to compute these myself.
            // we learn here: there is an inne logic for the computations when converting arabic numbers to roman numbers that automatically ensures that
            // we cannot add or subtract more than 3 atomic roman values from the next bigger/maller value i.e. VIIII is not possible to represent 8.
        }

        private boolean arabicDigitValueIsBetweenUpperAndMiddleLimit() {
            return arabicDigitValue > 5 && arabicDigitValue <= 8;
        }

        //private boolean arabicDigitValueIsClosestToTheLowerLimit() {
        //    return arabicDigitValue <= 3;
        //}

        private boolean arabicDigitValueIsClosestToTheHigherLimit() {
            return arabicDigitValue > 8;
        }

        private void initializeByComputingDistancesOfArabicDigitValueFromLimits() {
            distanceFromUpperLimit = Math.abs(arabicDigitUpperLimit - arabicDigitValue);
            distanceFromMiddleLimit = Math.abs(arabicDigitMiddleLimit - arabicDigitValue);
            distanceFromLowerLimit = Math.abs(arabicDigitLowerLimit - arabicDigitValue);
        }

        // --- helper: compute methods for adding and subtracting; Robert C. Martin's Clean Code Tip of the Week #2: The Inverse Scope Law of Function Names

        /**
         * eg sub I from V -> IV
         * eg sub I from X -> IX
         *
         * @param amount int
         * @return String roman numeral expression for one arabic digit
         */
        private String subtractFromRomanBaseValue(char baseRomanValue, int amount) {
            String termToPlaceLeftOfBaseValueMeaningSubtract = getCompoundTerm(amount);
            return termToPlaceLeftOfBaseValueMeaningSubtract + baseRomanValue;
        }

        /**
         * eg add I to V -> VI
         * eg add I to I -> II
         *
         * @param amount int
         * @return String roman numeral expression for one arabic digit
         */
        private String addToRomanBaseValue(char baseRomanValue, int amount) {
            String termToPlaceRightFromBseValueMeaningAdd = getCompoundTerm(amount);
            return baseRomanValue + termToPlaceRightFromBseValueMeaningAdd;
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
                result += lowerRomanBaseValue;
            }
            return result;
        }

    }
}

