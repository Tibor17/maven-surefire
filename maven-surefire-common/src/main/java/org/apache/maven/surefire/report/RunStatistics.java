package org.apache.maven.surefire.report;

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

import org.apache.maven.plugin.surefire.report.TestSetStats;
import org.apache.maven.shared.utils.logging.MessageBuilder;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.apache.maven.surefire.suite.RunResult;

/**
 * @author Kristian Rosenvold
 */
public class RunStatistics
{
    private int completedCount;

    private int errors;

    private int failures;

    private int skipped;

    private int flakes;

    public RunStatistics()
    {
    }

    public synchronized boolean hadFailures()
    {
        return failures > 0;
    }

    public synchronized boolean hadErrors()
    {
        return errors > 0;
    }

    public synchronized int getCompletedCount()
    {
        return completedCount;
    }

    public synchronized int getSkipped()
    {
        return skipped;
    }

    public synchronized int getFailures()
    {
        return failures;
    }

    public synchronized int getErrors()
    {
        return errors;
    }

    public synchronized int getFlakes()
    {
        return flakes;
    }

    public synchronized void add( TestSetStats testSetStats )
    {
        this.completedCount += testSetStats.getCompletedCount();
        this.errors += testSetStats.getErrors();
        this.failures += testSetStats.getFailures();
        this.skipped += testSetStats.getSkipped();
    }

    public synchronized void set( int completedCount, int errors, int failures, int skipped, int flakes )
    {
        this.completedCount = completedCount;
        this.errors = errors;
        this.failures = failures;
        this.skipped = skipped;
        this.flakes = flakes;
    }

    public synchronized RunResult getRunResult()
    {
        return new RunResult( completedCount, errors, failures, skipped, flakes );
    }

    public synchronized String getSummary()
    {
        return getSummary( completedCount, failures, errors, skipped, flakes );
    }

    public static String getSummary( int completedCount, int failures, int errors, int skipped, int flakes )
    {
        MessageBuilder builder = MessageUtils.buffer();

        builder.a( "Tests " );

        if ( completedCount == 0 )
        {
            builder.a( "run: 0" );
        }
        else
        {
            builder.success( "run: " + completedCount );
        }

        builder.a( ", " );

        if ( failures == 0 )
        {
            builder.a( "Failures: 0" );
        }
        else
        {
            builder.failure( "Failures: " + failures );
        }

        builder.a( ", " );

        if ( errors == 0 )
        {
            builder.a( "Errors: 0" );
        }
        else
        {
            builder.failure( "Errors: " + errors );
        }

        builder.a( ", Skipped: " + skipped );

        if ( flakes > 0 )
        {
            builder.a( ", " );

            builder.warning( "Flakes: " + flakes );
        }

        return builder.toString();
    }
}
