package vizix.test.decoder;

import java.util.HashMap;

public class DecodeGTIN128 {
    public static int bDecoding = 1;
    public String cxHeader_Text = null;
    public String laHeaderHex_Text = null;
    public String laHeaderShift_Text = null;
    public long lgtin = 0;


    public String cxGTIN_Text = null;
    public String laGTINHex = null;
    public String laGTINShift = null;
    public String cxLotNum_Text = null;
    public String laLotHex_Text = null;
    public String laLotShift_Text = null;
    public String laRFIDOut_Text = null;
    public String cxEPC_Text = null;
    public String cx128In_Text = null;

    public int cxDateType_SelectedIndex = 0;
    public String laDateValueHex_Text = null;
    public String laDateValueShift_Text = null;
    public String cxDate_Text = null;
    public String laDateTypeHex_Text = null;
    public String laDateTypeShift_Text = null;

    public String friendlyDate = null;

    public static HashMap<Integer, String> cxDateType_Text = new HashMap<>();
    public String la128HR_Text = null;

    public String cxQrIn_Text = null;
    public String cxQRHead_Text = null;
    public String cxSerNum_Text = null;
    static {
        cxDateType_Text.put(0,"(11) Production");
        cxDateType_Text.put(1,"(13) Packing");
        cxDateType_Text.put(2,"(15) Best before");
        cxDateType_Text.put(3,"(17) Expiration");
    }

    public String getFriendlyDate() {
        return friendlyDate;
    }

    public String getCxGTIN_Text() {
        return cxGTIN_Text;
    }

    public String getCxLotNum_Text() {
        return cxLotNum_Text;
    }

    public String getCxDate_Text() {
        return cxDate_Text;
    }

    public int getCxDateType_SelectedIndex() {
        return cxDateType_SelectedIndex;
    }

    public void setCxDateType_SelectedIndex(int cxDateType_SelectedIndex) {
        this.cxDateType_SelectedIndex = cxDateType_SelectedIndex;
    }

    public static HashMap<Integer, String> getCxDateType_Text() {
        return cxDateType_Text;
    }

    public String getCx128In_Text() {
        return cx128In_Text;
    }


    // Decode rfid string into components
    public void bnDecodeClick(String cxEPC)
    {
        String str, str2, sgtin;
        int c0;
        long val, valgtin;
        long lot;
        String szeroes = "00000000000000";  // 14 Digits
        int bDecoding = 1;

        // Check string length
        str = cxEPC;
        str2 = "";
        if (str.length() != 32)
        {
            System.out.println("Invalid EPC length. Needs 32 hex digits.");
            return;
        }
        // Check for hex characters
        for (int i = 0; i < 32; i++)
        {
            c0 = str.charAt(i);
            if ((c0 >= 'a') && (c0 <= 'f')) {
                c0 = Character.toUpperCase(c0);
            }
            if (((c0 >= '0') && (c0 <= '9'))
                    || ((c0 >= 'A') && (c0 <= 'F'))) {
                str2 = str2 + (char)c0;
            } else {
                System.out.println("Invalid hex character.");
                return;
            }
        }

        // ------Decode components
        // Header
        cxHeader_Text = str2.substring(0, 2);
        // GTIN
        int iniPosition = 2;
        int endPosition = iniPosition + 12 ;
        String str2Substring = str2.substring(iniPosition, endPosition);
        val = Long.parseUnsignedLong(str2Substring, 16);
        valgtin = val / 4;
        int _14MinussgtinLength =  14 - (valgtin+"").length();
        iniPosition = 0;
        endPosition = iniPosition + _14MinussgtinLength;
        String cad = szeroes.substring(iniPosition,endPosition);
        sgtin = cad + valgtin;
        cxGTIN_Text = sgtin;

        valgtin = checkGTIN();
        if (valgtin < 0)
        {
            System.out.println("Error in CheckGTIN");
            return;    // Error
        } else {
            cvtGTIN();
            if (bDecoding == 0) {
                System.out.println("genCheckDigit");
                genChkDigit(cxGTIN_Text);
            }
        }

        //Date Type
        cxDateType_SelectedIndex = (int)(val & 0x03);

        // Date value
        iniPosition = 14;
        endPosition = iniPosition + 4 ;
        val = Long.parseUnsignedLong(str2.substring(14, endPosition), 16);
        if (decodeDate((int)(val / 0x8)) < 0){
            System.out.println("Error at the moment to decode the Date.");
            return;    // Error
        }

        // Lot
        iniPosition = 17;
        endPosition = iniPosition + 3 ;
        val = Long.parseUnsignedLong(str2.substring(iniPosition, endPosition), 16);
        lot = (int)(val & 0x7FF);
        if (lot >= 1600)
        {
            System.out.println("Lot number encoded incorrectly.");
            return;    // Error
        }
        str = "";
        str = str + R40Decode(lot / 40);
        lot = lot - (lot / 40) * 40;
        str = str + R40Decode(lot);
        for (int i = 0; i < 3; i++)
        {
            iniPosition = 20 + 4 * i;
            endPosition = iniPosition + 4;
            String lotstr2 = str2.substring(iniPosition, endPosition);
            lot = Long.parseUnsignedLong(lotstr2, 16);
            if (lot >= 64000)
            {
                System.out.println("Lot number encoded incorrectly.");
                return;    // Error
            }
            if(lot == 0){
                break;
            }
            str = str + R40Decode(lot / 1600);
            lot = lot - (lot / 1600) * 1600;
            str = str + R40Decode(lot / 40);
            lot = lot - (lot / 40) * 40;
            str = str + R40Decode(lot);
        }
        cxLotNum_Text = str;
        myChanged();
    }

