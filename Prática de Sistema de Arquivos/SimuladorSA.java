import java.util.*;

// Representa um bloco de memória no sistema de arquivos
class Bloco {
    boolean estaOcupado; // Indica se o bloco está ocupado
    String nomeArquivo;  // Nome do arquivo que ocupa este bloco
    int fragmentado;     // Indica quanto do bloco está fragmentado (em bytes)

    Bloco() {
        this.estaOcupado = false;
        this.nomeArquivo = null;
        this.fragmentado = 0;
    }
}

// Representa um arquivo no sistema de arquivos
class Arquivo {
    String nome; // Nome do arquivo
    int tamanho; // Tamanho do arquivo em bytes
    List<Integer> blocosAlocados; // Lista de índices dos blocos alocados para o arquivo

    Arquivo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.blocosAlocados = new ArrayList<>();
    }
}

// Representa um diretório no sistema de arquivos
class Diretorio {
    String nome; // Nome do diretório
    List<Arquivo> arquivos; // Lista de arquivos no diretório

    Diretorio(String nome) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
    }
}

// Classe principal que simula o sistema de arquivos
class SistemaDeArquivos {
    private int totalBlocos; // Quantidade total de blocos na memória
    private int tamanhoBloco; // Tamanho de cada bloco (em bytes)
    private Bloco[] blocos; // Array representando os blocos de memória
    private Diretorio raiz; // Diretório raiz do sistema de arquivos

    SistemaDeArquivos(int tamanhoMemoria, int tamanhoBloco) {
        this.totalBlocos = tamanhoMemoria / tamanhoBloco; // Calcula o número de blocos
        this.tamanhoBloco = tamanhoBloco;
        this.blocos = new Bloco[totalBlocos];
        this.raiz = new Diretorio("raiz");

        // Inicializa todos os blocos como livres
        for (int i = 0; i < totalBlocos; i++) {
            blocos[i] = new Bloco();
        }
    }

    // Método para criar um novo arquivo
    public void criarArquivo(String nomeDiretorio, String nomeArquivo, int tamanho) {
        int blocosNecessarios = (int) Math.ceil((double) tamanho / tamanhoBloco); // Calcula quantos blocos são necessários
        List<Integer> blocosLivres = encontrarBlocosLivres(blocosNecessarios); // Encontra os blocos livres

        if (blocosLivres.size() < blocosNecessarios) {
            System.out.println("Erro: Espaço insuficiente no sistema de arquivos.");
            return;
        }

        Diretorio dir = encontrarDiretorio(nomeDiretorio); // Localiza o diretório especificado
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }

        // Verifica se um arquivo com o mesmo nome já existe
        for (Arquivo arquivo : dir.arquivos) {
            if (arquivo.nome.equals(nomeArquivo)) {
                System.out.println("Erro: Um arquivo com este nome já existe.");
                return;
            }
        }

        // Cria o novo arquivo e aloca os blocos
        Arquivo arquivo = new Arquivo(nomeArquivo, tamanho);
        for (int i = 0; i < blocosNecessarios; i++) {
            int indiceBloco = blocosLivres.get(i);
            blocos[indiceBloco].estaOcupado = true;
            blocos[indiceBloco].nomeArquivo = nomeArquivo;
            arquivo.blocosAlocados.add(indiceBloco);

            // Marcar fragmentação no último bloco alocado 
            if (i == blocosNecessarios - 1 && tamanho % tamanhoBloco != 0) {
                blocos[indiceBloco].fragmentado = tamanhoBloco - (tamanho % tamanhoBloco);
            }
        }

