import java.util.*;

// Representa um bloco de memória em um disco
class Bloco {
    boolean estaOcupado; // Indica se o bloco está ocupado
    String nomeArquivo;  // Nome do arquivo (ou indicação de paridade)
    int fragmentado;     // Fragmentação interna (em KB)

    Bloco() {
        this.estaOcupado = false;
        this.nomeArquivo = null;
        this.fragmentado = 0;
    }
}

// Representa um disco no sistema RAID 5
class Disco {
    int id;
    Bloco[] blocos;
    
    Disco(int id, int numBlocos) {
        this.id = id;
        this.blocos = new Bloco[numBlocos];
        for (int i = 0; i < numBlocos; i++) {
            this.blocos[i] = new Bloco();
        }
    }
}

// Representa a alocação de um bloco em um disco para um arquivo
class Alocacao {
    int disco;
    int indiceBloco;
    boolean isParidade; // true se o bloco alocado for de paridade

    Alocacao(int disco, int indiceBloco, boolean isParidade) {
        this.disco = disco;
        this.indiceBloco = indiceBloco;
        this.isParidade = isParidade;
    }
}

// Representa um arquivo no sistema de arquivos RAID
class Arquivo {
    String nome;       // Nome do arquivo
    int tamanho;       // Tamanho do arquivo (em KB)
    List<Alocacao> alocacoes; // Lista de alocações (bloco + disco)

    Arquivo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.alocacoes = new ArrayList<>();
    }
}

// Representa um diretório (suporte apenas para "raiz")
class Diretorio {
    String nome;          // Nome do diretório
    List<Arquivo> arquivos; // Lista de arquivos

    Diretorio(String nome) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
    }
}

// Classe principal que simula o sistema de arquivos com RAID 5
class SistemaDeArquivos {
    private int tamanhoBloco; // Tamanho de cada bloco (em KB)
    private Disco[] discos;   // Array de discos
    private Diretorio raiz;   // Diretório raiz
    private int numDiscos;    // Número de discos na simulação

    /*
     O parâmetro 'tamanhoMemoria' representa a capacidade efetiva do RAID (excluindo paridade).
     Em RAID 5 com 3 discos, a capacidade efetiva é de 2 discos. Assim, a capacidade de cada disco
     será: capacidadePorDisco = tamanhoMemoria / (numDiscos - 1)
     */
    SistemaDeArquivos(int tamanhoMemoria, int tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
        // RAID 5 com 3 discos (mínimo para RAID 5)
        this.numDiscos = 3;
        int capacidadePorDisco = tamanhoMemoria / (numDiscos - 1);
        int blocosPorDisco = capacidadePorDisco / tamanhoBloco;

        discos = new Disco[numDiscos];
        for (int i = 0; i < numDiscos; i++) {
            discos[i] = new Disco(i, blocosPorDisco);
        }
        raiz = new Diretorio("raiz");
    }

    // Retorna o índice do primeiro bloco livre em um disco (ou -1 se não houver espaço)
    private int encontrarPrimeiroBlocoLivre(int discoId) {
        Disco disco = discos[discoId];
        for (int i = 0; i < disco.blocos.length; i++) {
            if (!disco.blocos[i].estaOcupado) {
                return i;
            }
        }
        return -1;
    }

