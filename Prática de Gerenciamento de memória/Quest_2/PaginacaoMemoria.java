import java.util.*;

class Pagina {
    int id;
    int idProcesso;

    Pagina(int id, int idProcesso) {
        this.id = id;
        this.idProcesso = idProcesso;
    }
}

class Processo {
    int id;
    String nome;
    int tamanho;
    List<Pagina> paginas = new ArrayList<>();

    Processo(int id, String nome, int tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
    }
}

public class PaginacaoMemoria {
    private int tamanhoMemoriaFisica;
    private int tamanhoMemoriaVirtual;
    private int tamanhoPagina;
    private int totalPaginasFisicas;
    private int totalPaginasVirtuais;
    private Queue<Pagina> memoriaFisica = new LinkedList<>();
    private Queue<Processo> processos = new LinkedList<>();
    private int falhasDePagina = 0;

    public PaginacaoMemoria(int tamanhoMemoriaFisica, int tamanhoMemoriaVirtual, int tamanhoPagina) {
        this.tamanhoMemoriaFisica = tamanhoMemoriaFisica;
        this.tamanhoMemoriaVirtual = tamanhoMemoriaVirtual;
        this.tamanhoPagina = tamanhoPagina;
        this.totalPaginasFisicas = tamanhoMemoriaFisica / tamanhoPagina;
        this.totalPaginasVirtuais = tamanhoMemoriaVirtual / tamanhoPagina;
    }

    public void criarProcesso(String nome, int id, int tamanho) {
        Processo processo = new Processo(id, nome, tamanho);
        int paginasNecessarias = (int) Math.ceil((double) tamanho / tamanhoPagina);

        for (int i = 0; i < paginasNecessarias; i++) {
            processo.paginas.add(new Pagina(i, id));
        }
        processos.add(processo);
    }

    public void alocarPaginasFIFO() {
        for (Processo processo : processos) {
            for (Pagina pagina : processo.paginas) {
                if (memoriaFisica.size() >= totalPaginasFisicas) {
                    Pagina paginaRemovida = memoriaFisica.poll();
                    System.out.println("Página substituída: Processo " + paginaRemovida.idProcesso + ", Página " + paginaRemovida.id);
                    falhasDePagina++;
                }
                memoriaFisica.add(pagina);
                System.out.println("Página alocada: Processo " + pagina.idProcesso + ", Página " + pagina.id);
            }
        }
    }

    public void imprimirEstadoMemoria() {
        System.out.println("Estado da Memória Física:");
        for (Pagina pagina : memoriaFisica) {
            System.out.println("Processo " + pagina.idProcesso + ", Página " + pagina.id);
        }
        System.out.println("Total de Falhas de Página: " + falhasDePagina);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o tamanho da memória física (em KB): ");
        int tamanhoMemoriaFisica = scanner.nextInt();

        System.out.print("Digite o tamanho da memória virtual (em KB): ");
        int tamanhoMemoriaVirtual = scanner.nextInt();

        System.out.print("Digite o tamanho da página (em KB): ");
        int tamanhoPagina = scanner.nextInt();

        PaginacaoMemoria memoria = new PaginacaoMemoria(tamanhoMemoriaFisica, tamanhoMemoriaVirtual, tamanhoPagina);

        System.out.print("Digite o número de processos: ");
        int numeroProcessos = scanner.nextInt();

        for (int i = 0; i < numeroProcessos; i++) {
            System.out.print("Digite o nome do processo: ");
            String nome = scanner.next();

            System.out.print("Digite o ID do processo: ");
            int id = scanner.nextInt();

            System.out.print("Digite o tamanho do processo (em KB): ");
            int tamanho = scanner.nextInt();

            memoria.criarProcesso(nome, id, tamanho);
        }

        memoria.alocarPaginasFIFO();
        memoria.imprimirEstadoMemoria();
    }
}