    public long checkGTIN()
    // Check the GTIN format, including check digit
    {
        String str;
        str = cxGTIN_Text;
        if (str.length() == 13)
        {
            str = str + ""+genChkDigit(str);
        } else if (str.length() != 14) {
            System.out.println("GTIN must be 14 digits, including checkdigit.");
            lgtin = 0;
            return -1L;
        }
        try{
            long mylgtin = Long.parseLong(str);
            if (mylgtin < 0) {
                System.out.println("GTIN must be 14 digits, including checkdigit.");
                lgtin = 0;
                return -2L;
            } else {
                lgtin = mylgtin;
                return lgtin;
            }
        } catch(NumberFormatException ne){
            System.out.println("GTIN must be 14 digits, with checksum.");
            lgtin = 0;
            return (-4L);
        }
    }

    public int genChkDigit(String instr)
    // Returns check digit for input string
    // The final digit of a Universal Product Code is a check digit computed as follows:
    // Add the digits in the odd-numbered positions from the right(first, third, fifth, etc.
    //   -not including the check digit) together and multiply by three.
    // Add the digits(up to but not including the check digit) in the even-numbered
    //   positions(second, fourth, sixth, etc.) to the result.
    // Take the remainder of the result divided by 10(modulo operation).
    //   If the remainder is equal to 0 then use 0 as the check digit,
    //   and if not 0 subtract the remainder from 10 to derive the check digit.
    {
        String cxGTIN = null;
        int sumOdd = 0;
        int sumEven = 0;
        int sum = 0;
        int chkDigIn = -1;
        int chkDigCalc = 0;

        if (instr.length() == 14) {
            chkDigIn = instr.charAt(13) - '0';
            instr = instr.substring(0, 13);    // Strip off input chk digit
        }
        if (instr.length() != 13) {   // Check for valid length
            return -1;
        }
        for (int i = 0; i < 7; i++) {
            sumOdd += instr.charAt(2 * i) - '0';
        }
        for (int i = 0; i < 6; i++) {
            sumEven += instr.charAt((2 * i) + 1) - '0';
        }
        chkDigCalc = ((3 * sumOdd) + sumEven) % 10;
        if (chkDigCalc > 0) {
            chkDigCalc = 10 - chkDigCalc;
        }
        if (bDecoding == 0)  {  // If not decoding
            if (chkDigCalc != chkDigIn){
                System.out.println("Check digit should be " + chkDigCalc);
            }
        } else {
            cxGTIN = instr +""+ chkDigCalc;
            System.out.println("GTIN + checkdigit " + cxGTIN);
        }
        return sum;
    }

    // Encode the GTIN, including check digit, in straight binary
    public void cvtGTIN()
    {
        String szeroes = "000000000000";
        String sgtin = Long.toHexString(lgtin);
        int iniPosition = 0;
        int endPosition = iniPosition + (12 - sgtin.length()) ;
        laGTINHex = szeroes.substring(iniPosition, endPosition) + sgtin;
        sgtin = Long.toHexString(0x4 * lgtin);    // Shift left 2 bits
        endPosition = iniPosition + (12 - sgtin.length()) ;
        laGTINShift = szeroes.substring(iniPosition, endPosition) + sgtin;
    }

