# Oficina do Firmamento

`vazio_regalia.bbmodel` contém a revisão esculpida do Vazio: 197 malhas, 6.297 faces, 39 ossos, atlas incorporado e animação ambiente de 12 segundos. Abre em **Generic Model**, com superfícies e pontas realmente poligonais. O manto tem pregas e doze segmentos articulados; o movimento reativo mantém o solver da Astra. Fonte: `tools/vazio_armor.mjs` e `tools/lib/armor_mesh.mjs`; verificação: `node tools/validate_vazio.mjs`. O esqueleto Gecko fica em `geo/armor/vazio.geo.json`; as superfícies ficam em `armor_mesh/vazio.mesh.json` e são ligadas aos ossos pelo `ArmorMeshLoader`. Exportar somente Bedrock não preserva essa geometria. Consulte [../../docs/VAZIO.md](../../docs/VAZIO.md) antes de transferir edições manuais para o jogo. `node tools/preview_geo.mjs vazio caminho.png` mostra a geometria fora do jogo, sem simular movimento ou shaders.

`astra_oracle.bbmodel` é o Oráculo Celestial da escola Astra: 277 cubos, 40 ossos, atlas incorporado e loop de prévia de 12 segundos. Abre como projeto Bedrock no Blockbench. A capa tem quatro articulações em cada uma de três faixas; estolas/laterais têm três articulações cada. O loop do editor mostra uma oscilação de prévia; no jogo, GeckoLib recebe os ângulos da simulação de inércia em Java. Portanto, a resposta a correr/virar/parar não aparece apenas apertando Play no editor. Preserve os nomes e a hierarquia dos ossos. Use `node tools/astra_armor.mjs` para regenerar os assets e `node tools/validate_astra.mjs` para conferir. O atlas emissivo separado fica em `textures/armor/astra_glowmask.png`. Guia completo: [../../docs/ASTRA.md](../../docs/ASTRA.md).

Abra `00_colecao_firmamento.bbmodel` no Blockbench para ver as oito máquinas e os três instrumentos juntos. Cada peça também tem seu próprio projeto Java Block/Item, com texturas incorporadas, grupos nomeados e exportação Minecraft 1.21.1. A coleção é apenas uma prancha de inspeção, não um bloco para exportar ao jogo.

As oito máquinas, Foco Etéreo, Varinha de Enlace e Códice já têm modelos e texturas integrados aos recursos do mod. A primeira coleção foi aberta e inspecionada no Blockbench 5.2.2; depois foram refinados os UVs dos cristais e o contraste da obsidiana. A inspeção dessas revisões no cliente ainda está pendente.

`vigia.bbmodel` contém a geometria original do Vigia em seis grupos articulados. O renderer Java mantém os movimentos existentes de cabeça, caminhada e ataque. A primeira geometria ainda precisa de inspeção no cliente. Não inclui novas animações de combate.

Fontes de autoria: `tools/firmament_models.mjs` e `tools/vigia_model.mjs`. Rode com Node 20 ou superior na raiz do repositório. Esses geradores sobrescrevem seus arquivos de saída; guarde edições manuais do Blockbench em outra cópia antes de regenerar. Para exportar manualmente máquinas/instrumentos, use File > Export > Java Block/Item Model e preserve os nomes dos recursos. Para o Vigia, mudanças na geometria precisam acompanhar o renderer Java.

`node tools/validate_firmament_models.mjs` verifica os 11 modelos de blocos/itens, limites de rotação, UVs, referências de texturas e correspondência entre recursos e projetos. Não é uma avaliação artística nem valida animação.

As texturas e a geometria são originais do projeto. As máquinas continuam com colisão de bloco completo e aparência estática; as cores dos núcleos não indicam a energia atual.

Referência de formato: https://blockbench.net/wiki/docs/bbmodel/
