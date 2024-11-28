import java.util.*;

public class SimuladorEscalonamento {

    private static final List<Processo> filaProntos = new LinkedList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Simulador de Escalonamento!");
        System.out.println("Escolha um algoritmo de escalonamento:");
        System.out.println("1. Round-Robin");
        System.out.println("2. Prioridade");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        System.out.println("Defina o quantum de tempo (1 a 10 ms):");
        int quantum = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Criar novo processo");
            System.out.println("2. Exibir fila de prontos");
            System.out.println("3. Iniciar execução");
            System.out.println("4. Sair");
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (escolha) {
                case 1:
                    criarProcesso();
                    break;
                case 2:
                    exibirFila();
                    break;
                case 3:
                    if (opcao == 1) {
                        Escalonador.executarRoundRobin(filaProntos, quantum);
                    } else if (opcao == 2) {
                        Escalonador.executarPrioridade(filaProntos);
                    }
                    return;
                case 4:
                    System.out.println("Encerrando simulador...");
                    return;
                default:
                    System.out.println("Escolha inválida.");
            }
        }
    }

    private static void criarProcesso() {
        System.out.println("Criando novo processo...");
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Prioridade (1 a 10, 1 é maior prioridade): ");
        int prioridade = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        System.out.print("Tipo (1 para I/O-bound, 2 para CPU-bound): ");
        int tipo = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        boolean ioBound = tipo == 1;
        System.out.print("Tempo de CPU total (1 a 10 ms): ");
        int tempoCpuTotal = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        Processo novoProcesso = new Processo(id, nome, prioridade, ioBound, tempoCpuTotal);
        filaProntos.add(novoProcesso);
        System.out.println("Processo criado com sucesso!\n" + novoProcesso);
    }

    private static void exibirFila() {
        if (filaProntos.isEmpty()) {
            System.out.println("A fila de prontos está vazia.");
            return;
        }
        System.out.println("Fila de prontos:");
        for (Processo p : filaProntos) {
            System.out.println(p);
        }
    }
}