    int decodeDate(int date)
    // See above for encoding scheme
    {
        int year, month, day;
        //String sdate = cxDate.Text; //RRCC
        String str;
        //System.out.println("DATE: " + date);
        year = date / 0x200;
        month = (date & 0x1E0) / 0x20;
        day = date & 0x1F;
        // check values
        if (year > 9) {
            System.out.println("Invalid year format. (YMMDD, Year 0..9)");
            return -1;
        }
        str = String.valueOf(year);
        if ((month < 1) || (month > 12)) {
            System.out.println("Invalid month format. (YMMDD, Month 1..12)");
            return -2;
        }
        if (month > 9) {
            str = str + month;
        } else {
            str = str + "0" + month;
        }
        if ((day < 1) || (day > 31)) {
            System.out.println("Invalid day format. (YMMDD, Day 1..31)");
            return -2;
        }
        if (day > 9) {
            str = str + day;
        } else {
            str = str + "0" + day;
        }
        cxDate_Text = str;
        return 0;
    }

    // Lot number encoding/decoding
    // The (up to) 11-character upper-case alphanumeric lot number is encoded in
    // radix-40 from the following table. The encoding is in triplets, starting
    // from the right. The first two characters (from left) are encoded together.
    //
    // Table 1 – Radix-40 Character Set
    // Symbol Name              ASCII hex   Binary      Code
    //  PAD   						0
    //  A   Capital letter A		41   	01000001	1
    //  B   Capital letter B  		42 	    01000010  	2
    //  C   Capital letter C  		43 	    01000011  	3
    //  D   Capital letter D      	44      01000100  	4
    //  E   Capital letter E  		45      01000101  	5
    //  F   Capital letter F  		46      01000110  	6
    //  G   Capital letter G      	47      01000111  	7
    //  H   Capital letter H      	48      01001000  	8
    //  I   Capital letter I  		49      01001001  	9
    //  J   Capital letter J  		4A      01001010  	10
    //  K   Capital letter K  		4B      01001011  	11
    //  L   Capital letter L  		4C  	01001100  	12
    //  M   Capital letter M      	4D  	01001101  	13
    //  N   Capital letter N      	4E  	01001110  	14
    //  O   Capital letter O      	4F  	01001111  	15
    //  P   Capital letter P 		50  	01010000  	16
    //  Q   Capital letter Q     	51  	01010001  	17
    //  R   Capital letter R  		52  	01010010  	18
    //  S   Capital letter S  		53  	01010011  	19
    //  T   Capital letter T 		54  	01010100  	20
    //  U   Capital letter U     	55  	01010101  	21
    //  V   Capital letter V  		56  	01010110  	22
    //  W   Capital letter W     	57  	01010111  	23
    //  X   Capital letter X  		58  	01011000  	24
    //  Y   Capital letter Y  		59  	01011001  	25
    //  Z   Capital letter Z  		5A  	01011011 	26
    //  – 	Hyphen-Minus     		2D  	00101101  	27
    //  .   Full stop       		2E  	00101110 	28
    //  : 	Colon        			3A	    00101110 	29
    //  0 	Digit zero          	30  	00110000  	30
    //  1 	Digit one         		31  	00110001  	31
    //  2 	Digit two         		32  	00110010  	32
    //  3 	Digit three     		33      00110011  	33
    //  4 	Digit four       		34  	00110100  	34
    //  5 	Digit five       		35  	00110101  	35
    //  6 	Digit six           	36  	00110110  	36
    //  7 	Digit seven     		37  	00110111  	37
    //  8 	Digit eight     		38  	00111000  	38
    //  9 	Digit nine       		39  	00111001  	39
    //
    Character R40Decode(long idx)
    // Returns the character associated with the index idx in radix-40 table
    {
        if (idx == 0)
            return null;
        else if ((idx >= 1) && (idx <= 26))
            return (char)('A' + (idx - 1));
        else if (idx == 27)
            return '-';
        else if (idx == 28)
            return '.';
        else if (idx == 29)
            return ':';
        else if ((idx >= 30) && (idx <= 39))
            return (char)('0' + (idx - 30));
        else
        {
            System.out.println("Internal decoder error.");
            return '!';
        }
    }

