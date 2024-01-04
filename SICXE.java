import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Instruction {
    String Label = "";
    String Statement = "";
    String Operand = "";
    String Loction = "";
    String objectCode = "";
    boolean comment = false;

    Instruction(String label, String statement, String operand) {
        Label = label;
        Statement = statement;
        Operand = operand;
    }

    Instruction(String statement, String operand) {
        Statement = statement;
        Operand = operand;
    }

    Instruction(String statement) {
        Statement = statement;
    }

    Instruction() {
    };
}

class TextRecord {
    String type = "T";
    String startingAddress = "";
    String length = "";
    String objectCode = "";

    TextRecord() {
    };
}

class SYMBOL {
    String symbol = "";
    String loction = "";

    SYMBOL(String sym, String loc) {
        symbol = sym;
        loction = loc;
    }
}

public class SICXE {
    public static void main(String[] args) {
        String op_TAB[][] = { { "ADD", "3", "18" }, { "ADDF", "3", "58" }, { "ADDR", "2", "90" }, { "AND", "3", "40" },
                { "CLEAR", "2", "B4" }, { "COMPF", "3", "88" }, { "COMPR", "2", "A0" }, { "COMP", "3", "28" },
                { "DIVF", "3", "64" }, { "DIVR", "2", "9C" }, { "DIV", "3", "24" }, { "FIX", "1", "C4" },
                { "FLOAT", "1", "C0" }, { "HIO", "1", "F4" }, { "J", "3", "3C" }, { "JEQ", "3", "30" },
                { "JGT", "3", "34" }, { "JLT", "3", "38" }, { "JSUB", "3", "48" }, { "LDA", "3", "00" },
                { "LDB", "3", "68" }, { "LDCH", "3", "50" }, { "LDF", "3", "70" }, { "LDL", "3", "08" },
                { "LDS", "3", "6C" }, { "LDT", "3", "74" }, { "LDX", "3", "04" }, { "LPS", "3", "E0" },
                { "UML", "3", "20" }, { "MULF", "3", "60" }, { "MULR", "2", "98" }, { "NORM", "1", "C8" },
                { "OR", "3", "44" }, { "RD", "3", "D8" }, { "RMO", "2", "AC" }, { "RSUB", "3", "4C" },
                { "SHIFTL", "2", "A4" }, { "SHIFTR", "2", "A8" }, { "SIO", "1", "F0" }, { "SSK", "3", "EC" },
                { "STA", "3", "0C" }, { "STB", "3", "78" }, { "STCH", "3", "54" }, { "STF", "3", "80" },
                { "STI", "3", "D4" }, { "STL", "3", "14" }, { "STSW", "3", "E8" }, { "STS", "3", "7C" },
                { "STT", "3", "84" }, { "STX", "3", "10" }, { "SUBF", "3", "5C" }, { "SUBR", "2", "94" },
                { "SUB", "3", "1C" }, { "SVC", "2", "B0" }, { "TD", "3", "E0" }, { "TIO", "1", "F8" },
                { "TIXR", "2", "B8" }, { "TIX", "3", "2C" }, { "WD", "3", "DC" } };

        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<SYMBOL> SYMTAB = new ArrayList<>();
        ArrayList<String> location = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        System.out.println("請輸入檔案位置");
        String fileName = sc.nextLine();
        sc.close();
        pass1(fileName, instructions, SYMTAB, location, op_TAB);

        System.out.println("SYMTAB");
        System.out.println("---------------------");
        for (SYMBOL item : SYMTAB) {
            System.out.println(item.symbol + "\t" + item.loction);
        }
        // 將location插入instruction物件
        int j = 0;
        for (int i = 0; i < instructions.size(); i++) {
            if (!instructions.get(i).Label.equals(".") &&
                    !instructions.get(i).Statement.equals("END") && j < location.size()) {
                instructions.get(i).Loction = location.get(j);
                j++;
            }
        }
        pass2(instructions, SYMTAB, op_TAB);
    }

    public static void pass1(String fileName, ArrayList<Instruction> instructions, ArrayList<SYMBOL> SYMTAB,
            ArrayList<String> location, String[][] op_TAB) {
        // 讀取檔案的套件設定
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fr);
        String tmp = "";
        int loc = 0;
        Instruction tmpInstruction;
        String hex;

