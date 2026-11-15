# Etheris — Ecos do Infinito

Versão 0.5.0-alpha para Minecraft Java 1.21.1 com NeoForge 21.1.172 e Java 21. O GDD completo ainda não está implementado.

## Novidades da versão 0.5

A Câmara de Transmutação é a oitava máquina e o primeiro multibloco. Ela oferece uma rota de engenharia para materiais: quatro Cristais Estáveis + 160 Éter produzem uma Pérola do Ender; oito quartzos + 80 Éter produzem uma ametista. Cada operação leva um ciclo de cinco segundos. Os valores e a montagem são decisões provisórias de implementação, pois o GDD não define receitas exatas.

Monte uma base 3×3 de **ardósia polida**, deixando o centro livre para um funil de saída (oito blocos obrigatórios). Sobre ela, coloque a Câmara no centro, quatro blocos de ametista nos cantos e quatro blocos de cobre cortado nos lados. Todas as oxidações, com ou sem cera, são aceitas. Um funil acima da Câmara fornece a entrada. Vista superior:

```text
A C A     A = bloco de ametista
C M C     C = cobre cortado
A C A     M = Câmara
```

Ao obter Cristal Estável, a receita da Câmara aparece no livro de receitas. Ela usa dois cristais, uma runa, dois lingotes de ouro, uma fornalha potente e três ardósias polidas. Carregue com fragmentos ou enlace, insira reagentes pelo painel/funil e retire a produção. O painel distingue estrutura incompleta, falta de reagentes, falta de Éter e saída cheia. Reparar a estrutura retoma uma fila pendente em até cinco segundos, sem consumir materiais durante a falha. Sem fila, enlaces ou painel aberto, a Câmara não faz verificações periódicas.

Há dois novos avanços: obter a Câmara e registrar uma transmutação real. Depois de produzir, abra o painel ou retire o produto pelo menu. A prova é compartilhável: visitar uma Câmara pública que já operou permite registrar a descoberta. Ela persiste no mundo, mas não torna o tier III completo. O Códice passa a 14 páginas e inclui a montagem; fabrique um novo para atualizar o texto.

Os testes agora usam `run-gametest`, separado do cliente em `run`, evitando disputa pelos arquivos de log e pelos mundos. A aparência da Câmara ainda usa texturas vanilla e não foi inspecionada no cliente.

Validação final da 0.5: compilação concluída e **21 GameTests aprovados**, incluindo produção por funis, montagem quebrada/reparada, lotes, limites de saída, persistência e registro da conquista. Esses testes não substituem uma partida de sobrevivência ou multiplayer.

## Novidades da versão 0.4

Todas as máquinas têm um painel: clique com a mão vazia para abrir. Ele mostra Éter, entrada, saída e o motivo de espera, com botões para pausar/retomar e desconectar os enlaces de saída. Shift + clique em um item transfere entre o inventário e a máquina. Agachar e interagir com a mão vazia mantém a retirada rápida de produtos.

Pausa manual e sinal de redstone ligado suspendem coleta, envio de Éter e processamento automatizado. Funis, recebimento de energia e trocas manuais continuam funcionando. A pausa manual persiste ao salvar o mundo. Remover o sinal ou retomar pelo painel reativa a máquina. As ações são verificadas no servidor, incluindo distância e permissão de interação.

Validação: compilação e 15 GameTests de servidor passaram. O cliente iniciou e o painel da Forja foi observado, sem sobreposição de controles na resolução de teste. A conferência interativa foi interrompida; os botões foram validados pelos testes de servidor, não por uma sessão visual completa. Ainda falta uma partida completa de sobrevivência e multiplayer.

## Novidades da versão 0.3

Sete máquinas, automação por funis e cadeia de refino. O Triturador transforma ferro bruto em dois pós de ferro (30 Éter), ou ametista em dois pós de cristal (20). A Refinaria transforma um pó de ferro em lingote (20), ou um pó de cristal em Cristal Estável (40). Insira reagentes usando funis ou clique com o material nas duas máquinas novas; retire a produção com funil ou pelo painel. Coletor e Forja mantêm as trocas manuais e também aceitam reagentes por funis.