    void myChanged()
    // Respond to any change in input by reencoding rfid stream
    {
        long lgtin;
        int chkError = 0;

        //ClearErrors();
        if (CheckHeader() < 0){
            chkError = -1;
        }
        if ((lgtin = CheckGTIN()) < 0){
            chkError = -2;
        } else {
            cvtGTIN();
            if (bDecoding == 0){
                genChkDigit(cxGTIN_Text);
            }
        }
        CheckDateType();    // No possible error
        if (CheckDate() < 0){
            chkError = -3;
        }
        if (CheckLot() < 0){
            chkError = -4;
        }
        if (chkError >= 0)    // If there were no errors
        {
            SumItUp();
            if (bDecoding == 1)
            {
                // See if rfid and encoded strings match
                int lenE = cxEPC_Text.length();
                int lenR = laRFIDOut_Text.length();
                if (lenR == lenE)
                {
                    for (int i = 0; i < lenE; i++)
                    {
                        if (cxEPC_Text.charAt(i) != laRFIDOut_Text.charAt(i))
                        {
                            System.out.println("Error in decoding.");
                            return;    // Error
                        }
                    }
                }
                else
                {
                    System.out.println("Decoded length does not match.");
                    return;    // Error
                }
            }
            //drawBitmaps();
        }
    }

    int CheckHeader()
    // Eight bits are reserved for an identifying header
    {
        String str, str2;
        int c0, c1;

        str = cxHeader_Text;
        str2 = "";
        if (str.length() != 2)
        {
            System.out.println("Invalid header. Needs 2 hex digits.");
            return (-1);
        }
        c0 = str.charAt(0);
        c1 = str.charAt(1);
        if ((c0 >= 'a') && (c0 <= 'f')){
            c0 = Character.toUpperCase(c0);
        }
        if ((c1 >= 'a') && (c1 <= 'f')){
            c1 = Character.toUpperCase(c1);
        }
        if (((c0 >= '0') && (c0 <= '9'))
                || ((c0 >= 'A') && (c0 <= 'F')))
        {
            str2 = str2 + (char)c0;
            if (((c1 >= '0') && (c1 <= '9'))
                    || ((c1 >= 'A') && (c1 <= 'F')))
            {
                str2 = str2 + (char)c1;
                laHeaderHex_Text = str2;
                laHeaderShift_Text = str2;
                return 0;
            }
        }
        System.out.println("Invalid header. Needs 2 hex digits.");
        return (-2);
    }
    long CheckGTIN()
    // Check the GTIN format, including check digit
    {
        String str;
        str = cxGTIN_Text;
        if (str.length() == 13){
            str = str + "" + genChkDigit(str);
        } else if (str.length() != 14) {
            System.out.println("GTIN must be 14 digits, including checkdigit.");
            lgtin = 0;
            return (-1);
        }
        try{
            long mylgtin = Long.parseLong(str);
            if (mylgtin < 0) {
                System.out.println("GTIN must be 14 digits, including checkdigit.");
                lgtin = 0;
                return -2L;
            } else {
                lgtin = mylgtin;
                return lgtin;
            }
        } catch(NumberFormatException ne){
            System.out.println("GTIN must be 14 digits, with checksum.");
            lgtin = 0;
            return (-4L);
        }
    }

    void CheckDateType()
    // Get date type index from table
    {
        laDateTypeHex_Text = Integer.toHexString(cxDateType_SelectedIndex);
        laDateTypeShift_Text = laDateTypeHex_Text;
    }

