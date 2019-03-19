package com.thoughtworks.gauge.gradle.util;

import com.thoughtworks.gauge.gradle.GaugeExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class PropertyManagerTest {


    @Mock
    private Project project;
    @Mock
    private ConfigurationContainer configContainer;

    private GaugeExtension extension;

    @Before
    public void setUp() {
        extension = new GaugeExtension();

        when(project.getConfigurations()).thenReturn(configContainer);
        when(project.getBuildDir()).thenReturn(new File("/blah"));
    }

    @Test
    public void classpathShouldIncludeRuntimeConfigurations() {
        mockConfigurations("compile", "runtime");
        PropertyManager manager = new PropertyManager(project, extension);

        manager.setProperties();

        assertThat(extension.getClasspath(), containsString("blah.jar:blah2.jar"));
    }

    @Test
    public void classpathShouldIncludeTestRuntimeClasspathConfigurations() {
        mockConfigurations("compile", "testRuntimeClasspath");
        PropertyManager manager = new PropertyManager(project, extension);

        manager.setProperties();

        assertThat(extension.getClasspath(), containsString("blah.jar:blah2.jar"));
    }

    private void mockConfigurations(String... configNames) {
        when(configContainer.stream()).thenReturn(Arrays.stream(configNames).map(this::configWithName));
    }

    private Configuration configWithName(String name) {
        Configuration mock = mock(Configuration.class, RETURNS_DEEP_STUBS);
        when(mock.getName()).thenReturn(name);
        when(mock.getAsFileTree().getAsPath()).thenReturn("blah.jar:blah2.jar");
        return mock;
    }
}