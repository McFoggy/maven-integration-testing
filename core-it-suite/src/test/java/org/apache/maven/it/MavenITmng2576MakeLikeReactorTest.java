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

import java.io.File;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-2576">MNG-2576</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng2576MakeLikeReactorTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng2576MakeLikeReactorTest()
    {
        super( "[2.1.0,)" ); 
    }

    private void clean( Verifier verifier )
        throws Exception
    {
        verifier.deleteDirectory( "target" );
        verifier.deleteDirectory( "sub-a/target" );
        verifier.deleteDirectory( "sub-b/target" );
        verifier.deleteDirectory( "sub-c/target" );
        verifier.deleteDirectory( "sub-d/target" );
    }

    /**
     * Verify that project list by itself only builds specified projects.
     */
    public void testitMakeOnlyList()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "sub-b" );
        verifier.setLogFileName( "log-only.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that project list and all their upstream projects are built.
     */
    public void testitMakeUpstream()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "sub-b" );
        verifier.addCliOption( "-am" );
        verifier.setLogFileName( "log-upstream.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "target/touch.txt" );
        verifier.assertFilePresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that project list and all their downstream projects are built.
     */
    public void testitMakeDownstream()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "sub-b" );
        verifier.addCliOption( "-amd" );
        verifier.setLogFileName( "log-downstream.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFilePresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that project list and all their upstream and downstream projects are built.
     */
    public void testitMakeBoth()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "sub-b" );
        verifier.addCliOption( "-am" );
        verifier.addCliOption( "-amd" );
        verifier.setLogFileName( "log-both.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "target/touch.txt" );
        verifier.assertFilePresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFilePresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that using the mere basedir in the project list properly matches projects with non-default POM files.
     */
    public void testitMatchesByBasedir()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.assertFileNotPresent( "sub-d/pom.xml" );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "sub-d" );
        verifier.setLogFileName( "log-basedir.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFilePresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that using the mere basedir in the project list properly matches projects with non-default POM files.
     */
    public void testitMatchesByBasedirPlus()
        throws Exception
    {
        // as per MNG-5230
        requiresMavenVersion( "[3.2,)" );
        
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.assertFileNotPresent( "sub-d/pom.xml" );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "+sub-d" );
        verifier.setLogFileName( "log-basedir-plus.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFilePresent( "sub-d/target/touch.txt" );
    }
    /**
     * Verify that the project list can also specify project ids.
     */
    public void testitMatchesById()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( "org.apache.maven.its.mng2576:sub-b" );
        verifier.setLogFileName( "log-id.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that the project list can also specify aritfact ids.
     */
    public void testitMatchesByArtifactId()
        throws Exception
    {
        // as per MNG-4244
        requiresMavenVersion( "[3.0-alpha-3,)" );

        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-pl" );
        verifier.addCliOption( ":sub-b" );
        verifier.setLogFileName( "log-artifact-id.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-c/target/touch.txt" );
        verifier.assertFileNotPresent( "sub-d/target/touch.txt" );
    }

    /**
     * Verify that reactor is resumed from specified project.
     */
    public void testitResumeFrom()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2576" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        clean( verifier );
        verifier.addCliOption( "-rf" );
        verifier.addCliOption( "sub-b" );
        verifier.setLogFileName( "log-resume.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "target/touch.txt" );
        verifier.assertFileNotPresent( "sub-a/target/touch.txt" );
        verifier.assertFilePresent( "sub-b/target/touch.txt" );
        verifier.assertFilePresent( "sub-c/target/touch.txt" );
    }

}
