# Firmamento — direção do slice visual

Atualização 0.7.0-preview.5: Vazio reconstruído com superfícies poligonais ligadas ao esqueleto Gecko. Coroa afilada, casco envolvente, placas chanfradas, gravuras, engaste circular e capa pregueada substituem os volumes retangulares largos. Fonte Generic Model no Blockbench e recurso próprio de superfícies no mod. Prévia visual inspecionada fora do jogo; avaliação no cliente pendente. Movimento, brilho, identidade da escola e conjunto Astra preservados.

Atualização 0.7.0-preview.4: **Vazio — Coroa da Ausência** implementado a partir da nova referência. Obsidiana e metal violeta claro, rosto negro com estrela, coroa fraturada, eclipse no peito e manto rasgado com extremidades luminosas. Usa a base de inércia aprovada na Astra, com amplitude contida no metal e uma capa um pouco mais pesada. A geometria foi inspecionada fora do jogo; o teste visual no cliente está pendente. Veja [VAZIO.md](VAZIO.md).

Atualização 0.7.0-preview.3: a prioridade de armadura passou para **Astra — Oráculo Celestial**, seguindo a referência enviada pelo usuário. Marfim, violeta profundo e ouro; máscara sem olhos, instrumento orbital, talismãs e uma capa cerimonial. A revisão atual usa GeckoLib, brilho emissivo e tecido articulado com inércia. Veja [ASTRA.md](ASTRA.md). O Véu do Eclipse permanece como conjunto separado e não recebe automaticamente essas mudanças. As notas abaixo registram a direção inicial do slice.

Expansão de sistemas suspensa. A experiência deve comunicar um cavaleiro arcano que veste o céu de um eclipse.

Paleta: obsidiana azul #0B0D19, ouro antigo #D9BC7B, prata lunar e violeta #B8AAEC. Ouro desenha a estrutura; luz ocupa poucos pontos de foco. Evitar chuva de partículas e excesso de informação. Animações devem ter antecipação, liberação e dissipação.

Menu: instrumento celestial com constelação à esquerda e descrição curta à direita. Um clique inspeciona; Sintonizar confirma. Conhecimento, seleção e energia vêm do servidor. Agachar + usar o foco abre o Firmamento. A primeira versão desenha geometria vetorial animada, sem texturas emprestadas de outros menus.

Armadura flagship: Véu do Eclipse. Primeira versão implementada com coroa de duas hastes, viseira escura, ombreiras assimétricas e manto segmentado. Preto profundo, bordas douradas e núcleo lunar. Quatro peças equipáveis, geometria própria, atlas de paleta e ícones originais. Inspeção no cliente confirmou frente, costas e inventário. O manto ainda é rígido e o núcleo não tem camada emissiva; a revisão em movimento, agachado e com braços finos continua pendente.

Três magias escolhidas para a revisão seguinte: Faísca Astral (disparo preciso com selo e corte de luz), Toque Vital (coroa de restauração que fecha ao redor do corpo), Impulso de Fluxo (avanço legível com rastros geométricos). Mecânicas e colisão precisam acompanhar o visual. Ainda não refeitas neste checkpoint.

Ensaio final planejado: cenário compacto de observatório arruinado, apresentação da armadura, alvo de conjuração, desafio de travessia e restauração. Entrada explícita em mundo de teste; nenhuma construção sobre bases existentes. Ainda não implementado.

O checkpoint 0.7.0-preview.1 entrega a direção, a primeira implementação do menu e a primeira armadura original. O comando de operador `/etheris atelier` entrega o conjunto e um foco sem substituir equipamento. Não há receita de sobrevivência nesta prévia. Os 29 testes de servidor passaram; não substituem a revisão de game feel. A validação visual do menu, acessibilidade por teclado, adaptação a telas muito pequenas e localização completa precisam ser concluídas antes de chamar o menu de final.