O processamento automatizado realiza no máximo uma operação por ciclo de 100 ticks. Sem energia ou espaço na saída, nada é consumido. Os dois slots e a energia persistem juntos; quebrar a máquina derruba o inventário, mas descarta a energia.

O Nó de Distribuição permite quatro enlaces explícitos, servidos em ordem alternada, com orçamento total de 8 Éter por ciclo. Repetir um enlace do nó o remove. Outros dispositivos continuam com uma saída. As redes permanecem públicas e não transportam itens.

Runas aprendem as quatro magias após a inicial. As últimas três exigem Cristais Estáveis. A aba de avanços Etheris acompanha seis objetivos de descoberta e engenharia. O Códice passa a ter 12 páginas; fabrique um novo para receber o texto atualizado. O foco exibe sua magia e energia na barra de ação a cada dois segundos.

Os sete dispositivos receberam modelos JSON próprios com texturas vanilla. A arte ainda é provisória, sem animações ou revisão visual completa de todos os modelos.

## Estado atual

- Oito magias: Faísca Astral, Olho do Firmamento, Toque Vital, Transmutar Fragmento, Impulso de Fluxo, Lança Fulgente, Semente Quimérica e Liga de Éter.
- Éter interior, seleção e aprendizado persistem após morte; custos e recargas são verificados no servidor.
- Coletor capta Éter sob céu aberto e condensa ametista em fragmentos.
- Forja troca cobre e Éter por runas; Foco de Vinculação aprende magias e recarrega jogadores; Reservatório guarda até 1000 Éter.
- Vara de Enlace transfere energia entre máquinas a até 8 blocos, sem forçar carregamento de chunks.
- Ruína astral em terreno plano de novos chunks do Overworld, com saque de materiais.
- Códice fabricável (livro + ametista): primeiro uso transforma em livro escrito; segundo uso abre as páginas. Guia alternativo: `/etheris codex`.
- Assets visuais ainda são placeholders de desenvolvimento; a direção final depende do pipeline de arte/Blockbench.

## Execução

Use Java 21 e execute `gradlew runClient` para abrir o cliente de desenvolvimento. Para validar, execute `gradlew build runGameTestServer`. No Linux use `bash ./gradlew`. Confira também `All N required tests passed` no log: uma saída do Gradle sem erros não basta para provar que o servidor testou o mod.

Para jogar, instale Minecraft 1.21.1 com NeoForge 21.1.172 e coloque o JAR de `build/libs` na pasta `mods`, no cliente e no servidor. Use um mundo de teste com backup. O cliente e um painel foram inspecionados; ainda não foi validada uma sessão multiplayer com duas pessoas.

## Loop inicial

Colete cobre e ametista; fabrique o Coletor e coloque sob céu aberto. Ele coleta 2 Éter de dia ou 4 à noite a cada 5 segundos. Use ametista nele para gastar 20 Éter e obter um fragmento. Carregue a Forja com fragmentos e use cobre para criar runas (20 Éter). O Foco de Vinculação aprende as quatro magias após a inicial usando runas; as três restantes usam Cristais Estáveis. Cada aprendizado custa 40 Éter. Agache e use o foco pessoal no ar para selecionar; use normalmente para conjurar. Consulte as páginas do Códice para reagentes.

Máquinas são públicas. Quebrá-las perde a energia armazenada e derruba os itens internos. Máquinas comuns aceitam um enlace de saída; o Nó aceita quatro. Ainda faltam prioridades configuráveis, proteção por proprietário, transmissão de itens entre enlaces e estabilidade regional.

## Desenvolvimento e arte

Abra `build.gradle` no IntelliJ e selecione JDK 21. Arte vanilla é usada explicitamente como provisória; ainda não existem modelos `.bbmodel` ou animações finais. Os modelos JSON estão em `src/main/resources/assets/etheris/models`. Não são equivalentes às armaduras e megamáquinas previstas no GDD.

## Marcos

1. Fundação e primeiro loop jogável (esta branch).
2. Códice, descoberta da primeira ruína e oito magias do vertical slice.
3. Receitas e processamento real das quatro máquinas.
4. Rede de Éter, primeira estrutura multibloco e progressão I–III.
5. Conteúdo pós-MVP: lacaios, armaduras, dimensões e megamáquinas.
