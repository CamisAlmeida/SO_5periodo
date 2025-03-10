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

---

## **Atividade 3: Simulador de Sistema de Arquivos**  

### **Objetivo:**
Implemente um "mini" simulador de sistemas de arquivos, permitindo que o usuário crie e exclua arquivos e diretórios, além de realizar a listagem de arquivos de um diretório. A simulação deve ser feita utilizando um mecanismo de alocação de sua escolha, como encadeada, indexada, FAT, NTFS, entre outros.

### **Requisitos:**
1. **Alocação de Arquivos e Diretórios:**
   - Permitir que o usuário crie e exclua arquivos e diretórios.
   - A cada operação realizada, simular a alocação de blocos no sistema de arquivos.
   - Indicar a alocação de arquivos e diretórios nos blocos de memória.

2. **Detalhamento dos Arquivos e Diretórios:**
   - Informar o nome e tamanho de cada arquivo e diretório.
   - Não permitir arquivos ou diretórios com nomes iguais.
   - Limitar a estrutura de diretórios a apenas dois níveis (diretório raiz e subdiretórios ou arquivos dentro dele).

3. **Simulação de Fragmentação:**
   - Indicar se há fragmentação interna ou externa no sistema de arquivos após a alocação.

4. **Tamanho de Memória e Blocos:**
   - Permitir configurar o tamanho máximo de memória física (em KB, MB ou outra unidade).
   - Definir o tamanho dos blocos de memória (mesma unidade da memória física).

### **Funcionalidades:**
- **Criação de Arquivos e Diretórios:**
   - O usuário pode criar arquivos, informando o nome e o tamanho.
   - O usuário pode excluir arquivos e diretórios.
   
- **Listagem de Arquivos:**
   - O usuário pode listar os arquivos presentes no diretório.
   
- **Simulação de Alocação e Fragmentação:**
   - A cada operação (criação ou exclusão), o sistema mostra como os blocos estão sendo alocados e se há fragmentação.
   
- **Simulação de Mecanismo de Alocação:**
   - Utilização do **NTFS** ou outro mecanismo de alocação escolhido, com a possibilidade de alocar blocos de forma contígua ou não.

## **Atividade 4: Simulador de Gerenciamento de E/S **  

### **Parte 1: Simulador de Gerenciamento de E/S**  

Implementei um "mini" simulador de gerenciamento de E/S utilizando o algoritmo **SCAN** para escalonamento de braço de disco.  

#### **Funcionalidades implementadas**:  
1. Configuração do intervalo de blocos (e/ou setores) mínimo e máximo em disco.  
2. Definição da ordem de blocos a serem visitados (ou geração aleatória a partir de uma quantidade).  
3. Exibição da ordem dos blocos visitados na prática, conforme o algoritmo SCAN.  
4. Cálculo e exibição do tempo de seek parcial a cada requisição e do tempo total de seek.  
   - O tempo de seek considera uma unidade de tempo por bloco (ex.: mover do bloco 2 ao 5 gasta 3 u.t.).  
   - Inclui o tempo de seek gasto para movimentar a cabeça de disco até as extremidades.  
---

### **Parte 2: Simulação de RAID**  

Implementei a simulação de um sistema de armazenamento utilizando **RAID 5** com **3 discos**.  

#### **Funcionalidades implementadas**:  
1. Distribuição dos dados e paridade entre os 3 discos, conforme o padrão RAID 5.  
2. Exibição da alocação dos blocos de dados e paridade em cada disco.  
3. Manutenção da interface com o usuário inalterada, apenas mostrando o resultado da distribuição nos discos.  


## **Atividade 5: Prática sobre Segurança**

### **Parte 1: Criptografia e Quebra de Senhas**
Foi implementada uma técnica de criptografia para proteger as senhas digitadas pelo usuário (com tamanho máximo de 8 caracteres) e uma técnica para tentar "quebrar" essas senhas, medindo o tempo gasto para a conclusão da quebra. A cifra utilizada foi a Cifra de César.

### **Parte 2: Proteção de Diretórios e Arquivos**
Nesta parte, foi adicionada uma funcionalidade à prática de sistemas de arquivos que permite ao usuário:

- Proteger um determinado diretório ou arquivo com uma senha criptografada.
- Permitir o acesso a esses arquivos ou diretórios apenas mediante a inserção da senha correta.
