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

    static WikiSearch obj;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(event.getName().equals("spell")) {
            var message = Objects.requireNonNull(event.getInteraction().getOption("message")).getAsString();
            event.reply(processMessageSpell(message)).setEphemeral(true).queue();
        }


        // optimize if possible
        if(event.getName().equals("wiki")) {
            var message = Objects.requireNonNull(event.getInteraction().getOption("query")).getAsString();
            if(isAlphaNumeric(message)) {

                CompletableFuture<String> future = WikiSearch.search(message);

                String output = "";
                try {
                    output = future.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                embedBuilder.setTitle("Wikipedia | Query: " + message)
                        .setDescription(lengthCorrect(output))
                        .setColor(Color.CYAN);

                MessageEmbed messageEmbed = embedBuilder.build();

                event.reply("").addEmbeds(messageEmbed).setEphemeral(true).queue();

            } else {
                embedBuilder.setTitle("Wikipedia | Query: " + message)
                        .setDescription("Incorrect query. Can't have special characters!")
                        .setColor(Color.CYAN);

                MessageEmbed messageEmbed = embedBuilder.build();

                event.reply("").addEmbeds(messageEmbed).setEphemeral(true).queue();
            }


        }
    }
}
