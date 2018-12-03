package alice;

import java.util.Scanner;

public class MainAlice {

    private static Alice alice;

    public static void main(String[] args) throws Exception {
        alice = new Alice();
        Scanner teclado = new Scanner(System.in);

        MonitorEntrada monitorEntrada = new MonitorEntrada();
        Thread thread = new Thread(monitorEntrada);
        thread.start();

        while (teclado.hasNextLine()) {
            alice.enviarMensagemParaBob(teclado.nextLine());
        }

        alice.fechar();
        teclado.close();
    }

    private static class MonitorEntrada implements Runnable{

        public void run() {
            try {
                alice.receberMensagemDeBob();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
