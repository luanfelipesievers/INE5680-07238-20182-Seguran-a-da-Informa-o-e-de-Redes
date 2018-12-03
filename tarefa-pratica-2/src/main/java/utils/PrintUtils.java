package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtils {

    private final SimpleDateFormat dateFormat;

    public PrintUtils() {
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public void printComTempo(String texto) {
        System.out.println(UtilsColors.ANSI_RESET + this.dateFormat.format(new Date()) + "\n\t\t" + texto);
    }

    public void printNormal(String texto) {
        System.out.println(UtilsColors.ANSI_RESET + "\t\t" + texto);
    }
}
