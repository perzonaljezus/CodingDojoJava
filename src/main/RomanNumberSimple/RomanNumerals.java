package RomanNumberSimple;

import java.util.*;

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


german wikipedia
Simple Algo
mit den großen römischen Ziffern beginnend,
deren Wert so häufig wie möglich von der umzurechnenden Zahl abzuziehen
und die römischen Ziffern dabei zu notieren.
 */
class RomanNumerals {
    TreeMap<Integer,String> romanNumbers = new TreeMap<Integer,String>();
    public RomanNumerals() {
        romanNumbers.put(1000,"M");
        romanNumbers.put(500,"D");
        romanNumbers.put(100,"C");
        romanNumbers.put(50,"L");
        romanNumbers.put(10,"X");
        romanNumbers.put(5,"V");
        romanNumbers.put(1,"I");
    }
    Map<Integer,String> descendingRomanNumbers = romanNumbers.descendingMap();

    public String convertArabic(int arabic) {
        int i = arabic;

        String result = "";
        while (i > 0) {
            for (Map.Entry<Integer, String> entry : descendingRomanNumbers.entrySet()) {
                Integer arabicCurr = entry.getKey();
                String romanValue = entry.getValue();
                String currTranslated = romanValue.toString();
                int subtractValue = 0;
                int ii = i;
                while (ii >= arabicCurr) {
                    result += currTranslated;
                    ii -= arabicCurr;
                    subtractValue += arabicCurr;
                }
                i -= subtractValue;
            }
        }

        return result;
    }
}
