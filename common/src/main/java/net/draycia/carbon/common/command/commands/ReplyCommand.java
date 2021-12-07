/*
 * CarbonChat
 *
 * Copyright (c) 2021 Josua Parks (Vicarious)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.draycia.carbon.common.command.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import com.google.inject.Inject;
import java.util.UUID;
import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.users.ComponentPlayerResult;
import net.draycia.carbon.api.util.SourcedAudience;
import net.draycia.carbon.common.command.Commander;
import net.draycia.carbon.common.command.PlayerCommander;
import net.draycia.carbon.common.command.argument.CarbonPlayerArgument;
import net.draycia.carbon.common.messages.CarbonMessageService;
import net.draycia.carbon.common.util.CloudUtils;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ReplyCommand {

    @Inject
    public ReplyCommand(
        final CommandManager<Commander> commandManager,
        final CarbonMessageService messageService,
        final CarbonChat carbonChat
    ) {
        final var command = commandManager.commandBuilder("reply", "r")
            .argument(StringArgument.greedy("message"), ArgumentDescription.of("The message to reply with."))
            .permission("carbon.whisper.reply")
            .senderType(PlayerCommander.class)
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Component.text("Sends a message to the last player that messaged you."))
            .handler(handler -> {
                final CarbonPlayer sender = ((PlayerCommander) handler.getSender()).carbonPlayer();

                if (sender.muted()) {
                    messageService.muteCannotSpeak(sender);
                    return;
                }

                final String message = handler.get("message");
                final @Nullable UUID replyTarget = sender.whisperReplyTarget();

                if (replyTarget == null) {
                    messageService.replyTargetNotSet(sender, CarbonPlayer.renderName(sender));
                    return;
                }

                final ComponentPlayerResult<CarbonPlayer> result = carbonChat.server().player(replyTarget).join();
                final @MonotonicNonNull CarbonPlayer recipient = result.player();

                if (sender.equals(recipient)) {
                    messageService.whisperSelfError(sender, CarbonPlayer.renderName(sender));
                    return;
                }

                if (!recipient.online()
                    || (!sender.awareOf(recipient)
                    && !sender.hasPermission("carbon.whisper.vanished"))
                ) {
                    final var rawNameInput = CloudUtils.rawInputByMatchingName(handler.getRawInput(), recipient);
                    final var exception = new CarbonPlayerArgument.PlayerParseException(rawNameInput);

                    messageService.errorCommandArgumentParsing(sender, CloudUtils.message(exception));
                    return;
                }

                if (sender.ignoring(recipient)) {
                    messageService.whisperIgnoringTarget(sender, CarbonPlayer.renderName(recipient));
                    return;
                }

                if (recipient.ignoring(sender)) {
                    messageService.whisperTargetIgnoring(sender, CarbonPlayer.renderName(recipient));
                    return;
                }

                messageService.whisperSender(new SourcedAudience(sender, sender),
                    CarbonPlayer.renderName(sender), CarbonPlayer.renderName(recipient), message);

                messageService.whisperRecipient(new SourcedAudience(sender, recipient),
                    CarbonPlayer.renderName(sender), CarbonPlayer.renderName(recipient), message);

                sender.lastWhisperTarget(recipient.uuid());
                sender.whisperReplyTarget(recipient.uuid());
                recipient.whisperReplyTarget(sender.uuid());
            })
            .build();

        commandManager.command(command);
    }

}