    // Localiza o diretório (suporte apenas para "raiz")
    private Diretorio encontrarDiretorio(String nomeDiretorio) {
        if (nomeDiretorio.equals("raiz")) {
            return raiz;
        }
        return null;
    }

    
     //Cria um arquivo utilizando a alocação em RAID 5.
    public void criarArquivo(String nomeDiretorio, String nomeArquivo, int tamanho) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }
        // Verifica se já existe um arquivo com o mesmo nome
        for (Arquivo arq : dir.arquivos) {
            if (arq.nome.equals(nomeArquivo)) {
                System.out.println("Erro: Um arquivo com este nome já existe.");
                return;
            }
        }

        // Calcula quantos blocos de dados serão necessários para armazenar o arquivo
        int blocosDadosNecessarios = (int) Math.ceil((double) tamanho / tamanhoBloco);
        int blocosDadosAlocados = 0;
        int stripeIndex = 0;
        Arquivo arquivo = new Arquivo(nomeArquivo, tamanho);
        int dadosPorStripe = numDiscos - 1; // Cada stripe possui (n-1) blocos para dados

        // Aloca os blocos stripe a stripe
        while (blocosDadosAlocados < blocosDadosNecessarios) {
            int paridadeDisco = stripeIndex % numDiscos;
            // Aloca blocos de dados nos discos (exceto o disco de paridade para esta stripe)
            for (int d = 0; d < numDiscos; d++) {
                if (d == paridadeDisco) continue;
                if (blocosDadosAlocados < blocosDadosNecessarios) {
                    int blocoIndex = encontrarPrimeiroBlocoLivre(d);
                    if (blocoIndex == -1) {
                        System.out.println("Erro: Espaço insuficiente no disco " + d + ".");
                        return;
                    }
                    discos[d].blocos[blocoIndex].estaOcupado = true;
                    discos[d].blocos[blocoIndex].nomeArquivo = nomeArquivo;
                    // Se for o último bloco e não preencher completamente o bloco, marca fragmentação interna
                    if (blocosDadosAlocados == blocosDadosNecessarios - 1 && tamanho % tamanhoBloco != 0) {
                        discos[d].blocos[blocoIndex].fragmentado = tamanhoBloco - (tamanho % tamanhoBloco);
                    }
                    arquivo.alocacoes.add(new Alocacao(d, blocoIndex, false));
                    blocosDadosAlocados++;
                }
            }
            // Aloca o bloco de paridade no disco designado para esta stripe
            int blocoParidade = encontrarPrimeiroBlocoLivre(paridadeDisco);
            if (blocoParidade == -1) {
                System.out.println("Erro: Espaço insuficiente no disco " + paridadeDisco + " para paridade.");
                return;
            }
            discos[paridadeDisco].blocos[blocoParidade].estaOcupado = true;
            discos[paridadeDisco].blocos[blocoParidade].nomeArquivo = nomeArquivo + " (paridade)";
            arquivo.alocacoes.add(new Alocacao(paridadeDisco, blocoParidade, true));

            stripeIndex++;
        }

        dir.arquivos.add(arquivo);
        System.out.println("Arquivo criado com sucesso.");
    }

    // Exclui um arquivo e libera os blocos (dados e paridade) alocados
    public void excluirArquivo(String nomeDiretorio, String nomeArquivo) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }

        Arquivo arquivoParaRemover = null;
        for (Arquivo arq : dir.arquivos) {
            if (arq.nome.equals(nomeArquivo)) {
                arquivoParaRemover = arq;
                break;
            }
        }

        if (arquivoParaRemover == null) {
            System.out.println("Erro: Arquivo não encontrado.");
            return;
        }

        // Libera cada bloco alocado (em cada disco)
        for (Alocacao alloc : arquivoParaRemover.alocacoes) {
            discos[alloc.disco].blocos[alloc.indiceBloco].estaOcupado = false;
            discos[alloc.disco].blocos[alloc.indiceBloco].nomeArquivo = null;
            discos[alloc.disco].blocos[alloc.indiceBloco].fragmentado = 0;
        }

        dir.arquivos.remove(arquivoParaRemover);
        System.out.println("Arquivo excluído com sucesso.");
    }

    // Mostra os arquivos presentes em um diretório
    public void mostrarArquivosDiretorio(String nomeDiretorio) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }

        if (dir.arquivos.isEmpty()) {
            System.out.println("O diretório está vazio.");
        } else {
            System.out.println("Arquivos no diretório '" + nomeDiretorio + "':");
            for (Arquivo arq : dir.arquivos) {
                System.out.println("- " + arq.nome + " (" + arq.tamanho + " KB)");
            }
        }
    }

    // Mostra o estado de alocação dos blocos em cada disco
    public void mostrarAlocacaoBlocos() {
        System.out.println("Estado dos discos:");
        for (int d = 0; d < discos.length; d++) {
            System.out.println("Disco " + d + ":");
            for (int i = 0; i < discos[d].blocos.length; i++) {
                Bloco bloco = discos[d].blocos[i];
                if (bloco.estaOcupado) {
                    String tipo = bloco.nomeArquivo.contains("paridade") ? "Paridade" : "Dados";
                    String info = "Bloco " + i + ": Ocupado (" + tamanhoBloco + " KB, " + bloco.nomeArquivo + ", " + tipo + ")";
                    if (bloco.fragmentado > 0) {
                        info += ", Fragmentação interna: " + bloco.fragmentado + " KB desperdiçados";
                    }
                    System.out.println(info);
                } else {
                    System.out.println("Bloco " + i + ": Livre");
                }
            }
            System.out.println("-----------------------------------");
        }
    }

    // Verifica e mostra a fragmentação externa em cada disco
    public void verificarFragmentacao() {
        System.out.println("Fragmentação externa por disco:");
        for (int d = 0; d < discos.length; d++) {
            int espacoLivreTotal = 0;
            int maiorSequenciaLivre = 0;
            int sequenciaAtual = 0;
            for (Bloco bloco : discos[d].blocos) {
                if (!bloco.estaOcupado) {
                    espacoLivreTotal += tamanhoBloco;
                    sequenciaAtual++;
                    maiorSequenciaLivre = Math.max(maiorSequenciaLivre, sequenciaAtual * tamanhoBloco);
                } else {
                    sequenciaAtual = 0;
                }
            }
            System.out.println("Disco " + d + ": Espaço livre total: " + espacoLivreTotal +
                    " KB, Maior sequência contígua: " + maiorSequenciaLivre + " KB");
        }
    }
}

// Classe principal para interação com o sistema de arquivos RAID 5
public class SimuladorSA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Simulador de Sistema de Arquivos RAID 5!");

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
