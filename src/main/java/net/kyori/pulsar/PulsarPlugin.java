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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;

public class PulsarPlugin implements Plugin<Project> {
  @Override
  public void apply(final Project project) {
    this.applyDependencies(project.getPluginManager());

    final PulsarExtension extension = project.getExtensions().create(Pulsar.EXTENSION_NAME, PulsarExtension.class, project);
    extension.getConfigurations().add(project.getConfigurations().findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME));

    project.afterEvaluate(new PulsarAction(extension));
  }

  private void applyDependencies(final PluginManager pm) {
    pm.apply(DistributionPlugin.class);
    pm.apply(JavaPlugin.class);
  }
}
