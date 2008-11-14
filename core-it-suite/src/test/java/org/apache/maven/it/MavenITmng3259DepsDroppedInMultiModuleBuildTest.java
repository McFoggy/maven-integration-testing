package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3259">MNG-3259</a>.
 * 
 * @version $Id$
 */
public class MavenITmng3259DepsDroppedInMultiModuleBuildTest
    extends AbstractMavenIntegrationTestCase
{

    /*
     * TODO: All combinations from the cross product {jdk-1.4.2_16, jdk-1.5.0_14, jdk-1.6.0_07} x {mvn-2.0.7, mvn-2.0.8}
     * passed this test for me (bentmann on WinXP). This makes the test appear very weak.
     */
    public MavenITmng3259DepsDroppedInMultiModuleBuildTest()
    {
        super( "(2.0.8,)" );
    }

    public void testitMNG3259 ()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3259" );

        Verifier verifier;

        verifier = new Verifier( new File( testDir, "parent" ).getAbsolutePath() );

        List cliOptions = new ArrayList();

        verifier.setCliOptions( cliOptions );

        verifier.executeGoal( "install" );

        verifier.verifyErrorFreeLog();

        verifier.resetStreams();

        verifier = new Verifier( testDir.getAbsolutePath() );

        verifier.executeGoal( "install" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }
}