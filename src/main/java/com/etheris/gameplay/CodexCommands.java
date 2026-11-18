package com.etheris.gameplay;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
public final class CodexCommands {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("etheris").then(Commands.literal("codex").executes(context -> {
            var player = context.getSource().getPlayerOrException();
            player.sendSystemMessage(Component.literal("CÓDICE — DESPERTAR\n1. Explore geodos e obtenha ametista e cobre. Fabrique o Coletor Etérico e coloque sob céu aberto.\n2. Use ametista no coletor: 20 Éter viram um fragmento.\n3. Fabrique foco pessoal, Forja de Runas e Foco de Vinculação.\n4. Fragmentos carregam máquinas. Cobre na forja custa 20 Éter e produz uma runa.\n5. Use uma runa no Foco de Vinculação com 40 Éter para aprender a próxima magia.\n6. Use o foco pessoal na máquina para recarregar. Agache + use no ar para selecionar magia.\nReservatório: fragmentos depositam 20; ametista retira 20 como fragmento.\nMagias de Materia usam pedregulho/ferro bruto na mão secundária. Semente Quimérica cresce uma plantação apontada.\nÉter: " + EtherData.get(player) + "/100. Magias: " + (1 + Math.clamp(player.getPersistentData().getInt("etheris_unlocked"), 0, 7)) + "/8."));
            player.sendSystemMessage(Component.literal("Atualização 0.3: runas aprendem somente as quatro magias após a inicial. As últimas três exigem Cristais Estáveis da Refinaria. Funis automatizam Coletor, Forja, Triturador e Refinaria. O Nó distribui até 8 Éter por ciclo entre quatro destinos alternados. Fabrique um novo Códice para consultar as 12 páginas atualizadas; livros antigos mantêm seu texto."));
            return 1;
        })));
    }
}
