package RomanNumbersSubstract;

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


german wikipedia
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

    public String convertArabicNumber(int arabic) {
        int i = arabic;

        String result = "";

        int thousands = getThousands(arabic);
        String thousandsConverted = convertThousands(thousands);
        int remainder = arabic - 1000 * thousands;

        int hundreds = getHundreds(remainder);
        String hundredsConverted = convertHundreds(hundreds);
        remainder = remainder - 100 * hundreds;

        int tens = getTens(remainder);
        String tensConverted = convertTens(tens);
        remainder = remainder - 10 * tens;

        int ones = getOnes(remainder);
        String onesConverted = convertOnes(ones);

        return thousandsConverted
                + hundredsConverted
                + tensConverted
                + onesConverted;
    }

    public String convertThousands(int thousands) {
        String result = "";
        for(int i=1; i <= thousands; i++ ) {
            result += "M";
        }
        return result;
    }

    public int getThousands(int i) {
        return getNumber(i, 1000);
    }

    public String convertHundreds(int hundreds) {
        if( hundreds == 0) {
            return "";
        }

        String result = "";
        int distanceFrom1000 = 10 - hundreds;
        int distanceFrom500  =  hundreds - 5;

        if( hundreds >= 5 ) {
            if( distanceFrom1000 == 1 && distanceFrom1000 < distanceFrom500) {
                // subtract from 1000
                result = "";
                for(int i=1; i<= distanceFrom1000; i++) {
                    result += "C";
                }
                result += "M";
            } else {
                // add to 500
                result = "D";
                for(int i=1; i<= distanceFrom500; i++) {
                    result += "C";
                }
            }
        } else {
            distanceFrom500  =  5 - hundreds;
            int distanceFrom100  =  hundreds - 1;
            if( distanceFrom500 < distanceFrom100) {
                // subtract from 500
                result = "";
                for(int i=1; i<= distanceFrom500; i++) {
                    result += "C";
                }
                result += "D";
            } else {
                // add 100s
                result = "C";
                for(int i=1; i<= distanceFrom100; i++) {
                    result += "C";
                }
            }
        }

        return result;
    }

    public int getHundreds(int i) {
        // contract: i must be in the range [1000;3000]
        if(i>=1000 && i<=3000) {
            throw new IllegalArgumentException();
        }
        return getNumber(i, 100);
    }



    public String convertTens(int tens) {
        if( tens == 0) {
            return "";
        }

        String result = "";
        int distanceFrom100 = 10 - tens;
        int distanceFrom50  =  tens - 5;

        if( tens >= 5 ) {
            if( distanceFrom100 == 1 && distanceFrom100 < distanceFrom50) {
                // subtract from 100
                result = "";
                for(int i=1; i<= distanceFrom100; i++) {
                    result += "X";
                }
                result += "C";
            } else {
                // add to 50
                result = "L";
                for(int i=1; i<= distanceFrom50; i++) {
                    result += "X";
                }
            }
        } else {
            distanceFrom50  =  5 - tens;
            int distanceFrom10  =  tens - 1;
            if( distanceFrom50 == 1 && distanceFrom50 < distanceFrom10) {
                // subtract from 50
                result = "";
                for(int i=1; i<= distanceFrom50; i++) {
                    result += "X";
                }
                result += "L";
            } else {
                // add 10s
                result = "X";
                for(int i=1; i<= distanceFrom10; i++) {
                    result += "X";
                }
            }
        }

        return result;
    }

    public int getTens(int i) {
        // contract: i must in the range [99;10]
        if( i<=99 && i>=10) {
            return getNumber(i, 10);
        }
        return 0;
    }


    public String convertOnes(int ones) {
        if( ones == 0) {
            return "";
        }

        String result = "";
        int distanceFrom10 = 10 - ones;
        int distanceFrom5  =  ones -5;

        if( ones >= 5 ) {
            if( distanceFrom10 == 1 && distanceFrom10 < distanceFrom5) {
                // subtract from 10
                result = "";
                for(int i=1; i<= distanceFrom10; i++) {
                    result += "I";
                }
                result += "X";
            } else {
                // add to 5
                result = "V";
                for(int i=1; i<= distanceFrom5; i++) {
                    result += "I";
                }
            }
        } else {
            distanceFrom5  =  5 - ones;
            int distanceFrom1  =  ones - 1;
            if( distanceFrom5 == 1 && distanceFrom5 < distanceFrom10) {
                // subtract from 5
                result = "";
                for(int i=1; i<= distanceFrom5; i++) {
                    result += "I";
                }
                result += "V";
            } else {
                // add 1s
                result = "I";
                for(int i=1; i<= distanceFrom1; i++) {
                    result += "I";
                }
            }
        }

        return result;
    }

    public int getOnes(int i) {
        // contract: i must in the range [9;1]
        if(i<=9 && i>=0) {
            return getNumber(i, 1);
        }
        return 0;
    }




    // -----

    public int getNumber(int i, int place) {
        return (int) i / place;
    }

}
