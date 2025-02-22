import java.util.*;

public class SimuladorDisco {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Simulador de Gerenciamento de E/S (Algoritmo SCAN) ===");
        
        // Configuração do disco
        System.out.print("Digite o bloco MÍNIMO do disco: ");
        int blocoMinimo = scanner.nextInt();
        System.out.print("Digite o bloco MÁXIMO do disco: ");
        int blocoMaximo = scanner.nextInt();
        
        // Posição inicial da cabeça do disco
        System.out.print("Digite a posição inicial da cabeça: ");
        int posicaoInicial = scanner.nextInt();
        
        // Quantidade de requisições 
        System.out.print("Digite a quantidade de requisições: ");
        int qtdRequisicoes = scanner.nextInt();
        
        // Gera o numero da requisição aleatória 
        Set<Integer> requisicoesSet = new HashSet<>();
            Random random = new Random();
            while (requisicoesSet.size() < qtdRequisicoes) {
                requisicoesSet.add(random.nextInt((blocoMaximo - blocoMinimo) + 1) + blocoMinimo);
            }
        List<Integer> listaRequisicoes = new ArrayList<>(requisicoesSet);

        System.out.println("\nRequisições (não ordenadas): " + listaRequisicoes);
        
        // Separa as requisições em duas listas:
        // - 'requisicoesDireita': blocos maiores ou iguais à posição inicial
        // - 'requisicoesEsquerda': blocos menores que a posição inicial
        List<Integer> requisicoesDireita = new ArrayList<>();
        List<Integer> requisicoesEsquerda = new ArrayList<>();
        
        for (int req : listaRequisicoes) {
            if (req >= posicaoInicial) {
                requisicoesDireita.add(req);
            } else {
                requisicoesEsquerda.add(req);
            }
        }
        
        // Ordena as requisições para a direita em ordem crescente
        Collections.sort(requisicoesDireita);
        // Ordena as requisições para a esquerda em ordem decrescente
        Collections.sort(requisicoesEsquerda, Collections.reverseOrder());
        
        System.out.println("Requisições à direita: " + requisicoesDireita);
        System.out.println("Requisições à esquerda: " + requisicoesEsquerda);
        
        // Lista para armazenar a ordem dos blocos visitados
        List<Integer> ordemVisita = new ArrayList<>();
        
        int tempoTotal = 0;
        int posicaoAtual = posicaoInicial;
        
        System.out.println("\n--- Movimentação da cabeça ---");
        
        // Parte 1: Movimenta a cabeça para a direita, atendendo as requisições
        for (int req : requisicoesDireita) {
            int tempoParcial = Math.abs(req - posicaoAtual);
            tempoTotal += tempoParcial;
            System.out.println("Movendo de " + posicaoAtual + " para " + req + " -> tempo de seek: " + tempoParcial + " u.t.");
            posicaoAtual = req;
            ordemVisita.add(req);
        }
        
        // Após atender as requisições à direita, vai até o extremo direito (blocoMaximo)
        if (posicaoAtual != blocoMaximo) {
            int tempoParcial = Math.abs(blocoMaximo - posicaoAtual);
            tempoTotal += tempoParcial;
            System.out.println("Movendo de " + posicaoAtual + " para " + blocoMaximo + " (extremo direito) -> tempo de seek: " + tempoParcial + " u.t.");
            posicaoAtual = blocoMaximo;
            ordemVisita.add(blocoMaximo);
        }
        
        // Parte 2: Inverte a direção e atende as requisições à esquerda
        for (int req : requisicoesEsquerda) {
            int tempoParcial = Math.abs(posicaoAtual - req);
            tempoTotal += tempoParcial;
            System.out.println("Movendo de " + posicaoAtual + " para " + req + " -> tempo de seek: " + tempoParcial + " u.t.");
            posicaoAtual = req;
            ordemVisita.add(req);
        }
        
        // Após atender as requisições à esquerda, vai até o extremo esquerdo (blocoMinimo)
        if (posicaoAtual != blocoMinimo) {
            int tempoParcial = Math.abs(posicaoAtual - blocoMinimo);
            tempoTotal += tempoParcial;
            System.out.println("Movendo de " + posicaoAtual + " para " + blocoMinimo + " (extremo esquerdo) -> tempo de seek: " + tempoParcial + " u.t.");
            posicaoAtual = blocoMinimo;
            ordemVisita.add(blocoMinimo);
        }
        
        System.out.println("\nOrdem dos blocos visitados: " + ordemVisita);
        System.out.println("Tempo total de seek: " + tempoTotal + " u.t.");
    }
}
