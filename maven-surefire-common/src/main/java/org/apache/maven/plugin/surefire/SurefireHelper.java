package org.apache.maven.plugin.surefire;

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

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.surefire.log.PluginConsoleLogger;
import org.apache.maven.surefire.cli.CommandLineOption;
import org.apache.maven.surefire.suite.RunResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.apache.maven.surefire.cli.CommandLineOption.LOGGING_LEVEL_ERROR;
import static org.apache.maven.surefire.cli.CommandLineOption.LOGGING_LEVEL_WARN;
import static org.apache.maven.surefire.cli.CommandLineOption.LOGGING_LEVEL_INFO;
import static org.apache.maven.surefire.cli.CommandLineOption.LOGGING_LEVEL_DEBUG;
import static org.apache.maven.surefire.cli.CommandLineOption.SHOW_ERRORS;

/**
 * Helper class for surefire plugins
 */
public final class SurefireHelper
{

    /**
     * Do not instantiate.
     */
    private SurefireHelper()
    {
        throw new IllegalAccessError( "Utility class" );
    }

    public static void reportExecution( SurefireReportParameters reportParameters, RunResult result,
                                        PluginConsoleLogger log )
        throws MojoFailureException, MojoExecutionException
    {
        boolean timeoutOrOtherFailure = result.isFailureOrTimeout();

        if ( !timeoutOrOtherFailure )
        {
            if ( result.getCompletedCount() == 0 )
            {
                if ( reportParameters.getFailIfNoTests() == null || !reportParameters.getFailIfNoTests() )
                {
                    return;
                }
                throw new MojoFailureException(
                    "No tests were executed!  (Set -DfailIfNoTests=false to ignore this error.)" );
            }

            if ( result.isErrorFree() )
            {
                return;
            }
        }

        String msg = timeoutOrOtherFailure
            ? "There was a timeout or other error in the fork"
            : "There are test failures.\n\nPlease refer to " + reportParameters.getReportsDirectory()
                + " for the individual test results.";

        if ( reportParameters.isTestFailureIgnore() )
        {
            log.error( msg );
        }
        else
        {
            if ( result.isFailure() )
            {
                throw new MojoExecutionException( msg );
            }
            else
            {
                throw new MojoFailureException( msg );
            }
        }
    }

    public static List<CommandLineOption> commandLineOptions( MavenSession session, PluginConsoleLogger log )
    {
        List<CommandLineOption> cli = new ArrayList<CommandLineOption>();
        if ( log.isErrorEnabled() )
        {
            cli.add( LOGGING_LEVEL_ERROR );
        }

        if ( log.isWarnEnabled() )
        {
            cli.add( LOGGING_LEVEL_WARN );
        }

        if ( log.isInfoEnabled() )
        {
            cli.add( LOGGING_LEVEL_INFO );
        }

        if ( log.isDebugEnabled() )
        {
            cli.add( LOGGING_LEVEL_DEBUG );
        }

        try
        {
            Method getRequestMethod = session.getClass().getMethod( "getRequest" );
            MavenExecutionRequest request = (MavenExecutionRequest) getRequestMethod.invoke( session );

            if ( request.isShowErrors() )
            {
                cli.add( SHOW_ERRORS );
            }

            String f = getFailureBehavior( request );
            if ( f != null )
            {
                // compatible with enums Maven 3.0
                cli.add( CommandLineOption.valueOf( f.startsWith( "REACTOR_" ) ? f : "REACTOR_" + f ) );
            }
        }
        catch ( Exception e )
        {
            // don't need to log the exception that Maven 2 does not have getRequest() method in Maven Session
        }
        return unmodifiableList( cli );
    }

    public static void logDebugOrCliShowErrors( String s, PluginConsoleLogger log, Collection<CommandLineOption> cli )
    {
        if ( cli.contains( LOGGING_LEVEL_DEBUG ) )
        {
            log.debug( s );
        }
        else if ( cli.contains( SHOW_ERRORS ) )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug( s );
            }
            else
            {
                log.info( s );
            }
        }
    }

    private static String getFailureBehavior( MavenExecutionRequest request )
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        try
        {
            return request.getFailureBehavior();
        }
        catch ( NoSuchMethodError e )
        {
            return (String) request.getClass()
                .getMethod( "getReactorFailureBehavior" )
                .invoke( request );
        }
    }

}
