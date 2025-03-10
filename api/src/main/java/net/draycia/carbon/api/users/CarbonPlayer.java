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
package net.draycia.carbon.api.users;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.util.InventorySlot;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Generic abstraction for players.
 *
 * @since 2.0.0
 */
@DefaultQualifier(NonNull.class)
public interface CarbonPlayer extends Audience, Identified {

    CarbonPlayer EMPTY = new EmptyCarbonPlayer();

    /**
     * Returns the appropriate {@link Component} to represent the player's name.
     *
     * @param player the player
     * @return the player's name
     * @since 2.0.0
     */
    static Component renderName(final CarbonPlayer player) {
        if (player.hasCustomDisplayName()) {
            return Objects.requireNonNull(player.displayName());
        } else {
            return Component.text(player.username());
        }
    }

    /**
     * Gets the player's username.
     *
     * @return the player's username
     * @since 2.0.0
     */
    String username();

    /**
     * Checks if the player has a display name set through {@link CarbonPlayer#displayName(Component)}.
     *
     * @return if the player has a display name set through {@link CarbonPlayer#displayName(Component)}
     * @since 2.0.0
     */
    boolean hasCustomDisplayName();

    /**
     * Gets the player's display name, shown in places like chat and tab menu.
     *
     * @return the player's display name
     * @since 2.0.0
     */
    @Nullable Component displayName();

    /**
     * Sets the player's display name.<br>
     * Setting null is equivalent to setting the display name to the username.
     *
     * @param displayName the new display name
     * @since 2.0.0
     */
    void displayName(final @Nullable Component displayName);

    /**
     * The player's UUID, often used for identification purposes.
     *
     * @return the player's UUID
     * @since 2.0.0
     */
    UUID uuid();

    /**
     * Creates the chat component for the item in the {@link InventorySlot}, or null if the slot is empty.
     *
     * @param slot the inventory slot containing the item
     * @return the chat component for the item in the slot, or null if the slot is empty
     * @since 2.0.0
     */
    @Nullable Component createItemHoverComponent(final InventorySlot slot);

    /**
     * The player's locale.
     *
     * @return the player's locale, or null if offline
     * @since 2.0.0
     */
    @Nullable Locale locale();

    /**
     * The player's selected channel, or null if one isn't set.
     *
     * @return the player's selected channel
     * @since 2.0.0
     */
    @Nullable ChatChannel selectedChannel();

    /**
     * Sets the player's selected channel.
     *
     * @param chatChannel the channel
     * @since 2.0.0
     */
    void selectedChannel(final @Nullable ChatChannel chatChannel);

    /**
     * Checks if the player has the specified permission.
     *
     * @param permission the permission to check
     * @return if the player has the permission
     * @since 2.0.0
     */
    boolean hasPermission(final String permission);

    /**
     * Returns the player's primary group.
     *
     * @return the player's primary group
     * @since 2.0.0
     */
    String primaryGroup();

    /**
     * Returns the complete list of groups the player is in.
     *
     * @return the groups the player is in
     * @since 2.0.0
     */
    List<String> groups();

    /**
     * Returns if the player is muted.
     *
     * @return if the player is muted
     * @since 2.0.0
     */
    boolean muted();

    /**
     * Mutes and unmutes the player.
     *
     * @param muted if the player is now muted
     * @since 2.0.0
     */
    void muted(boolean muted);

    /**
     * Checks if the sender is being ignored by this player.
     *
     * @param sender the potential source of a message
     * @return if this player is ignoring the sender
     * @since 2.0.0
     */
    boolean ignoring(final CarbonPlayer sender);

    /**
     * Adds the player to and removes the player from the ignore list.
     *
     * @param player the player to be added/removed
     * @param nowIgnoring if the player should be ignored
     * @since 2.0.0
     */
    void ignoring(final CarbonPlayer player, boolean nowIgnoring);

    /**
     * Returns if the player is deafened and unable to read messages.
     *
     * @return if the player is deafened
     * @since 2.0.0
     */
    boolean deafened();

    /**
     * Deafens and undeafens the player.
     *
     * @since 2.0.0
     */
    void deafened(final boolean deafened);

    /**
     * Returns if the player is spying on messages and able to read muted/private messages.
     *
     * @return if the player is spying on messages
     * @since 2.0.0
     */
    boolean spying();

    /**
     * Sets and unsets the player's ability to spy.
     *
     * @since 2.0.0
     */
    void spying(final boolean spying);

    /**
     * Sends the message as the player.
     *
     * @param message the message to be sent
     * @since 2.0.0
     */
    void sendMessageAsPlayer(final String message);

    /**
     * Checks if the player is allowed to send the message or not.
     *
     * @param message the message
     * @return if the message can be sent in chat
     * @since 2.0.4
     */
    boolean speechPermitted(final String message);

    /**
     * Returns whether the player is online.
     *
     * @return if the player is online.
     * @since 2.0.0
     */
    boolean online();

    /**
     * The UUID of the player that replies will be sent to.
     *
     * @return the player's reply target
     * @since 2.0.0
     */
    @Nullable UUID whisperReplyTarget();

    /**
     * Sets the whisper reply target for this player.
     *
     * @param uuid the uuid of the reply target
     * @since 2.0.0
     */
    void whisperReplyTarget(final @Nullable UUID uuid);

    /**
     * The last player this player has whispered.
     *
     * @return the player's last whisper target
     * @since 2.0.0
     */
    @Nullable UUID lastWhisperTarget();

    /**
     * Sets the last player this player has whispered.
     *
     * @param uuid the uuid of the whisper target
     * @since 2.0.0
     */
    void lastWhisperTarget(final @Nullable UUID uuid);

    /**
     * If this player is vanished in another supported plugin.
     * Other players will be unaware of this player.
     * There is no way to set this state through Carbon, we do not store this information; but merely bridge it.
     *
     * @return If this player is vanished in another plugin.
     * @since 2.0.0
     */
    boolean vanished();

    /**
     * Whether this player can see the other player.
     *
     * @param other the other, potentially vanished, player
     * @return if this player is aware of the other player
     * @since 2.0.0
     */
    boolean awareOf(final CarbonPlayer other);

}
