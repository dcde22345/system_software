import java.io.*;

class op_TAB {
    String mnemonic[] = { "ADD", "ADDF", "ADDR", "AND", "CLEAR", "COMPF", "COMPR", "COMP", "DIVF", "DIVR", "DIV", "FIX",
            "FLOAT", "HIO", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS", "LDT", "LDX",
            "LPS", "UML", "MULF", "MULR", "NORM", "OR", "RD", "RMO", "RSUB", "SHIFTL", "SHIFTR", "SIO", "SSK", "STA",
            "STB", "STCH", "STF", "STI", "STL", "STSW", "STS", "STT", "STX", "SUBF", "SUBR", "SUB", "SVC", "TD", "TIO",
            "TIXR", "TIX", "WD" };
    String format[] = { "3", "3", "2", "3", "2", "3", "2", "3", "3", "2", "3", "1", "1", "1", "3", "3", "3", "3", "3",
            "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "2", "1", "3", "3", "2", "3", "2", "2", "1", "3",
            "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "3", "2", "3", "2", "3", "1", "2", "3", "3" };
    String OPcode[]={
        "18","58","90","40","B4","88","A0","28","64","9C","24","C4","C0","F4","3C","30","34","38","48","00","68","50"
        ,"70","08","6C","74","04","E0","20","60","98","C8","44","D8","AC","4C","A4","A8","F0","EC","0C","78","54","80"
        ,"D4","14","E8","7C","84","10","5C","94","1C","B0","E0","F8","B8","2C","DC"};
}

public class SICXE {
    public static void main(String[] args) {
        op_TAB TAB = new op_TAB();
        System.out.println(TAB);
        for(int i = 0; i<=TAB.OPcode.length; i++){
            System.out.println(TAB.mnemonic[i]);
        }

        // String fileName = "./system_software/Figure2.1.txt";
        // read(fileName);
    }

    public static void read(String fileName) {
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fr);
        String tmp = null;

        // 以行的方式讀取檔案，並調整格式
        try {
            tmp = br.readLine();
            // 將tab刪除
            tmp = format(tmp);
            while (tmp != null) {
                System.out.println(tmp);
                tmp = br.readLine();
                tmp = format(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 將tmp字串格式化
    public static String format(String tmp) {
        // 將tab刪掉並將多個連續空格改成一個空格
        tmp = tmp.replaceAll("\t", "").replaceAll("\\s+", " ");
        // 如果首字為空白，刪掉第一個空白
        if (tmp.charAt(0) == ' ')
            tmp = tmp.substring(1);
        // 如果字尾為空白，刪掉最後一個空白
        if (tmp.charAt(tmp.length() - 1) == ' ')
            tmp = tmp.substring(0, tmp.length() - 2);
        return tmp;
    }
}