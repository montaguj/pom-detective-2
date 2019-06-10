package com.montague.utility.pom;

import org.apache.maven.pom._4_0.Dependency;
import org.apache.maven.pom._4_0.DependencyManagement;
import org.apache.maven.pom._4_0.Model;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PomTest {
    private final String group = "com.montague";
    private final String artifact = "some-artifact";
    private final String version = "1.0;";

    @Test
    public void pomIsPositivelyIdentifiable() {
        Model mock = mock(Model.class);
        when(mock.getGroupId()).thenReturn(group);
        when(mock.getArtifactId()).thenReturn(artifact);
        when(mock.getVersion()).thenReturn(version);

        Pom p = new Pom(mock);
        Assert.assertThat(p.is(group, artifact, version), is(true));
    }

    @Test
    public void pomIsNegativelyIdentifiable() {
        Model mock = mock(Model.class);
        when(mock.getGroupId()).thenReturn(group);
        when(mock.getArtifactId()).thenReturn(artifact);
        when(mock.getVersion()).thenReturn(version);

        Pom p = new Pom(mock);
        Assert.assertThat(p.is(group + "different", artifact, version), is(false));
    }

    @Test
    public void pomHasDependency() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);
        Model.Dependencies dependencies = mock(Model.Dependencies.class);
        List<Dependency> dependencyList = new ArrayList<>();
        dependencyList.add(d);
        when(dependencies.getDependency()).thenReturn(dependencyList);
        when(mock.getDependencies()).thenReturn(dependencies);

        Pom p = new Pom(mock);
        Assert.assertThat(p.dependsOn(d).get(), is(d));
    }

    @Test
    public void pomHasNoDependency() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);
        Dependency d2 = getMockedDependency(group + "diff", artifact, version);

        Model.Dependencies dependencies = mock(Model.Dependencies.class);
        List<Dependency> dependencyList = new ArrayList<>();
        dependencyList.add(d);
        when(dependencies.getDependency()).thenReturn(dependencyList);
        when(mock.getDependencies()).thenReturn(dependencies);

        Pom p = new Pom(mock);
        Assert.assertThat(p.dependsOn(d2), is(Optional.empty()));
    }

    @Test
    public void pomHasNoDependencies() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);

        Model.Dependencies dependencies = mock(Model.Dependencies.class);
        List<Dependency> dependencyList = new ArrayList<>();
        when(dependencies.getDependency()).thenReturn(dependencyList);
        when(mock.getDependencies()).thenReturn(dependencies);

        Pom p = new Pom(mock);
        Assert.assertThat(p.dependsOn(d), is(Optional.empty()));
    }

    @Test
    public void pomHasNullDependencies() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);

        when(mock.getDependencies()).thenReturn(null);

        Pom p = new Pom(mock);
        Assert.assertThat(p.dependsOn(d), is(Optional.empty()));
    }

    private Dependency getMockedDependency(String group, String artifact, String version) {
        Dependency d = mock(Dependency.class);
        when(d.getGroupId()).thenReturn(group);
        when(d.getArtifactId()).thenReturn(artifact);
        when(d.getVersion()).thenReturn(version);
        return d;
    }

    @Test
    public void pomHasDependencyManagement() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);

        List<Dependency> dependencyList = new ArrayList<>();
        dependencyList.add(d);

        DependencyManagement dm = mock(DependencyManagement.class);
        DependencyManagement.Dependencies dependencies = mock(DependencyManagement.Dependencies.class);
        when(dm.getDependencies()).thenReturn(dependencies);
        when(dependencies.getDependency()).thenReturn(dependencyList);
        when(mock.getDependencyManagement()).thenReturn(dm);
        Pom p = new Pom(mock);
        Assert.assertThat(p.managesDependencyOf(d).get(), is(d));
    }

    @Test
    public void pomDoesNotHaveDependencyManagement() {
        Model mock = mock(Model.class);
        Dependency d = getMockedDependency(group, artifact, version);
        Dependency d2 = getMockedDependency(group + "diff", artifact, version);
        List<Dependency> dependencyList = new ArrayList<>();
        dependencyList.add(d);

        DependencyManagement dm = mock(DependencyManagement.class);
        DependencyManagement.Dependencies dependencies = mock(DependencyManagement.Dependencies.class);
        when(dm.getDependencies()).thenReturn(dependencies);
        when(dependencies.getDependency()).thenReturn(dependencyList);
        when(mock.getDependencyManagement()).thenReturn(dm);
        Pom p = new Pom(mock);
        Assert.assertThat(p.managesDependencyOf(d2), is(Optional.empty()));
    }

    @Test
    public void pomHasProperty() {
        Model mock = mock(Model.class);
        Model.Properties props = mock(Model.Properties.class);
        Element e = mock(Element.class);
        Node n = mock(Node.class);
        List<Element> elements = new ArrayList<>();

        when(e.getTagName()).thenReturn("name");
        when(e.getFirstChild()).thenReturn(n);
        elements.add(e);
        when(n.getNodeValue()).thenReturn("value");
        when(mock.getProperties()).thenReturn(props);
        when(props.getAny()).thenReturn(elements);

        Pom p = new Pom(mock);
        Assert.assertThat(p.getProperty("name").get(), is("value"));
    }


    @Test
    public void pomHasNoProperty() {
        Model mock = mock(Model.class);
        Model.Properties props = mock(Model.Properties.class);
        Element e = mock(Element.class);
        Node n = mock(Node.class);
        List<Element> elements = new ArrayList<>();

        when(e.getTagName()).thenReturn("notname");
        when(e.getFirstChild()).thenReturn(n);
        elements.add(e);
        when(n.getNodeValue()).thenReturn("value");
        when(mock.getProperties()).thenReturn(props);
        when(props.getAny()).thenReturn(elements);

        Pom p = new Pom(mock);
        Assert.assertThat(p.getProperty("name"), is(Optional.empty()));
    }

    @Test
    public void pomHasEmptyProperty() {
        Model mock = mock(Model.class);
        Model.Properties props = mock(Model.Properties.class);
        Node n = mock(Node.class);
        List<Element> elements = new ArrayList<>();
        when(mock.getProperties()).thenReturn(props);
        when(props.getAny()).thenReturn(elements);

        Pom p = new Pom(mock);
        Assert.assertThat(p.getProperty("name"), is(Optional.empty()));
    }

}
