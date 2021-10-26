package net.draycia.carbon.bukkit.util;

import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.SourcedAudience;
import net.draycia.carbon.common.config.PrimaryConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import net.kyori.moonshine.message.IMessageRenderer;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class BukkitMessageRenderer implements IMessageRenderer<SourcedAudience, String, Component, Component> {

    private final PlaceholderAPIMiniMessageParser parser = PlaceholderAPIMiniMessageParser.create(MiniMessage.miniMessage());
    private final PrimaryConfig config;

    @Inject
    public BukkitMessageRenderer(final PrimaryConfig config) {
        this.config = config;
    }

    @Override
    public Component render(
        final SourcedAudience receiver,
        final String intermediateMessage,
        final Map<String, ? extends Component> resolvedPlaceholders,
        final Method method,
        final Type owner
    ) {
        final List<Template> templates = new ArrayList<>();

        for (final var entry : resolvedPlaceholders.entrySet()) {
            templates.add(Template.template(entry.getKey(), entry.getValue()));
        }

        // https://github.com/KyoriPowered/adventure-text-minimessage/issues/131
        // TLDR: 25/10/21, tags in templates aren't parsed. we want them parsed.
        String placeholderResolvedMessage = intermediateMessage;

        for (final var entry : this.config.customPlaceholders().entrySet()) {
            placeholderResolvedMessage = placeholderResolvedMessage.replace("<" + entry.getKey() + ">",
                entry.getValue());
        }

        if (receiver.sender() instanceof CarbonPlayer sender && sender.online()) {
            if (receiver.recipient() instanceof CarbonPlayer recipient && recipient.online()) {
                return this.parser.parseRelational(Bukkit.getPlayer(sender.uuid()),
                    Bukkit.getPlayer(recipient.uuid()), placeholderResolvedMessage, templates);
            } else {
                return this.parser.parse(Bukkit.getPlayer(sender.uuid()), placeholderResolvedMessage, templates);
            }
        }

        return MiniMessage.miniMessage().deserialize(placeholderResolvedMessage, TemplateResolver.templates(templates));
    }

}
