/*
 * This file is part of pulsar, licensed under the MIT License.
 *
 * Copyright (c) 2018 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.pulsar;

import net.kyori.pulsar.bootstrap.PulsarBootstrap;
import net.kyori.pulsar.bootstrap.PulsarBootstrapImpl;
import net.kyori.pulsar.dependency.PulsarDependencies;
import net.kyori.pulsar.dependency.PulsarDependenciesImpl;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

public class PulsarExtension {
  Collection<Configuration> configurations = new ArrayList<>();
  final PulsarBootstrapImpl bootstrap = new PulsarBootstrapImpl();
  final PulsarDependencies filter;
  boolean self = true;

  public PulsarExtension(final Project project) {
    this.filter = new PulsarDependenciesImpl(project);
    this.configurations.add(project.getConfigurations().findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME));
  }

  public PulsarExtension bootstrap(final Action<PulsarBootstrap> action) {
    action.execute(this.bootstrap);
    return this;
  }

  public PulsarExtension dependencies(final Action<PulsarDependencies> action) {
    action.execute(this.filter);
    return this;
  }

  public Collection<Configuration> getConfigurations() {
    return this.configurations;
  }

  public void setConfigurations(final Collection<Configuration> configurations) {
    this.configurations = configurations;
  }

  public void setSelf(final boolean self) {
    this.self = self;
  }
}
