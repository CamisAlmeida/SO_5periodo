import java.security.MessageDigest;
import java.util.*;

// Classe utilitária para criptografia de senhas utilizando SHA-256
class Criptografia {
    public static String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

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

// Representa um arquivo no sistema de arquivos RAID, com opção de proteção por senha
class Arquivo {
    String nome;              // Nome do arquivo
    int tamanho;              // Tamanho do arquivo (em KB)
    List<Alocacao> alocacoes; // Lista de alocações (bloco + disco)
    String senhaHash;         // Armazena o hash da senha, se protegido (null se não protegido)

    Arquivo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.alocacoes = new ArrayList<>();
        this.senhaHash = null;
    }
    
    // Define a senha de proteção (armazenada como hash)
    public void setSenha(String senha) {
        this.senhaHash = Criptografia.hashSenha(senha);
    }
    
    // Verifica se a senha informada é correta
    public boolean verificarSenha(String senha) {
        if (senhaHash == null) return true; // não protegido
        return senhaHash.equals(Criptografia.hashSenha(senha));
    }
}

// Representa um diretório (suporte apenas para "raiz") com opção de proteção por senha
class Diretorio {
    String nome;             // Nome do diretório
    List<Arquivo> arquivos;  // Lista de arquivos
    String senhaHash;        // Hash da senha de proteção (null se não protegido)

    Diretorio(String nome) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
        this.senhaHash = null;
    }
    
    public void setSenha(String senha) {
        this.senhaHash = Criptografia.hashSenha(senha);
    }
    
    public boolean verificarSenha(String senha) {
        if (senhaHash == null) return true;
        return senhaHash.equals(Criptografia.hashSenha(senha));
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

    // Cria um arquivo utilizando a alocação em RAID 5.
    // O parâmetro 'senha' é opcional; se não for nulo ou vazio, o arquivo será protegido.
    public void criarArquivo(String nomeDiretorio, String nomeArquivo, int tamanho, String senha) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }
        // Se o diretório estiver protegido, solicita a senha
        if (dir.senhaHash != null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Diretório protegido. Digite a senha: ");
            String senhaDir = sc.nextLine();
            if (!dir.verificarSenha(senhaDir)) {
                System.out.println("Senha incorreta. Ação negada.");
                return;
            }
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
        // Se o usuário definiu uma senha, protege o arquivo
        if (senha != null && !senha.isEmpty()) {
            arquivo.setSenha(senha);
        }
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

        // Se o arquivo estiver protegido, solicita a senha
        if (arquivoParaRemover.senhaHash != null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Arquivo protegido. Digite a senha: ");
            String senhaArquivo = sc.nextLine();
            if (!arquivoParaRemover.verificarSenha(senhaArquivo)) {
                System.out.println("Senha incorreta. Ação negada.");
                return;
            }
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

    // Mostra os arquivos presentes em um diretório (solicitando senha se o diretório estiver protegido)
    public void mostrarArquivosDiretorio(String nomeDiretorio) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }
        // Se o diretório estiver protegido, solicita a senha
        if (dir.senhaHash != null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Diretório protegido. Digite a senha: ");
            String senhaDir = sc.nextLine();
            if (!dir.verificarSenha(senhaDir)) {
                System.out.println("Senha incorreta. Acesso negado.");
                return;
            }
        }

        if (dir.arquivos.isEmpty()) {
            System.out.println("O diretório está vazio.");
        } else {
            System.out.println("Arquivos no diretório '" + nomeDiretorio + "':");
            for (Arquivo arq : dir.arquivos) {
                System.out.println("- " + arq.nome + " (" + arq.tamanho + " KB)" +
                        (arq.senhaHash != null ? " [PROTEGIDO]" : ""));
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
    
    // Permite proteger um diretório
    public void protegerDiretorio(String nomeDiretorio, String senha) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }
        dir.setSenha(senha);
        System.out.println("Diretório protegido com sucesso.");
    }
    
    // Acesso a um arquivo protegido
    public void acessarArquivo(String nomeDiretorio, String nomeArquivo) {
        Diretorio dir = encontrarDiretorio(nomeDiretorio);
        if (dir == null) {
            System.out.println("Erro: Diretório não encontrado.");
            return;
        }
        // Se o diretório estiver protegido, solicita a senha
        if (dir.senhaHash != null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Diretório protegido. Digite a senha: ");
            String senhaDir = sc.nextLine();
            if (!dir.verificarSenha(senhaDir)) {
                System.out.println("Senha incorreta.");
                return;
            }
        }
        Arquivo arquivo = null;
        for (Arquivo arq : dir.arquivos) {
            if (arq.nome.equals(nomeArquivo)) {
                arquivo = arq;
                break;
            }
        }
        if (arquivo == null) {
            System.out.println("Erro: Arquivo não encontrado.");
            return;
        }
        // Se o arquivo estiver protegido, solicita a senha
        if (arquivo.senhaHash != null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Arquivo protegido. Digite a senha: ");
            String senhaArquivo = sc.nextLine();
            if (!arquivo.verificarSenha(senhaArquivo)) {
                System.out.println("Senha incorreta.");
                return;
            }
        }
        System.out.println("Arquivo acessado.");
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
            System.out.println("3. Mostrar alocação de blocos e fragmentação");
            System.out.println("4. Mostrar arquivos do diretório");
            System.out.println("5. Sair");
            System.out.println("6. Proteger diretório");
            System.out.println("7. Acessar arquivo");
            System.out.println("--------------------------------------");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // limpar buffer
            System.out.println("--------------------------------------");

            switch (opcao) {
                case 1:
                    System.out.print("Diretório (use 'raiz'): ");
                    String dir = scanner.nextLine();
                    System.out.print("Nome do arquivo: ");
                    String nome = scanner.nextLine();
                    System.out.print("Tamanho do arquivo (em KB): ");
                    int tamanho = scanner.nextInt();
                    scanner.nextLine(); // limpar buffer
                    System.out.print("Deseja proteger este arquivo com senha? (s/n): ");
                    String opcaoProtecao = scanner.nextLine();
                    String senhaArquivo = null;
                    if (opcaoProtecao.equalsIgnoreCase("s")) {
                        System.out.print("Digite a senha para proteger o arquivo: ");
                        senhaArquivo = scanner.nextLine();
                    }
                    sistema.criarArquivo(dir, nome, tamanho, senhaArquivo);
                    break;
                case 2:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.nextLine();
                    System.out.print("Nome do arquivo: ");
                    nome = scanner.nextLine();
                    sistema.excluirArquivo(dir, nome);
                    break;
                case 3:
                    sistema.verificarFragmentacao();
                    System.out.println("--------------------------------------");
                    sistema.mostrarAlocacaoBlocos();
                    break;
                case 4:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.nextLine();
                    sistema.mostrarArquivosDiretorio(dir);
                    break;
                case 5:
                    System.out.println("Encerrando...");
                    scanner.close();
                    return;
                case 6:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.nextLine();
                    System.out.print("Digite a senha para proteger o diretório: ");
                    String senhaDir = scanner.nextLine();
                    sistema.protegerDiretorio(dir, senhaDir);
                    break;
                case 7:
                    System.out.print("Diretório (use 'raiz'): ");
                    dir = scanner.nextLine();
                    System.out.print("Nome do arquivo: ");
                    nome = scanner.nextLine();
                    sistema.acessarArquivo(dir, nome);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}
