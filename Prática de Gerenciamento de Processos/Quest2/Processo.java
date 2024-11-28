public class Processo {
    String id;
    String nome;
    int prioridade;
    boolean ioBound; // true = I/O bound, false = CPU bound
    int tempoCpuTotal; // Tempo total necessário na CPU
    int tempoRestante; // Tempo restante para conclusão
    int tempoEspera = 0; // Tempo total de espera
    int tempoTurnaround = 0; // Tempo de turnaround

    public Processo(String id, String nome, int prioridade, boolean ioBound, int tempoCpuTotal) {
        this.id = id;
        this.nome = nome;
        this.prioridade = prioridade;
        this.ioBound = ioBound;
        this.tempoCpuTotal = tempoCpuTotal;
        this.tempoRestante = tempoCpuTotal;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Nome: %s, Prioridade: %d, Tipo: %s, Tempo Restante: %d",
                id, nome, prioridade, ioBound ? "I/O-bound" : "CPU-bound", tempoRestante);
    }
}