    long CheckDate()
    // Check the date format. The decial value displayed in the combo box is YMMDD.
    // This is converted to binary as follows:
    //     4 bits for last digit of year
    //     4 bits for month
    //     5 bits for day of month
    {
        String szeroes = "0000";
        String sdate = cxDate_Text;
        int result;
        try{
            Long.parseLong(sdate);
            if (sdate.length() == 5)
            {
                int iyear = Integer.parseInt(sdate.substring(0, 0 + 1), 10);
                int imonth = Integer.parseInt(sdate.substring(1, 1 + 2), 10);
                if ((imonth < 1) || (imonth > 12))
                {
                    //cxDate.ForeColor = Color.Red;
                    //PostErrorMsg("Invalid date format. (YMMDD, Month 1..12)");
                    System.out.println("Invalid date format. (YMMDD, Month 1..12)");
                    return -2;
                }
                int iday = Integer.parseInt(sdate.substring(3, 3 + 2), 10);
                switch (imonth)
                {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        if ((iday < 1) || (iday > 31))
                        {
                            //cxDate.ForeColor = Color.Red;
                            //PostErrorMsg("Invalid date format. (YMMDD, Day 1..31)");
                            System.out.println("Invalid date format. (YMMDD, Day 1..31)");
                            return -2;
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        if ((iday < 1) || (iday > 30))
                        {
                            //cxDate.ForeColor = Color.Red;
                            //PostErrorMsg("Invalid date format. (YMMDD, Day 1..30)");
                            System.out.println("Invalid date format. (YMMDD, Day 1..30)");
                            return -2;
                        }
                        break;
                    case 2:
                        if ((iday < 1) || (iday > 29))
                        {
                            //cxDate.ForeColor = Color.Red;
                            //PostErrorMsg("Invalid date format. (YMMDD, Day 1..29)");
                            System.out.println("Invalid date format. (YMMDD, Day 1..29)");
                            return -2;
                        }
                        break;
                    default:
                        //cxDate.ForeColor = Color.Red;
                        //PostErrorMsg("Invalid date format. (YMMDD)");    // Should never get here
                        System.out.println("Invalid date format. (YMMDD)");
                        return -2;
                }
                int ipackeddate = 0x200 * iyear + 0x20 * imonth + iday;
                //String sDateX = ipackeddate.ToString("X");
                String sDateX = Long.toHexString(ipackeddate);
                laDateValueHex_Text = szeroes.substring(0, 4 - sDateX.length()) + sDateX;
                ipackeddate *= 0x8;    // Shift left 3 bits
                //sDateX = ipackeddate.ToString("X");
                sDateX = Long.toHexString(ipackeddate);
                laDateValueShift_Text = szeroes.substring(0, 4 - sDateX.length()) + sDateX;
            }
        }catch(NumberFormatException e ){
            //cxDate.ForeColor = Color.Red;
            //PostErrorMsg("Invalid date format. (YMMDD, all numbers)");
            System.out.println("Invalid date format. (YMMDD)");
            return -2;
        }
        return 0;
    }

    long CheckLot()
    {
        String str;
        String spad = "           "; //11 spaces
        String szeroes = "0000";

        str = cxLotNum_Text;
        int len = str.length();
        if (len > 11) {
            System.out.println("Max 11 characters. (0-9, A-Z, '-', '.', ':', ' ')");
            return (-1);
        }
        if (len < 11){
            str = str + spad.substring(0, 11 - len);    // Pad on right with spaces
        }
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            // Test to see if character valid
            if (((c >= 'A') && (c <= 'Z'))
                    || ((c >= '0') && (c <= '9'))
                    || (c == '-') || (c == '.') || (c == ':') || (c == ' ')) {
                continue;
            } else {
                System.out.println("Invalid lot character. (0-9, A-Z, '-', '.', ':', ' ')");
                return -2;
            }
        }
        // Characters are encoded in blocks of three using base (radix) 40.
        // The first group contains only 2 characters.
        String s0, s1, s2, s3;
        int i0, i1, i2, i3;
        i0 = 40 * R40Encode(str.charAt(0)) + R40Encode(str.charAt(1));
        i1 = 1600 * R40Encode(str.charAt(2)) + 40 * R40Encode(str.charAt(3)) + R40Encode(str.charAt(4));
        i2 = 1600 * R40Encode(str.charAt(5)) + 40 * R40Encode(str.charAt(6)) + R40Encode(str.charAt(7));
        i3 = 1600 * R40Encode(str.charAt(8)) + 40 * R40Encode(str.charAt(9)) + R40Encode(str.charAt(10));
        s0 = Integer.toHexString(i0);
        s0 = szeroes.substring(0, 3 - s0.length()) + s0;    // Pad on left with zeroes
        s1 = Integer.toHexString(i1);
        s1 = szeroes.substring(0, 4 - s1.length()) + s1;    // Pad on left with zeroes
        s2 = Integer.toHexString(i2);
        s2 = szeroes.substring(0, 4 - s2.length()) + s2;    // Pad on left with zeroes
        s3 = Integer.toHexString(i3);
        s3 = szeroes.substring(0, 4 - s3.length()) + s3;    // Pad on left with zeroes
        laLotHex_Text = s0 + " " + s1 + " " + s2 + " " + s3;
        laLotShift_Text = s0 + s1 + s2 + s3;
        return 0;
    }

