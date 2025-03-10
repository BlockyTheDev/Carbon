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
//
// ban - A punishment suite for Velocity.
// Copyright (C) 2021 Mariell Hoversholm
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published
// by the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
// Modified to better fit in Carbon
//

package net.draycia.carbon.common.messages;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import net.draycia.carbon.api.util.SourcedAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.receiver.IReceiverLocator;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public final class ReceiverResolver implements IReceiverLocatorResolver<Audience> {

    @Override
    public IReceiverLocator<Audience> resolve(final Method method, final Type proxy) {
        return new Resolver();
    }

    private static final class Resolver implements IReceiverLocator<Audience> {
        @Override
        public Audience locate(final Method method, final Object proxy, final @Nullable Object[] parameters) {
            if (parameters.length == 0) {
                return Audience.empty();
            }

            final @Nullable Object parameter = parameters[0];

            if (parameter instanceof SourcedAudience sourcedAudience) {
                return sourcedAudience.recipient();
            } else if (parameter instanceof Audience audience) {
                return audience;
            }

            return Audience.empty();
        }
    }

}
