import java.util.*;

// Representa uma página na memória
class Pagina {
    int id; // id da página
    int idProcesso; 

    Pagina(int id, int idProcesso) {
        this.id = id;
        this.idProcesso = idProcesso;
    }
}

// Representa um processo 
class Processo {
    int id; // Id do processo
    String nome; 
    int tamanho; // Tamanho em KB
    List<Pagina> paginas = new ArrayList<>(); // Lista de páginas associadas ao processo

    Processo(int id, String nome, int tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
    }
}

// Classe que gerencia a paginação de memória
public class PaginacaoMemoria {
    private int tamanhoMemoriaFisica; // Tamanho total em KB
    private int tamanhoMemoriaVirtual; // Tamanho total em KB
    private int tamanhoPagina; // Tamanho de cada página em KB
    private int totalPaginasFisicas; // Número total de páginas na memória física
    private int totalPaginasVirtuais; // Número total de páginas na memória virtual
    private Queue<Pagina> memoriaFisica = new LinkedList<>(); // Representa as páginas atualmente na memória física
    private List<Pagina> memoriaVirtual = new ArrayList<>(); // Representa as páginas na memória virtual
    private Queue<Processo> processos = new LinkedList<>(); // Fila de processos a serem alocados
    private int pageMiss = 0; // Contador de falhas de página

    public PaginacaoMemoria(int tamanhoMemoriaFisica, int tamanhoMemoriaVirtual, int tamanhoPagina) {
        this.tamanhoMemoriaFisica = tamanhoMemoriaFisica;
        this.tamanhoMemoriaVirtual = tamanhoMemoriaVirtual;
        this.tamanhoPagina = tamanhoPagina;
        this.totalPaginasFisicas = tamanhoMemoriaFisica / tamanhoPagina;
        this.totalPaginasVirtuais = tamanhoMemoriaVirtual / tamanhoPagina;
    }

    // Cria um novo processo e associa as páginas necessárias
    public void criarProcesso(String nome, int id, int tamanho) {
        Processo processo = new Processo(id, nome, tamanho);
        int paginasNecessarias = (int) Math.ceil((double) tamanho / tamanhoPagina); // Calcula o número de páginas

        // Cria as páginas para o processo
        for (int i = 0; i < paginasNecessarias; i++) {
            Pagina pagina = new Pagina(i, id);
            processo.paginas.add(pagina);
            memoriaVirtual.add(pagina); // Adiciona a página na memória virtual
        }
        processos.add(processo); // Adiciona o processo à fila
    }

    // Aloca páginas na memória física usando FIFO
    public void alocarPaginasFIFO() {
        for (Processo processo : processos) {
            for (Pagina pagina : processo.paginas) {
                if (memoriaFisica.size() >= totalPaginasFisicas) { // Se a memória física está cheia
                    Pagina paginaRemovida = memoriaFisica.poll(); // Remove a página mais antiga
                    System.out.println("Página substituída: Processo " + paginaRemovida.idProcesso + ", Página " + paginaRemovida.id);
                    memoriaVirtual.add(paginaRemovida); // Retorna a página para a memória virtual
                    pageMiss++; // Incrementa o contador de falhas de página
                }
                memoriaFisica.add(pagina); // Adiciona a nova página
                memoriaVirtual.remove(pagina); // Remove a página da memória virtual
                System.out.println("Página alocada na memória física: Processo " + pagina.idProcesso + ", Página " + pagina.id);
            }
        }
    }

    // Imprime o estado atual das memórias física e virtual
    public void imprimirEstadoMemoria() {
        System.out.println("----------------------------------");
        System.out.println("Estado da Memória Física:");
        for (Pagina pagina : memoriaFisica) {
            System.out.println("Processo " + pagina.idProcesso + ", Página " + pagina.id);
        }
        System.out.println("----------------------------------");
        System.out.println("Estado da Memória Virtual:");
        for (Pagina pagina : memoriaVirtual) {
            System.out.println("Processo " + pagina.idProcesso + ", Página " + pagina.id);
        }
        System.out.println("----------------------------------");
        System.out.println("Total de Falhas de Página: " + pageMiss);
    }

    // Inicia o programa
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicita as informações iniciais de memória para o usuário
        System.out.print("Digite o tamanho da memória física (em KB): ");
        int tamanhoMemoriaFisica = scanner.nextInt();

        System.out.print("Digite o tamanho da memória virtual (em KB): ");
        int tamanhoMemoriaVirtual = scanner.nextInt();

        System.out.print("Digite o tamanho da página (em KB): ");
        int tamanhoPagina = scanner.nextInt();

        // Inicializa o gerenciador de memória
        PaginacaoMemoria memoria = new PaginacaoMemoria(tamanhoMemoriaFisica, tamanhoMemoriaVirtual, tamanhoPagina);

        // Solicita ao usuário os dados dos processos
        System.out.print("Digite o número de processos: ");
        int numeroProcessos = scanner.nextInt();

        for (int i = 0; i < numeroProcessos; i++) {
            System.out.println("--------------------------------------");
            System.out.print("Digite o nome do processo: ");
            String nome = scanner.next();

            System.out.print("Digite o ID do processo: ");
            int id = scanner.nextInt();

            System.out.print("Digite o tamanho do processo (em KB): ");
            int tamanho = scanner.nextInt();

            memoria.criarProcesso(nome, id, tamanho); // Cria o processo
        }

        System.out.println("--------------------------------------");
        // Realiza a alocação de páginas e exibe o estado final da memória
        memoria.alocarPaginasFIFO();
        memoria.imprimirEstadoMemoria();
    }
}