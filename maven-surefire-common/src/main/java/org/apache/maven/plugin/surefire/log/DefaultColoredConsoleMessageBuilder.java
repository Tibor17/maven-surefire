package org.apache.maven.plugin.surefire.log;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.surefire.log.api.ColoredConsoleMessageBuilder;
import org.apache.maven.plugin.surefire.log.api.Level;
import org.apache.maven.shared.utils.logging.MessageBuilder;

import static java.lang.Integer.numberOfLeadingZeros;
import static org.apache.maven.shared.utils.logging.MessageUtils.buffer;

/**
 * wrapper of org.apache.maven.shared.utils.logging.MessageUtils.
 *
 * @author <a href="mailto:tibordigana@apache.org">Tibor Digana (tibor17)</a>
 * @since 2.19.2
 */
public final class DefaultColoredConsoleMessageBuilder
    implements ColoredConsoleMessageBuilder
{
    public String toDebug( CharSequence message )
    {
        return createAnsiBuilder( message ).debug( message ).toString();
    }

    public String toInfo( CharSequence message )
    {
        return createAnsiBuilder( message ).info( message ).toString();
    }

    public String toWarning( CharSequence message )
    {
        return createAnsiBuilder( message ).warning( message ).toString();
    }

    public String toError( CharSequence message )
    {
        return createAnsiBuilder( message ).error( message ).toString();
    }

    public String toSuccess( CharSequence message )
    {
        return createAnsiBuilder( message ).success( message ).toString();
    }

    public String toUnstable( CharSequence message )
    {
        return createAnsiBuilder( message ).warning( message ).toString();
    }

    public String toFailure( CharSequence message )
    {
        return createAnsiBuilder( message ).failure( message ).toString();
    }

    public String toColorless( CharSequence message )
    {
        return createAnsiBuilder( message ).a( message ).toString();
    }

    public String format( CharSequence message, Level level )
    {
        switch ( level )
        {
            case NO_COLOR:
                return toColorless( message );
            case DEBUG:
                return toDebug( message );
            case INFO:
                return toInfo( message );
            case WARN:
                return toWarning( message );
            case ERROR:
                return toError( message );
            case FAILURE:
                return toFailure( message );
            case UNSTABLE:
                return toWarning( message );
            case SUCCESS:
                return toSuccess( message );
            default:
                throw new IllegalArgumentException( "unknown enum '" + level.name() + "'" );
        }
    }

    private static MessageBuilder createAnsiBuilder( CharSequence message )
    {
        return buffer( bufferSize( message ) );
    }

    private static int bufferSize( CharSequence message )
    {
        return 32 - numberOfLeadingZeros( message.length() );
    }
}