        dir.arquivos.add(arquivo); // Adiciona o arquivo ao diretório
        System.out.println("Arquivo criado com sucesso.");
    }

    // Método para excluir um arquivo
    public void excluirArquivo(String nomeDiretorio, String nomeArquivo) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio); // Localiza o diretório especificado
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }

        // Procura o arquivo no diretório
        Arquivo arquivoParaRemover = null;
        for (Arquivo arquivo : dir.arquivos) {
            if (arquivo.nome.equals(nomeArquivo)) {
                arquivoParaRemover = arquivo;
                break;
            }
        }

        if (arquivoParaRemover == null) {
            System.out.println("Erro: Arquivo não encontrado.");
            return;
        }

        // Libera os blocos alocados para o arquivo
        for (int indiceBloco : arquivoParaRemover.blocosAlocados) {
            blocos[indiceBloco].estaOcupado = false; // Marca o bloco como livre
            blocos[indiceBloco].nomeArquivo = null; // Remove a associação ao arquivo
            blocos[indiceBloco].fragmentado = 0; // Remove o estado de fragmentação
        }

        dir.arquivos.remove(arquivoParaRemover); // Remove o arquivo do diretório
        System.out.println("Arquivo excluído com sucesso.");
    }

    // Método para mostrar os arquivos de um diretório
    public void mostrarArquivosDiretorio(String nomeDiretorio) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio); // Localiza o diretório especificado
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }

        if (dir.arquivos.isEmpty()) {
            System.out.println("O diretório está vazio.");
        } else {
            System.out.println("Arquivos no diretório '" + nomeDiretorio + "':");
            for (Arquivo arquivo : dir.arquivos) {
                System.out.println("- " + arquivo.nome + " (" + arquivo.tamanho + " KB)");
            }
        }
    }

    // Método para mostrar o estado de alocação dos blocos
    public void mostrarAlocacaoBlocos() {
        System.out.println("Estado dos blocos:");
        for (int i = 0; i < blocos.length; i++) {
            if (blocos[i].estaOcupado) {
                String fragmentacao = blocos[i].fragmentado > 0 ? ", Há fragmentação interna (" + blocos[i].fragmentado + " KB desperdiçados)" : "";
                System.out.println("Bloco " + i + ": Ocupado (" + tamanhoBloco + " KB, " + blocos[i].nomeArquivo + ")" + fragmentacao);
            } else {
                System.out.println("Bloco " + i + ": Livre");
            }
        }
    }

    // Método para verificar a fragmentação externa
    public void verificarFragmentacao() {
        int espacoLivreTotal = 0;
        int maiorSequenciaLivre = 0;
        int sequenciaAtual = 0;

        for (Bloco bloco : blocos) {
            if (!bloco.estaOcupado) {
                espacoLivreTotal += tamanhoBloco;
                sequenciaAtual++;
                maiorSequenciaLivre = Math.max(maiorSequenciaLivre, sequenciaAtual);
            } else {
                sequenciaAtual = 0;
            }
        }

        System.out.println("Fragmentação externa:");
        System.out.println("Espaço livre total: " + espacoLivreTotal + " KB");
        System.out.println("Maior sequência contígua de blocos livres: " + (maiorSequenciaLivre * tamanhoBloco) + " KB");

        if (espacoLivreTotal > 0 && espacoLivreTotal > (maiorSequenciaLivre * tamanhoBloco)) {
            System.out.println("Fragmentação externa detectada: existem blocos livres fragmentados.");
        } else if (espacoLivreTotal > 0) {
            System.out.println("Nenhuma fragmentação externa detectada.");
        } else {
            System.out.println("Sem espaço livre no sistema.");
        }
    }

    // Método auxiliar para localizar um diretório (suporte apenas para "raiz")
    private Diretorio encontrarDiretorio(String nomeDiretorio) {
        if (nomeDiretorio.equals("raiz")) {
            return raiz;
        }
        return null; // Não suporta subdiretórios
    }

    // Método auxiliar para encontrar blocos livres
    private List<Integer> encontrarBlocosLivres(int blocosNecessarios) {
        List<Integer> blocosLivres = new ArrayList<>();
        for (int i = 0; i < blocos.length && blocosLivres.size() < blocosNecessarios; i++) {
            if (!blocos[i].estaOcupado) {
                blocosLivres.add(i);
            }
        }
        return blocosLivres;
    }
}

// Classe principal para interação com o sistema de arquivos
public class SimuladorSA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Simulador de Sistema de Arquivos!");

        System.out.print("Digite o tamanho total da memória (em KB): ");
        int tamanhoMemoria = scanner.nextInt();

        System.out.print("Digite o tamanho de cada bloco (em KB): ");
        int tamanhoBloco = scanner.nextInt();

        SistemaDeArquivos sistema = new SistemaDeArquivos(tamanhoMemoria, tamanhoBloco);

        while (true) {
            System.out.println("--------------------------------------");
            System.out.println("\n1. Criar arquivo");
            System.out.println("2. Excluir arquivo");
            System.out.println("3. Mostrar alocação de blocos");
            System.out.println("4. Mostrar arquivos do diretório");
            System.out.println("5. Sair");
            System.out.println("--------------------------------------");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            System.out.println("--------------------------------------");
        
            switch (opcao) {
                case 1:
                    System.out.print("Diretório (use 'raiz'): ");
                    String dir = scanner.next();
                    System.out.print("Nome do arquivo: ");
                    String nome = scanner.next();
                    System.out.print("Tamanho do arquivo (em KB): ");
                    int tamanho = scanner.nextInt();
                    sistema.criarArquivo(dir, nome, tamanho);
                    break;
                case 2:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.next();
                    System.out.print("Nome do arquivo: ");
                    nome = scanner.next();
                    sistema.excluirArquivo(dir, nome);
                    break;
                case 3:
                    sistema.verificarFragmentacao();
                    System.out.println("--------------------------------------");
                    sistema.mostrarAlocacaoBlocos();
                    break;
                case 4:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.next();
                    sistema.mostrarArquivosDiretorio(dir);
                    break;
                case 5:
                    System.out.println("Encerrando...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }        
    }
}
