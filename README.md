# Etheris — Ecos do Infinito

Versão 0.2.0-alpha para Minecraft Java 1.21.1 com NeoForge 21.1.172 e Java 21. O GDD completo ainda não está implementado.

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

Para jogar, instale Minecraft 1.21.1 com NeoForge 21.1.172 e coloque o JAR de `build/libs` na pasta `mods`, no cliente e no servidor. Use um mundo de teste com backup. Não foram feitos testes visuais de cliente nem uma sessão multiplayer com duas pessoas.

## Loop inicial

Colete cobre e ametista; fabrique o Coletor e coloque sob céu aberto. Ele coleta 2 Éter de dia ou 4 à noite a cada 5 segundos. Use ametista nele para gastar 20 Éter e obter um fragmento. Carregue a Forja com fragmentos e use cobre para criar runas (20 Éter). O Foco de Vinculação aprende uma nova magia por runa e 40 Éter. Agache e use o foco pessoal no ar para selecionar; use normalmente para conjurar. Consulte as páginas do Códice para reagentes.

Máquinas são públicas e têm interação manual. Quebrá-las perde a energia armazenada. Cada máquina aceita um enlace de saída, reconfigurável pela vara. Não há inventários automatizados, prioridades, proteção por proprietário, transmissão de itens ou estabilidade regional ainda.

## Desenvolvimento e arte

Abra `build.gradle` no IntelliJ e selecione JDK 21. Arte vanilla é usada explicitamente como provisória; ainda não existem modelos `.bbmodel` ou animações finais. Os modelos JSON estão em `src/main/resources/assets/etheris/models`. Não são equivalentes às armaduras e megamáquinas previstas no GDD.

## Marcos

1. Fundação e primeiro loop jogável (esta branch).
2. Códice, descoberta da primeira ruína e oito magias do vertical slice.
3. Receitas e processamento real das quatro máquinas.
4. Rede de Éter, primeira estrutura multibloco e progressão I–III.
5. Conteúdo pós-MVP: lacaios, armaduras, dimensões e megamáquinas.
