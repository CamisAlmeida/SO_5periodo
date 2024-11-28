//Função principal 
public class Cozinha {
    public static void main(String[] args) {
        // Recursos compartilhados: dois cozinheiros
        Cozinheiro cozinheiro1 = new Cozinheiro("Cozinheiro 1");
        Cozinheiro cozinheiro2 = new Cozinheiro("Cozinheiro 2");

        // Criar 5 clientes - threads
        for (int i = 1; i <= 5; i++) {
            Cliente cliente = new Cliente("Cliente " + i, cozinheiro1, cozinheiro2);
            new Thread(cliente).start();
        }
    }
}

// Recurso compartilhado - Cozinheiro
class Cozinheiro {
    private final String nome;

    public Cozinheiro(String nome) {
        this.nome = nome;
    }

    public synchronized void atender(String cliente) {
        System.out.println(cliente + " está sendo atendido por " + nome);
        try {
            Thread.sleep(3000); // tempo de atendimento - 3 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(cliente + " terminou de ser atendido por " + nome);
    }
}

// Cliente - Thread
class Cliente implements Runnable {
    private final String nome;
    private final Cozinheiro cozinheiro1;
    private final Cozinheiro cozinheiro2;

    public Cliente(String nome, Cozinheiro cozinheiro1, Cozinheiro cozinheiro2) {
        this.nome = nome;
        this.cozinheiro1 = cozinheiro1;
        this.cozinheiro2 = cozinheiro2;
    }

    @Override
    public void run() {
        // Cliente tenta acessar um dos dois cozinheiros
        while (true) {
            try {
                if (Math.random() < 0.5) {
                    cozinheiro1.atender(nome);
                } else {
                    cozinheiro2.atender(nome);
                }
                break; // Termina após ser atendido
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
