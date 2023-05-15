import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class MinkBot {
    public static void main(String[] args) throws LoginException {
        final String TOKEN = "MTEwNDA1MzUzNjQ1NjcxNjM1MA.GekKDo.7oGIZS7_7BNbLlOGNsNs57TxKyEmVEfBQiSULY";

        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        JDA jda = jdaBuilder
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.watching("a movie"))
                .addEventListeners(new ReadyEventListener(),
                        new InteractionEventListener(),
                        new ReactionEventListener())
                .build();

        jda.upsertCommand("spell", "Spells out your message using regional indicator emojis!").addOption(OptionType.STRING, "message", "Message to be spelled out.", true, false).queue();
        jda.upsertCommand("wiki", "Wikipedia search!").addOption(OptionType.STRING, "query", "Your wiki query.", true, false).queue();
    }
}
