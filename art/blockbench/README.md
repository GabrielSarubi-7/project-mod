# Oficina do Firmamento

Abra `00_colecao_firmamento.bbmodel` no Blockbench para ver as oito máquinas e os três instrumentos juntos. Cada peça também tem seu próprio projeto Java Block/Item, com texturas incorporadas, grupos nomeados e exportação Minecraft 1.21.1. A coleção é apenas uma prancha de inspeção, não um bloco para exportar ao jogo.

As oito máquinas, Foco Etéreo, Varinha de Enlace e Códice já têm modelos e texturas integrados aos recursos do mod. A primeira coleção foi aberta e inspecionada no Blockbench 5.2.2; depois foram refinados os UVs dos cristais e o contraste da obsidiana. A inspeção dessas revisões no cliente ainda está pendente.

`vigia.bbmodel` contém a geometria original do Vigia em seis grupos articulados. O renderer Java mantém os movimentos existentes de cabeça, caminhada e ataque. A primeira geometria ainda precisa de inspeção no cliente. Não inclui novas animações de combate.

Fontes de autoria: `tools/firmament_models.mjs` e `tools/vigia_model.mjs`. Rode com Node 20 ou superior na raiz do repositório. Esses geradores sobrescrevem seus arquivos de saída; guarde edições manuais do Blockbench em outra cópia antes de regenerar. Para exportar manualmente máquinas/instrumentos, use File > Export > Java Block/Item Model e preserve os nomes dos recursos. Para o Vigia, mudanças na geometria precisam acompanhar o renderer Java.

`node tools/validate_firmament_models.mjs` verifica os 11 modelos de blocos/itens, limites de rotação, UVs, referências de texturas e correspondência entre recursos e projetos. Não é uma avaliação artística nem valida animação.

As texturas e a geometria são originais do projeto. As máquinas continuam com colisão de bloco completo e aparência estática; as cores dos núcleos não indicam a energia atual.

Referência de formato: https://blockbench.net/wiki/docs/bbmodel/
