package bob;

import java.util.Scanner;

public class MainBob {

    private static Bob bob;

    public static void main(String[] args) throws Exception {
        bob = new Bob();
        Scanner teclado = new Scanner(System.in);

        MonitorEntrada monitorEntrada = new MonitorEntrada();
        Thread thread = new Thread(monitorEntrada);
        thread.start();

        while (teclado.hasNextLine()) {
            bob.enviarMensagemParaAlice(teclado.nextLine());
        }

        bob.fechar();
        teclado.close();

    }

    private static class MonitorEntrada implements Runnable{

        public void run() {
            try {
                bob.receberMensagemDeAlice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
