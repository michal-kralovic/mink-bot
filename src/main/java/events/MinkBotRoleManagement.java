package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

public class MinkBotRoleManagement extends ListenerAdapter {


    public boolean isHexadecimalColor(String color) {
        String regex = "^[#0-9A-Fa-f]+$";
        return color.matches(regex)  && color.startsWith("#") && color.length() == 7;
    }

    public MessageEmbed stockEmbed(String head, String body) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(head)
                .setDescription(body)
                .setColor(Color.CYAN);

        return embedBuilder.build();
    }

    public void roleCreate(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        var nameOption = event.getInteraction().getOption("role-name");
        var colorOption = event.getInteraction().getOption("color");
        var mentionOption = event.getInteraction().getOption("mentionability");
        RoleAction roleAction;
        Guild guild = event.getGuild();

        assert nameOption != null;
        assert guild != null;
        boolean colorPresent = false;
        if (colorOption != null) {
            var color = colorOption.getAsString();
            colorPresent = isHexadecimalColor(color);
        }

        boolean mentionableBool;
        assert mentionOption != null;
        mentionableBool = mentionOption.getAsBoolean();

        if (colorPresent) {
            roleAction = guild.createRole().setColor(Color.decode(colorOption.getAsString())).setName(nameOption.getAsString()).setMentionable(mentionableBool);
        } else {
            roleAction = guild.createRole().setName(nameOption.getAsString()).setMentionable(mentionableBool);
        }

        roleAction.queue(role -> event.reply("")
                .addEmbeds(stockEmbed("Role Create", "\nSuccessfully created the role \"" + role.getName() + "\"."))
                .setEphemeral(true)
                .queue());
    }

    public void roleUpdate(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        var role = Objects.requireNonNull(event.getInteraction().getOption("role")).getAsRole();
        var nameOption = event.getInteraction().getOption("role-name");
        var colorOption = event.getInteraction().getOption("color");

        if (colorOption != null && nameOption != null) {
            var color = colorOption.getAsString();
            var name = nameOption.getAsString();

            if (isHexadecimalColor(color)) {
                role.getManager().setColor(Color.decode(color)).setName(name).queue();
                event.reply("")
                        .addEmbeds(stockEmbed("Role Update", "\nSuccessfully edited the role \"" + role.getName() + "\". \nChanged the color to: " + color.toUpperCase(Locale.ROOT) + ".\nChanged the name to: " + name + "."))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("")
                        .addEmbeds(stockEmbed("Role Update", "Invalid color or name! (HEX colors only!). Your strings:\nColor: " + color + "\nName: " + name))
                        .setEphemeral(true)
                        .queue();
            }
        }
        else if (nameOption != null) {
            var name = nameOption.getAsString();
            role.getManager().setName(nameOption.getAsString()).queue();
            event.reply("")
                    .addEmbeds(stockEmbed("Role Update", "\nSuccessfully edited the role \"" + role.getName() + "\". \nChanged the name to: " + name + "."))
                    .setEphemeral(true)
                    .queue();
        }
        else {
            assert colorOption != null;
            var color = colorOption.getAsString();
            role.getManager().setColor(Color.decode(color)).queue();

            if (isHexadecimalColor(color)) {
                role.getManager().setColor(Color.decode(color)).queue();
                event.reply("")
                        .addEmbeds(stockEmbed("Role Update", "\nSuccessfully edited the role \"" + role.getName() + "\". \nChanged the color to: " + color.toUpperCase(Locale.ROOT) + "."))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("")
                        .addEmbeds(stockEmbed("Role Update", "Invalid color! (HEX colors only!). Your string:\nColor: " + color))
                        .setEphemeral(true)
                        .queue();
            }

        }
    }

    // desperately in need of optimizing
    public void roleDelete(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

            var role = Objects.requireNonNull(event.getInteraction().getOption("role")).getAsRole();
            var embed = stockEmbed("Role Delete", "\nAre you sure you want to delete \"" + role.getName() + "\"?");
            var yesButton = Button.danger(role.getId(), "Yes");
            var noButton = Button.primary("no", "No");

            event.replyEmbeds(embed)
                    .addActionRow(yesButton, noButton)
                    .setEphemeral(true)
                    .queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        Guild guild = event.getGuild();
        assert guild != null;

        if (event.getComponentId().contains("no")) {
            event.reply("")
                    .addEmbeds(stockEmbed("Role Delete", "\nSuccessfully aborted the operation."))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        String roleId = event.getComponentId();
        Role role = guild.getRoleById(roleId);
        if (event.getComponentId().equals(roleId) && !event.getComponentId().contains("no")) {
            try {
                assert role != null;
                role.delete().queue();
                event.reply("")
                        .addEmbeds(stockEmbed("Role Delete", "\nSuccessfully deleted the role \"" + role.getName() + "\"."))
                        .setEphemeral(true)
                        .queue();
            } catch (HierarchyException e) {
                event.reply("")
                        .addEmbeds(stockEmbed("Role Delete", "\nCannot delete roles that are higher or equal!"))
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    public void roleGive(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        var role = Objects.requireNonNull(event.getInteraction().getOption("role")).getAsRole();
        var colorOption = event.getInteraction().getOption("color");
        Guild guild = event.getGuild();

        if (colorOption != null) {
            var color = colorOption.getAsString();

            if (isHexadecimalColor(color)) {
                role.getManager().setColor(Color.decode(color)).queue();
                assert guild != null;
                guild.retrieveMember(event.getUser()).queue(member -> {
                    guild.addRoleToMember(member, role).queue();
                    event.reply("").addEmbeds(stockEmbed("Roles", "\nSuccessfully gave you the role \"" + role.getName() + "\" with the color: " + color + ".")).setEphemeral(true).queue();
                });
            } else {
                event.reply("").addEmbeds(stockEmbed("Roles", "Invalid color! (HEX colors only!). Color used: " + color + ".")).setEphemeral(true).queue();
            }

        } else {
            assert guild != null;
            guild.retrieveMember(event.getUser()).queue(member -> {
                guild.addRoleToMember(member, role).queue();
                event.reply("").addEmbeds(stockEmbed("Roles", "\nSuccessfully gave you the role \"" + role.getName() + "\".")).setEphemeral(true).queue();
            });

        }

    }
}
