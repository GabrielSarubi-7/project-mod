# Vazio — Coroa da Ausência

Prévia 0.7.0-preview.4 · Minecraft Java 1.21.1 · NeoForge 21.1.172 · GeckoLib NeoForge 4.9.3 · Java 21.

## Testar

Instale `etheris-0.7.0-preview.4.jar` junto com `geckolib-neoforge-1.21.1-4.9.3.jar` na pasta `mods`. Mantenha apenas uma versão do Etheris. No ambiente de desenvolvimento, `gradlew runClient` já inclui a dependência. Esta versão também contém a Astra aprovada anteriormente.

Em um mundo com comandos habilitados, use **`/etheris vazio`**. Equipe as quatro peças e pressione F5. Os itens também estão na aba criativa Etheris. O comando não altera os equipamentos atuais: coloca os itens no inventário e derruba excedentes.

## Aparência e movimento

- Coroa quebrada de metal violeta, três fragmentos que flutuam e rosto sem feições, com uma única estrela luminosa.
- Eclipse negro no peito: disco opaco cercado por uma borda emissiva e uma íris interrompida que gira devagar.
- Ombreiras angulares, placas sobrepostas, manoplas pesadas e detalhes luminosos pontuais.
- Capa com doze segmentos, rasgos reais na geometria e bordas violetas. Levanta ao correr, acompanha curvas com atraso e assenta após parar.
- Laterais e tabardo frontal divididos em três partes. Lâminas dos punhos têm movimento menor que o tecido.
- Brilho pulsante em cristais, fendas e pontas do manto, sem iluminar os blocos ao redor. O centro do eclipse e a máscara continuam negros.

## Conferência rápida

1. Observe frente/costas em F5, especialmente máscara, eclipse e silhueta da coroa.
2. Caminhe, corra, faça curvas e pare. Observe o atraso da ponta da capa e a dobra das vestes.
3. Agache, pule e mova os braços. Confira interseções do tecido com pernas/armas.
4. Tire somente o capacete: coroa/fragmentos/fenda devem sumir. Tire somente o peitoral: capa, núcleo e ombreiras devem sumir.
5. Compare de dia e à noite. A luz deve ficar nas fendas e bordas, sem clarear todo o metal ou preencher o eclipse negro.

A construção usa articulações amortecidas, sem colisão completa de tecido nem substituição do esqueleto vanilla. Compatibilidade visual com elytra, capas cosméticas, braços finos e shaders ainda precisa de teste. Não há receita de sobrevivência, bônus de escola ou novas mecânicas de Vazio nesta prévia. As quatro peças somam 20 de defesa e usam Cristal Estável para reparo, como balanceamento provisório.

## Fonte editável e validação

Abra `vazio_regalia.bbmodel` no Blockbench. O arquivo contém atlas, grupos, UVs e loop ambiente. No pacote ZIP também estão `.geo.json`, `.animation.json`, textura e máscara emissiva. Preserve os nomes dos ossos `armor*`, das articulações e dos fragmentos; o renderer os usa para separar equipamentos e aplicar movimento.

A prévia do Blockbench não reproduz a resposta a correr/parar: isso é calculado por Java no jogo. `tools/vazio_armor.mjs` gera os recursos e sobrescreve suas saídas; salve edições manuais em uma cópia. `tools/validate_vazio.mjs` confere recursos e emissão. `gradlew build runGameTestServer` compila e verifica lógica e estabilidade do movimento.

Nesta entrega passaram build, 30 GameTests, testes de movimento e validações de assets. A imagem de apresentação foi renderizada da geometria real, fora do jogo, com iluminação aproximada. A inspeção interativa foi interrompida pelo Esc do usuário e não foi retomada; aparência e animação no cliente ainda aguardam sua avaliação.
