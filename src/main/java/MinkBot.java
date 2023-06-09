import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class MinkBot {
    public static void main(String[] args) throws LoginException {
        // fear not, it's not a real token
        final String TOKEN = "";

        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        JDA jda = jdaBuilder
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.watching("a movie"))
                .addEventListeners(new ReadyEventListener(),
                        new InteractionEventListener(),
                        new ReactionEventListener(),
                        new MinkBotRoleManagement())
                .build();

        jda.upsertCommand("spell", "Spells out your message using regional indicator emojis!").addOption(OptionType.STRING, "message", "Message to be spelled out.", true, false).queue();
        jda.upsertCommand("wiki", "Wikipedia search!").addOption(OptionType.STRING, "query", "Your wiki query.", true, false).queue();

        // Role Management
        jda.upsertCommand("createrole", "Role creating!")
                .addOption(OptionType.STRING, "role-name", "The role's name!", true, false)
                .addOption(OptionType.BOOLEAN, "mentionability", "(OPTIONAL) Set mentionability (true/false).", true, false)
                .addOption(OptionType.STRING, "color", "(OPTIONAL) Set color (hex).", false, false)
                .queue();
        jda.upsertCommand("updaterole", "Role updating!")
                .addOption(OptionType.ROLE, "role", "The role!", true, false)
                .addOption(OptionType.STRING, "role-name", "(OPTIONAL) Change the role's name.", false, false)
                .addOption(OptionType.STRING, "color", "(OPTIONAL) Change color (HEX).", false, false).queue();
        jda.upsertCommand("deleterole", "Role deleting! Be careful!")
                .addOption(OptionType.ROLE, "role", "The role!", true, false).queue();
        jda.upsertCommand("giverole", "Role giving!")
                .addOption(OptionType.ROLE, "role", "The role!", true, false)
                .addOption(OptionType.USER, "person", "Who do you want to give the role to?", true, false)
                .addOption(OptionType.STRING, "color", "(OPTIONAL) Change color (HEX).", false, false).queue();
    }
}