    int R40Encode(char c)
    // Returns the index of c in radix-40 table
    {
        if (c == ' ') {    // Pad character. Actually, should be a null.
            return 0;
        } else if ((c >= 'A') && (c <= 'Z')) {
            return 1 + c - 'A';
        } else if (c == '-') {
            return 27;
        } else if (c == '.') {
            return 28;
        } else if (c == ':') {
            return 29;
        } else if ((c >= '0') && (c <= '9')) {
            return 30 + c - '0';
        } else {
            return -1;    // Should be unreachable
        }
    }

    void SumItUp()
    // Concatinate the individual shifted binary strings to make final rfid string
    {
        String str;
        long val;
        String szeroes = "000000000000";

        str = laHeaderShift_Text;
        val = 4 * lgtin;     // Shift left 2 bits
        int idx = cxDateType_SelectedIndex;
        if (idx < 0){
            idx = 0;
        } else if (idx > 3) {
            idx = 3;
        }
        val += idx;
        String sgtindt = Long.toHexString(val);
        str = str + szeroes.substring(0, 12 - sgtindt.length()) + sgtindt;
        str = str + laDateValueShift_Text.substring(0, 3);
        int iniPosition = 3;
        int endPosition = iniPosition + 1 ;
        val = Long.parseUnsignedLong(laDateValueShift_Text.substring(iniPosition, endPosition), 16);
        iniPosition = 3;
        endPosition = iniPosition + 1 ;
        val = Long.parseUnsignedLong(laDateValueShift_Text.substring(iniPosition, endPosition), 16);
        iniPosition = 0;
        endPosition = iniPosition + 1 ;
        val = val + Long.parseUnsignedLong(laLotShift_Text.substring(iniPosition, endPosition), 16);
        str = str + Long.toHexString(val);
        iniPosition = 1;
        endPosition = iniPosition + 14 ;
        str = str + laLotShift_Text.substring(iniPosition, endPosition);
        laRFIDOut_Text = str;
        cxEPC_Text = str;
        iniPosition = 1;
        endPosition = iniPosition + 2 ;
        cx128In_Text = "01" + cxGTIN_Text + cxDateType_Text.get(cxDateType_SelectedIndex).substring(iniPosition, endPosition) + "2" + cxDate_Text + "10" + cxLotNum_Text;
        la128HR_Text = "(01) " + cxGTIN_Text + " (" + cxDateType_Text.get(cxDateType_SelectedIndex).substring(iniPosition, endPosition) + ") 2" + cxDate_Text + " (10) " + cxLotNum_Text;
        cxQrIn_Text = cxQRHead_Text + "/01/" + cxGTIN_Text + "/" + cxDateType_Text.get(cxDateType_SelectedIndex).substring(iniPosition, endPosition) + "/2" + cxDate_Text + "/10/" + cxLotNum_Text
                + "/21/" + cxSerNum_Text;
        friendlyDate = cxDate_Text.substring(1, 3) +"/"+cxDate_Text.substring(3, 5)+"/202"+cxDate_Text.substring(0,1);
    }

    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer("");
        buffer.append("lgtin: "+this.lgtin+"\n");
        buffer.append("laGTINHex: "+this.laGTINHex+"\n");
        buffer.append("GTIN_Text: "+this.cxGTIN_Text +"\n");
        buffer.append("laGTINShift: "+this.laGTINShift+"\n");
        buffer.append("header: "+this.cxHeader_Text +"\n");
        buffer.append("cxDateType_SelectedIndex: "+this.cxDateType_SelectedIndex +"\n");
        buffer.append("cxDate_Text: "+this.cxDate_Text+"\n");
        buffer.append("cxLotNum_Text: "+this.cxLotNum_Text+"\n");
        return buffer.toString();
    }

    public String toResponseString(){
        StringBuffer buffer = new StringBuffer("");
        buffer.append("GTIN_Text: "+this.cxGTIN_Text +"\n");
        buffer.append("cxDateType_SelectedIndex: "+this.cxDateType_Text.get(cxDateType_SelectedIndex) +"\n");
        buffer.append("cxDate_Text: "+this.cxDate_Text+"\n");
        buffer.append("cxLotNum_Text: "+this.cxLotNum_Text+"\n");
        buffer.append("cx128In_Text: "+this.cx128In_Text+"\n");
        buffer.append("la128HR_Text: "+this.la128HR_Text+"\n");
        return buffer.toString();
    }
}

