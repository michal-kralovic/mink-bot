package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class InteractionEventListener extends ListenerAdapter {

    final static Map<Character,String> numbers = new HashMap<>() {{
        put('0', ":zero:");
        put('1', ":one:");
        put('2', ":two:");
        put('3', ":three:");
        put('4', ":four:");
        put('5', ":five:");
        put('6', ":six:");
        put('7', ":seven:");
        put('8', ":eight:");
        put('9', ":nine:");
    }};

    public String toRegionalIndicators(char a) {
        if (Character.isDigit(a)) return numbers.get(a);
        if (Character.isWhitespace(a)) return " ";
        if (a == '!') return ":grey_exclamation:";
        if (a == '?') return ":grey_question:";
        if (a == '\'') return "";
        String base = ":regional_indicator_";
        return (base + a + ":").toLowerCase();
    }

    public boolean isAlphaNumeric(String string) {
        String regex = "['!?a-zA-Z0-9\s]+";
        return string.matches(regex);
    }

    public String processMessageSpell(String message) {
        StringBuilder output = new StringBuilder();
        char[] charArray;
        charArray = message.toCharArray();

        if (message.length() > 80) {
            return "Your message is too long! Max allowed characters are 80.";
        }

        if (!(isAlphaNumeric(message))) {
            return "Invalid characters in message!\nSupported characters: \"!, ?, a-z, A-Z, 0-9\" ";
        }

        for (int i = 0; i < message.length(); i++)
            output.append(toRegionalIndicators(charArray[i]));

        return output.toString();
    }

    public String lengthCorrect(String query) {
        String regex = "[a-zA-Z]+";

        if (query.length() < 4095)
            return query;
        else if (query.length() > 4095) {
            String longWarningMessage = ":warning: Output too long! Shortening to under 4096 characters...";
            return longWarningMessage + "\n\n" + query.substring(0, (4096-2-longWarningMessage.length()));
        } else {
            return "Invalid query";
        }
    }

    public MessageEmbed stockEmbed(String head, String body) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(head)
                .setDescription(body)
                .setColor(Color.CYAN);

        return embedBuilder.build();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        if(event.getName().equals("spell")) {
            var message = Objects.requireNonNull(event.getInteraction().getOption("message")).getAsString();
            event.reply(processMessageSpell(message)).setEphemeral(true).queue();
        }

        // Role Management
        if(event.getName().equals("giverole")) {
            MinkBotRoleManagement minkBotRoleManagement = new MinkBotRoleManagement();
            minkBotRoleManagement.roleGive(event);
        }
        if(event.getName().equals("createrole")) {
            MinkBotRoleManagement minkBotRoleManagement = new MinkBotRoleManagement();
            minkBotRoleManagement.roleCreate(event);
        }
        if(event.getName().equals("updaterole")) {
            MinkBotRoleManagement minkBotRoleManagement = new MinkBotRoleManagement();
            minkBotRoleManagement.roleUpdate(event);
        }
        if(event.getName().equals("deleterole")) {
            MinkBotRoleManagement minkBotRoleManagement = new MinkBotRoleManagement();
            minkBotRoleManagement.roleDelete(event);
        }

        if(event.getName().equals("wiki")) {
            var message = Objects.requireNonNull(event.getInteraction().getOption("query")).getAsString();
            String embedTitle = "Wikipedia | Query";

            if(isAlphaNumeric(message)) {

                CompletableFuture<String> future = WikiSearch.search(message);

                String output = "";
                try {
                    output = future.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                event.reply("").addEmbeds(stockEmbed(embedTitle + " " + message, lengthCorrect(output) + ".")).setEphemeral(true).queue();
            } else {
                event.reply("").addEmbeds(stockEmbed(embedTitle, "Incorrect query. Can't have special characters!")).setEphemeral(true).queue();
            }
        }
    }
}
