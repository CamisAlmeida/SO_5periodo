import java.util.*;

public class AlocacaoMemoria {
    // Classe de processo
    private static class Processo {
        String nome;
        int id;
        int tamanho;

        Processo(String nome, int id, int tamanho) {
            this.nome = nome;
            this.id = id;
            this.tamanho = tamanho;
        }
    }

    // Classe de bloco de memória
    private static class BlocoMemoria {
        int inicio;
        int tamanho;
        boolean livre;

        BlocoMemoria(int inicio, int tamanho) {
            this.inicio = inicio;
            this.tamanho = tamanho;
            this.livre = true;
        }
    }

    // Atributos da alocação de memória
    private static int memoriaTotal;
    private static List<BlocoMemoria> memoria = new ArrayList<>();
    private static List<Processo> processos = new ArrayList<>();
    private static List<Processo> swap = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Configuração inicial - Pede o tamanho total da memória
        System.out.print("Informe o tamanho total da memória (em MB): ");
        memoriaTotal = scanner.nextInt();
        memoria.add(new BlocoMemoria(0, memoriaTotal));

        // Loop de alocação e criação de processos na memória
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Criar processo");
            System.out.println("2. Mostrar estado da memória");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            int escolha = scanner.nextInt();

            switch (escolha) {
                case 1:
                    criarProcesso(scanner);
                    break;
                case 2:
                    mostrarEstadoMemoria();
                    break;
                case 3:
                    System.out.println("Encerrando...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void criarProcesso(Scanner scanner) {
        System.out.print("Nome do processo: ");
        String nome = scanner.next();
        System.out.print("ID do processo: ");
        int id = scanner.nextInt();
        System.out.print("Tamanho do processo (em MB): ");
        int tamanho = scanner.nextInt();

        Processo processo = new Processo(nome, id, tamanho);
        boolean alocado = alocarMemoria(processo);

        if (!alocado) {
            compactarMemoria();
            alocado = alocarMemoria(processo);
        }

        while(!alocado && !processos.isEmpty()){
            if (!alocado) {
                removerProcessoAleatorio();
                compactarMemoria();
                alocado = alocarMemoria(processo);
            }
        }

        if (!alocado) {
            System.out.println("Falha: não foi possível alocar o processo " + processo.nome + " após todas as tentativas.");
        }
    }

    private static boolean alocarMemoria(Processo processo) {
        BlocoMemoria bestFit = null;

        // Encontra o bloco mais próximo em tamanho (Best-Fit)
        for (BlocoMemoria bloco : memoria) {
            if (bloco.livre && bloco.tamanho >= processo.tamanho) {
                if (bestFit == null || bloco.tamanho < bestFit.tamanho) {
                    bestFit = bloco;
                }
            }
        }

        // Se encontrou um bloco adequado
        if (bestFit != null) {
            bestFit.livre = false;

            // Ajusta o tamanho do bloco livre restante
            if (bestFit.tamanho > processo.tamanho) {
                int tamanhoRestante = bestFit.tamanho - processo.tamanho;
                memoria.add(new BlocoMemoria(bestFit.inicio + processo.tamanho, tamanhoRestante));
            }

            // Atualiza o tamanho do bloco alocado para o processo
            bestFit.tamanho = processo.tamanho;
            processos.add(processo);
            return true;
        }

        System.out.println("Memória insuficiente para o processo " + processo.nome);
        return false;
    }

    private static void compactarMemoria() {
        System.out.println("Realizando compactação...");
        int inicioAtual = 0;
        List<BlocoMemoria> novaMemoria = new ArrayList<>();

        for (BlocoMemoria bloco : memoria) {
            if (!bloco.livre) {
                novaMemoria.add(new BlocoMemoria(inicioAtual, bloco.tamanho));
                inicioAtual += bloco.tamanho;
            }
        }

        if (inicioAtual < memoriaTotal) {
            novaMemoria.add(new BlocoMemoria(inicioAtual, memoriaTotal - inicioAtual));
        }

        memoria = novaMemoria;
    }

    private static void removerProcessoAleatorio() {
        if (!processos.isEmpty()) {
            Random random = new Random();
            int indice = random.nextInt(processos.size());
            Processo processoRemovido = processos.remove(indice);

            System.out.println("Removendo processo " + processoRemovido.nome + " para memória secundária.");
            swap.add(processoRemovido);

            for (BlocoMemoria bloco : memoria) {
                if (!bloco.livre && bloco.tamanho == processoRemovido.tamanho) {
                    bloco.livre = true;
                    break;
                }
            }
        } else {
            System.out.println("Nenhum processo para remover.");
        }
    }

    private static void mostrarEstadoMemoria() {
        System.out.println("\nEstado da memória:");
        for (BlocoMemoria bloco : memoria) {
            System.out.println("Posição: " + bloco.inicio + ", Tamanho: " + bloco.tamanho + "MB, Livre: " + bloco.livre);
        }

        System.out.println("\nProcessos alocados:");
        for (Processo processo : processos) {
            System.out.println("Nome: " + processo.nome + ", ID: " + processo.id + ", Tamanho: " + processo.tamanho + "MB");
        }

        System.out.println("\nProcessos na memória secundária:");
        for (Processo processo : swap) {
            System.out.println("Nome: " + processo.nome + ", ID: " + processo.id + ", Tamanho: " + processo.tamanho + "MB");
        }
    }
}