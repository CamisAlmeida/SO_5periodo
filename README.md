# Atividade de Programação Concorrente e Escalonamento de Processos  

Este repositório contém a implementação e apresentação de dois desafios propostos, realizados individualmente ou em dupla, utilizando a linguagem de programação de sua escolha. A atividade está dividida em duas partes principais:

---

## **Parte 1: Threads Concorrentes com Recursos Compartilhados**

Implemente um programa com:  
- **Cinco threads** concorrendo simultaneamente ao acesso de **dois recursos compartilhados** (como variáveis globais, buffers, etc.).  
- Apenas **uma thread por vez** pode acessar cada recurso compartilhado, garantindo a exclusão mútua (utilize qualquer técnica apropriada).  

### Requisitos:  
1. Demonstre as condições de corrida (com logs, prints, gráficos, etc.).  
2. Prove que a exclusão mútua está funcionando corretamente.  
3. Sugestão: configure uma troca de threads a cada **3 segundos**, simulando o uso do recurso por uma thread enquanto as demais aguardam (bloqueadas ou em espera ocupada).  
4. Utilize um exemplo como o **Produtor/Consumidor**, mas considerando dois buffers.  

---

## **Parte 2: Mini Simulador de Escalonamento de Processos**

Implemente um simulador de escalonamento preemptivo com as seguintes funcionalidades:  

### Funcionalidades:  
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

---

## **Apresentação**  
Grave um vídeo explicando o código e a execução. Caso seja feito em dupla, cada integrante deve apresentar sua parte.  

### **Avaliação**  
- A nota é individual, valendo **25% da 1ª VA**.  
- Se feito em dupla, cada integrante será avaliado pela parte apresentada.
