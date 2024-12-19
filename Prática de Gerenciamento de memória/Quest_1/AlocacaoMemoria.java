import java.util.*;

public class AlocacaoMemoria {

    // Classe que representa um processo
    private static class Processo {
        String nome; // Nome do processo
        int id; // ID do processo
        int tamanho; // Tamanho do processo em MB

        Processo(String nome, int id, int tamanho) {
            this.nome = nome;
            this.id = id;
            this.tamanho = tamanho;
        }
    }

    // Classe que representa um bloco de memória
    private static class BlocoMemoria {
        int inicio; // Índice inicial do bloco na memória
        int tamanho; // Tamanho do bloco em MB
        boolean livre; // Indica se o bloco está livre

        BlocoMemoria(int inicio, int tamanho) {
            this.inicio = inicio;
            this.tamanho = tamanho;
            this.livre = true; // Por padrão, todos os blocos começam livres
        }
    }

    // Variáveis globais
    private static int memoriaTotal; // Capacidade total de memória
    private static List<BlocoMemoria> memoria = new ArrayList<>(); // Lista de blocos de memória
    private static List<Processo> processos = new ArrayList<>(); // Lista de processos alocados
    private static List<Processo> swap = new ArrayList<>(); // Lista de processos na memória secundária (swap)

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Configuração inicial da memória
        System.out.print("Informe o tamanho total da memória (em MB): ");
        memoriaTotal = scanner.nextInt();
        memoria.add(new BlocoMemoria(0, memoriaTotal)); // Inicialmente, um único bloco livre ocupa toda a memória

        // Loop principal para interação com o usuário
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

    // Método para criar e alocar um novo processo
    private static void criarProcesso(Scanner scanner) {
        System.out.print("Nome do processo: ");
        String nome = scanner.next();
        System.out.print("ID do processo: ");
        int id = scanner.nextInt();
        System.out.print("Tamanho do processo (em MB): ");
        int tamanho = scanner.nextInt();

        Processo processo = new Processo(nome, id, tamanho);
        boolean alocado = alocarMemoria(processo);

        // Tenta alocar memória; se não conseguir, compacta e tenta novamente
        if (!alocado) {
            compactarMemoria();
            alocado = alocarMemoria(processo);
        }
        // Se ainda não conseguir, remove um processo aleatório e tenta novamente
        if (!alocado) {
            removerProcessoAleatorio();
            alocarMemoria(processo);
        }

        if (!alocado) {
            System.out.println("Falha: não foi possível alocar o processo " + processo.nome + " após todas as tentativas.");
        }
    }

    // Método que implementa a alocação de memória pelo Best-Fit
    private static boolean alocarMemoria(Processo processo) {
        BlocoMemoria bestFit = null;

        // Encontra o menor bloco que ainda seja grande o suficiente
        for (BlocoMemoria bloco : memoria) {
            if (bloco.livre && bloco.tamanho >= processo.tamanho) {
                if (bestFit == null || bloco.tamanho < bestFit.tamanho) {
                    bestFit = bloco;
                }
            }
        }

        // Se encontrou um bloco adequado
        if (bestFit != null) {
            bestFit.livre = false; // Marca o bloco como ocupado

            // Se o bloco é maior que o necessário, cria um bloco para o espaço restante
            if (bestFit.tamanho > processo.tamanho) {
                int tamanhoRestante = bestFit.tamanho - processo.tamanho;
                memoria.add(new BlocoMemoria(bestFit.inicio + processo.tamanho, tamanhoRestante));
            }

            bestFit.tamanho = processo.tamanho; // Ajusta o tamanho do bloco ocupado
            processos.add(processo); // Adiciona o processo à lista de processos alocados
            return true;
        }

        System.out.println("Memória insuficiente para o processo " + processo.nome);
        return false;
    }

    // Método para compactar a memória
    private static void compactarMemoria() {
        System.out.println("Realizando compactação...");
        int inicioAtual = 0;
        List<BlocoMemoria> novaMemoria = new ArrayList<>();

        // Move todos os blocos ocupados para o início da memória
        for (BlocoMemoria bloco : memoria) {
            if (!bloco.livre) {
                novaMemoria.add(new BlocoMemoria(inicioAtual, bloco.tamanho));
                inicioAtual += bloco.tamanho;
            }
        }

        // Adiciona o espaço livre restante como um único bloco
        if (inicioAtual < memoriaTotal) {
            novaMemoria.add(new BlocoMemoria(inicioAtual, memoriaTotal - inicioAtual));
        }

        memoria = novaMemoria; // Substitui a lista de blocos pela versão compactada
    }

    // Método para remover aleatoriamente um processo
    private static void removerProcessoAleatorio() {
        if (!processos.isEmpty()) {
            Random random = new Random();
            int indice = random.nextInt(processos.size());
            Processo processoRemovido = processos.remove(indice); // Remove o processo da lista de processos alocados

            System.out.println("Removendo processo " + processoRemovido.nome + " para memória secundária.");
            swap.add(processoRemovido); // Adiciona o processo à memória secundária

            // Libera o bloco correspondente na memória
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

    // Método para exibir o estado atual da memória
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