# Vazio — Coroa da Ausência

Prévia 0.7.0-preview.5 — revisão esculpida · Minecraft Java 1.21.1 · NeoForge 21.1.172 · GeckoLib NeoForge 4.9.3 · Java 21.

Esta revisão substitui os cuboides largos por superfícies poligonais: casco envolvente de oito lados, coroa curva e afilada, placas chanfradas, engaste circular, lamelas finas e capa com pregas contínuas. O atlas também ganhou variação de metal e degradê no tecido. A mudança é na geometria do jogo, não somente na imagem de apresentação.

## Testar

Instale `etheris-0.7.0-preview.5.jar` junto com `geckolib-neoforge-1.21.1-4.9.3.jar` na pasta `mods`. Mantenha apenas uma versão do Etheris. No ambiente de desenvolvimento, `gradlew runClient` já inclui a dependência. Esta versão também contém a Astra aprovada anteriormente.

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

Abra `vazio_regalia.bbmodel` no Blockbench como **Generic Model**. O arquivo contém 197 peças em malha editável, atlas, grupos, UVs e loop ambiente. São 6.297 faces, equivalentes a 8.818 triângulos antes da passagem emissiva. No pacote ZIP estão também o esqueleto `.geo.json`, as superfícies `vazio.mesh.json`, `.animation.json`, textura e máscara emissiva. Preserve os nomes dos ossos `armor*`, das articulações e dos fragmentos.

O formato Bedrock/Gecko padrão não exporta estas superfícies poligonais. O mod usa `ArmorMeshLoader` para converter os polígonos em faces ligadas aos ossos do GeckoLib, mantendo animação, separação por slot e brilho. **Não substitua os arquivos do jogo por uma exportação Bedrock vazia.** As fontes reproduzíveis são `tools/vazio_armor.mjs` e `tools/lib/armor_mesh.mjs`; para integrar alterações feitas à mão no Blockbench, é preciso transferi-las também para o recurso de superfícies. Uma cópia do projeto editada no Blockbench não é lida automaticamente pelo jogo.

A prévia do Blockbench não reproduz a resposta a correr/parar: isso é calculado por Java no jogo. `tools/vazio_armor.mjs` gera os recursos e sobrescreve suas saídas; salve edições manuais em uma cópia. `tools/validate_vazio.mjs` confere recursos e emissão. `gradlew build runGameTestServer` compila e verifica lógica e estabilidade do movimento.

Nesta entrega passaram build, 30 GameTests, testes de movimento e validações de assets. O teste do carregador usa o código real do jogo e verifica as 6.297 faces, normais, ordem dos vértices após espelhamento, UVs, separação dos oito slots, recarga e ausência de duplicação. A imagem de apresentação foi renderizada dos polígonos reais fora do jogo, com iluminação aproximada. Aparência, movimentação e desempenho com muitos jogadores ainda aguardam avaliação no cliente.
