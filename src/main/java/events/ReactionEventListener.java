package events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReactionEventListener extends ListenerAdapter {

    // work on fixing
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        List<User> users = message.retrieveReactionUsers(event.getReaction().getEmoji()).complete();
        Emoji reactedEmoji = event.getReaction().getEmoji();

        if (users.size() % 2 != 0) {
            message.addReaction(reactedEmoji).submit()
                    .thenAccept(result ->
                    {
                        var usersCount = message.retrieveReactionUsers(event.getReaction().getEmoji()).complete().size();
                        if (usersCount % 2 != 0) {
                            message.removeReaction(reactedEmoji).queue();
                        }
                    });
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        List<User> users = message.retrieveReactionUsers(event.getReaction().getEmoji()).complete();
        Emoji reactedEmoji = event.getReaction().getEmoji();

        if (users.size() % 2 != 0) {
            message.addReaction(reactedEmoji).submit()
                    .thenAccept(result ->
                    {
                        var usersCount = message.retrieveReactionUsers(event.getReaction().getEmoji()).complete().size();
                        if (usersCount % 2 != 0) {
                            message.removeReaction(reactedEmoji).queue();
                        }
                    });
        }
    }
}