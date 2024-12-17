# Atividades de Sistemas Operacionais  

Este repositório contém a implementação e apresentação de desafios relacionados à disciplina de Sistemas Operacionais, realizados individualmente ou em dupla, utilizando a linguagem de programação de sua escolha.  

Cada atividade está dividida em partes específicas, conforme descrito abaixo:  

---

## **Atividade 1: Programação Concorrente e Escalonamento de Processos**  

### **Parte 1: Threads Concorrentes com Recursos Compartilhados**  

Implemente um programa com:  
- **Cinco threads** concorrendo simultaneamente ao acesso de **dois recursos compartilhados** (como variáveis globais, buffers, etc.).  
- Apenas **uma thread por vez** pode acessar cada recurso compartilhado, garantindo a exclusão mútua (utilize qualquer técnica apropriada).  

#### **Requisitos**:  
1. Demonstre as condições de corrida (com logs, prints, gráficos, etc.).  
2. Prove que a exclusão mútua está funcionando corretamente.  
3. Sugestão: configure uma troca de threads a cada **3 segundos**, simulando o uso do recurso por uma thread enquanto as demais aguardam (bloqueadas ou em espera ocupada).  
4. Utilize um exemplo como o **Produtor/Consumidor**, mas considerando dois buffers.  

---

### **Parte 2: Mini Simulador de Escalonamento de Processos**  

Implemente um simulador de escalonamento preemptivo com as seguintes funcionalidades:  

#### **Funcionalidades**:  
- Permitir que o usuário (via linha de comando):  
  1. **Crie processos**, fornecendo:  
     - ID  
     - Nome  
     - Prioridade  
     - Tipo (I/O bound ou CPU bound)  
     - Tempo total de CPU (em unidades de tempo, ex.: 1 a 10 ms)  
  2. Escolha entre dois **algoritmos de escalonamento** (um por integrante, caso em dupla).  
  3. Defina o **tempo de quantum** da preempção (em unidades de tempo, ex.: 1 a 10 ms).  

- Adicionar cada processo criado na fila de **"prontos"**, com atualização dinâmica da lista.  
- **Iniciar a execução**, mostrando:  
  - Qual processo está ativo na CPU e por quanto tempo.  
  - Preempção do processo.  
  - Processos aguardando na fila.  
  - Ordem de execução conforme o algoritmo escolhido.  
- **Finalizar a execução**, indicando:  
  - **Tempo de turnaround** de cada processo.  
  - **Tempo médio de espera** de todos os processos.  

#### **Apresentação**  
Grave um vídeo explicando o código e a execução. Caso seja feito em dupla, cada integrante deve apresentar sua parte.  

#### **Avaliação**  
- A nota é individual, valendo **25% da 1ª VA**.  
- Se feito em dupla, cada integrante será avaliado pela parte apresentada.  

---

## **Atividade 2: Gerenciamento de Memória**  

### **Parte 1: Alocação de Memória com Partições Fixas ou Variáveis**  

Implemente (sem necessidade de interface gráfica) a alocação de memória com partições fixas ou variáveis. Deve ser possível configurar/alterar:  
1. **Tamanho máximo de memória física** (em qualquer unidade, MB, KB, etc.).  
2. Se partições fixas, definir previamente o tamanho das partições (na mesma unidade).  
3. Criar um conjunto de processos de duração "infinita" (thread ou simulado) com nome, ID e tamanho.  
4. Escolher UM (ou DOIS, caso em dupla) algoritmo de alocação de memória, como:  
   - First-Fit  
   - Best-Fit  
   - Worst-Fit  

#### **Comportamento esperado**:  
- Mostrar a alocação da memória (estado, fragmentação interna/externa, posições livres/alocadas).  
- Caso a memória esteja cheia, realizar compactação (somente para partições variáveis). Persistindo a falta de memória:  
  - Realizar swapping, removendo aleatoriamente um processo para a memória secundária.  

---

### **Parte 2: Alocação de Memória com Paginação**  

Implemente a alocação de memória com paginação. Deve ser possível configurar/alterar:  
1. **Tamanho máximo de memória física e virtual** (em qualquer unidade).  
2. Definir o tamanho das páginas e calcular a quantidade necessária para cada processo.  
3. Criar um conjunto de processos de duração "infinita" (thread ou simulado) com nome, ID e tamanho.  
4. Escolher UM (ou DOIS, caso em dupla) algoritmo de substituição de páginas, como:  
   - FIFO  
   - LRU  
   - Relógio  
   - Segunda chance  

#### **Comportamento esperado**:  
- Mostrar a alocação da memória (estado, fragmentação interna, páginas livres/alocadas).  
- Caso a memória esteja cheia, aplicar o algoritmo de substituição de páginas escolhido.  
- Ao final da execução, exibir a quantidade de **page misses**.  