        // 以行的方式讀取檔案，並調整格式，最終轉變為物件
        try {
            // 讀取第一行並確認是否為START statement
            tmp = br.readLine();
            tmp = format(tmp);
            tmpInstruction = changeToInstruction(tmp);

            if (tmpInstruction.Statement.equals("START")) {
                loc = Integer.parseInt(tmpInstruction.Operand, 16);
                hex = String.format("%04X", loc);
                location.add(hex);
                instructions.add(tmpInstruction);
                // 將label和LOC插入SYMTAB
                SYMTAB.add(new SYMBOL(tmpInstruction.Label, hex));
            } else {
                hex = String.format("%04X", loc);
                location.add(hex);
                instructions.add(tmpInstruction);
            }

            do {
                tmp = br.readLine();

                // 如果沒有if會在最後一行報錯
                if (tmp == null)
                    break;

                tmp = format(tmp);
                tmpInstruction = new Instruction();
                if (tmp.contains(".")) {
                    // 註解
                    String tmpStrings[] = tmp.split(" ");
                    if (tmpStrings.length == 1) {
                        tmpInstruction.Label = tmpStrings[0];
                    } else {
                        tmpInstruction.Label = tmpStrings[0];
                        tmpInstruction.Statement = tmpStrings[1];
                    }
                    tmpInstruction.comment = true;
                } else {
                    // 非註解
                    hex = String.format("%04X", loc);
                    tmpInstruction = changeToInstruction(tmp);

                    // 確認有無Label
                    if (tmpInstruction.Label != "") {
                        for (SYMBOL item : SYMTAB) {
                            if (item.symbol.contains(tmpInstruction.Label)) {
                                System.err.println("Duplicate symbol");
                            }
                        }
                        SYMTAB.add(new SYMBOL(tmpInstruction.Label, hex));
                    }

                    // 確認loc計算
                    for (String[] item : op_TAB) {
                        if (tmpInstruction.Statement.equals(item[0])) {
                            loc = loc + 3;
                            break;
                        }
                    }
                    if (tmpInstruction.Statement.equals("WORD"))
                        loc += 3;
                    if (tmpInstruction.Statement.equals("RESW"))
                        loc += (3 * Integer.parseInt(tmpInstruction.Operand));
                    if (tmpInstruction.Statement.equals("RESB"))
                        loc += Integer.parseInt(tmpInstruction.Operand);
                    if (tmpInstruction.Statement.equals("BYTE")) {
                        if (tmpInstruction.Operand.contains("C")) {
                            loc += tmpInstruction.Operand.length() - 3;
                        } else {
                            loc += 1;
                        }
                    }
                    location.add(hex);
                }
                instructions.add(tmpInstruction);
            } while (tmp != null);
        } catch (

        IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void pass2(ArrayList<Instruction> instructions, ArrayList<SYMBOL> SYMTAB, String[][] op_TAB) {
        try {
            // Output file path for object program
            String objectProgramFilePath = "object_program.txt";
            String recordFile = "record.txt";
            FileWriter objectFw = new FileWriter(objectProgramFilePath);
            FileWriter recordFw = new FileWriter(recordFile);
            String format = "%1$-6s%2$-8s%3$-6s%4$12s%5$10s\n";
            int i = 0;

            // 第一行確認有無START並建立Head record
            if (instructions.get(0).Statement.equals("START")) {
                Instruction startInstruction = instructions.get(0);
                int start = Integer.parseInt(startInstruction.Operand, 16);
                int end = Integer.parseInt(instructions.get(instructions.size() - 2).Loction, 16) + 1;
                int size = end - start;
                objectFw.write(String.format(format, startInstruction.Loction, startInstruction.Label,
                        startInstruction.Statement, startInstruction.Operand, startInstruction.objectCode));

                recordFw.write("H" + String.format("%1$-6s", startInstruction.Label) + "\t"
                        + String.format("%06X", start) + String.format("%06X", size) + "\n");
                i++;
            }

            Instruction tmp;
            String previousLoc = instructions.get(0).Loction;
            String decoded = "00";
            String addressingMode;
            String operandAddr = "0000";
            TextRecord tr = new TextRecord();

            while (!instructions.get(i).Statement.equals("END")) {
                // 直到END前持續
                tmp = instructions.get(i);
                // 重制
                addressingMode = "0";
                if (!tmp.comment) {
                    // instruction不是comment
                    for (String[] opCode : op_TAB) {
                        if (tmp.Statement.equals(opCode[0])) {
                            // 在OP table中找與statement相同的opcode
                            decoded = opCode[2];
                            if (!tmp.Operand.isEmpty()) {
                                // tmp的operand欄不為空
                                if (tmp.Operand.contains(",") && tmp.Operand.contains("X")) {
                                    operandAddr = findOperandInSYMTAB(
                                            tmp.Operand.substring(0, tmp.Operand.length() - 2), SYMTAB);
                                    addressingMode = "1";
                                } else {
                                    operandAddr = findOperandInSYMTAB(tmp.Operand, SYMTAB);
                                }

                            } else {
                                // tmp.Operand欄為空
                                operandAddr = "0000";
                            }
                            String bin = HexToBinary(operandAddr);
                            String hex = combineAndToHex(addressingMode, bin);
                            tmp.objectCode = decoded + padLeftZeros(hex, 4);

                            // 處理Text Record
                            if (tr.objectCode.length() + tmp.objectCode.length() > 60
                                    || Integer.parseInt(hexMinus(tmp.Loction, previousLoc), 16) >= 31) {
                                // 若tr.objectCode超過60或tr.length超過1E則寫出來並重置
                                tr.length = hexMinus(instructions.get(i).Loction, tr.startingAddress);
                                recordFw.write(tr.type + tr.startingAddress + tr.length + tr.objectCode + "\n");
                                tr.objectCode = "";
                            }
                            if (tr.objectCode.equals("")) {
                                // 新的record
                                tr.startingAddress = padLeftZeros(tmp.Loction, 6);
                            }
                            // 舊有record
                            tr.objectCode += tmp.objectCode;
                            previousLoc = tmp.Loction;
                            break;
                        }
                    }
                    if (tmp.Statement.equals("BYTE") || tmp.Statement.equals("WORD")) {
                        if (tmp.Operand.contains("C")) {
                            // 儲存字串
                            operandAddr = "";
                            for (int k = 2; k < tmp.Operand.length() - 1; k++) {
                                int acsii = tmp.Operand.charAt(k);
                                String acsiiString = String.format("%2X", acsii);
                                operandAddr += acsiiString;
                            }
                            tmp.objectCode = operandAddr;

                        } else if (tmp.Operand.contains("X")) {
                            // 儲存暫存器位址
                            tmp.objectCode = tmp.Operand.substring(2, tmp.Operand.length() - 1);
                        } else {
                            // 儲存常數
                            String bin = DecimalToBinary(tmp.Operand);
                            String hex = combineAndToHex(addressingMode, bin);
                            tmp.objectCode = String.format("%06d", Integer.valueOf(hex));
                        }
                        // 處理Text Record
                        if (tr.objectCode.length() + tmp.objectCode.length() > 60
                                || Integer.parseInt(hexMinus(tmp.Loction, previousLoc), 16) >= 31) {
                            // 若tr.objectCode超過60或tr.length超過1E則寫出來並重置
                            tr.length = hexMinus(instructions.get(i).Loction, tr.startingAddress);
                            recordFw.write(tr.type + tr.startingAddress + tr.length + tr.objectCode+ "\n");
                            tr.objectCode = "";
                        }
                        if (tr.objectCode.equals("")) {
                            // 新的record
                            tr.startingAddress = padLeftZeros(tmp.Loction, 6);
                        }
                        // 舊有record
                        tr.objectCode += tmp.objectCode;
                        previousLoc = tmp.Loction;
                    }
                }
                objectFw.write(String.format(format, tmp.Loction, tmp.Label, tmp.Statement, tmp.Operand,
                        tmp.objectCode.toUpperCase()));
                i++;
            }
            recordFw.write(tr.type + tr.startingAddress + tr.length + tr.objectCode + "\n");


            Instruction endInstruction = instructions.get(i);
            objectFw.write(String.format(format, endInstruction.Loction, endInstruction.Label, endInstruction.Statement,
                    endInstruction.Operand, endInstruction.objectCode));

            recordFw.write("E" + String.format("%06X", Integer.parseInt(instructions.get(0).Loction, 16)) + "\n");

            objectFw.close();
            recordFw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 將tmp字串格式化
    public static String format(String tmp) {
        // 將tab刪掉並將多個連續空格改成一個空格
        tmp = tmp.replaceAll("\t", " ").replaceAll("\\s+", " ");
        // 如果首字為空白，刪掉第一個空白
        if (tmp.charAt(0) == ' ')
            tmp = tmp.substring(1);
        // 如果字尾為空白，刪掉最後一個空白
        if (tmp.charAt(tmp.length() - 1) == ' ')
            tmp = tmp.substring(0, tmp.length() - 1);
        return tmp;
    }

    // 將格式化的字串轉換為instruction物件
    public static Instruction changeToInstruction(String format) {
        String tmp[] = format.split(" ");
        if (tmp.length == 1)
            return new Instruction(tmp[0]);
        if (tmp.length == 2)
            return new Instruction(tmp[0], tmp[1]);
        return new Instruction(tmp[0], tmp[1], tmp[2]);
    }

    public static String findOperandInSYMTAB(String operand, ArrayList<SYMBOL> SYMTAB) {
        for (SYMBOL item : SYMTAB) {
            // 在SYMTAB裡找operand
            if (operand.equals(item.symbol)) {
                // 找到了將operandAddr設為其location
                return item.loction;
            }
        }
        // 沒找到跳錯誤訊息
        System.err.println("undefine symbol");
        return "0000";
    }

    public static String DecimalToBinary(String operand) {
        return String.format("%016d", Long.valueOf(Integer.toBinaryString(Integer.valueOf(operand))));
    }

    public static String HexToBinary(String operand) {
        return String.format("%016d", Long.valueOf(Integer.toBinaryString(Integer.valueOf(operand, 16))));
    }

    public static String combineAndToHex(String addressingMode, String bin) {
        String tmp = addressingMode.concat(bin.substring(1));
        return Integer.toHexString(Integer.valueOf(tmp, 2));

    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static String hexMinus(String a, String b) {
        int aValue = Integer.parseInt(a, 16);
        int bValue = Integer.parseInt(b, 16);

        // 計算
        int result = aValue - bValue;

        // 轉換為16進位string
        String resultHex = Integer.toHexString(result).toUpperCase();

        return resultHex;
    }

}