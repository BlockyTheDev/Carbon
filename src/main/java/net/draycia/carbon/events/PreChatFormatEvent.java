package net.draycia.carbon.events;

import net.draycia.carbon.channels.ChatChannel;
import net.draycia.carbon.storage.ChatUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PreChatFormatEvent extends Event implements Cancellable {

  @NonNull
  private static final HandlerList HANDLERS_LIST = new HandlerList();
  private boolean isCancelled = false;

  @NonNull
  private final ChatUser user;

  @NonNull
  private ChatChannel chatChannel;

  @Nullable
  private String format;

  @NonNull
  private String message;

  public PreChatFormatEvent(@NonNull ChatUser user, @NonNull ChatChannel chatChannel, @Nullable String format, @NonNull String message) {
    super(!Bukkit.isPrimaryThread());

    this.user = user;
    this.chatChannel = chatChannel;
    this.format = format;
    this.message = message;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.isCancelled = cancelled;
  }

  @Override
  @NonNull
  public HandlerList getHandlers() {
    return HANDLERS_LIST;
  }

  @NonNull
  public static HandlerList getHandlerList() {
    return HANDLERS_LIST;
  }

  @NonNull
  public ChatUser getUser() {
    return user;
  }

  @NonNull
  public ChatChannel getChannel() {
    return chatChannel;
  }

  public void setChannel(@NonNull ChatChannel chatChannel) {
    this.chatChannel = chatChannel;
  }

  @Nullable
  public String getFormat() {
    return format;
  }

  public void setFormat(@Nullable String format) {
    this.format = format;
  }

  @NonNull
  public String getMessage() {
    return message;
  }

  public void setMessage(@NonNull String message) {
    this.message = message;
  }

}
