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

import net.kyori.pulsar.bootstrap.BootstrapConstants;
import net.kyori.pulsar.util.Identifier;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.file.CopySpec;
import org.gradle.api.plugins.JavaPlugin;

import java.io.File;

public class PulsarAction implements Action<Project> {
  private final PulsarExtension extension;

  public PulsarAction(final PulsarExtension extension) {
    this.extension = extension;
  }

  @Override
  public void execute(final Project project) {
    final Distribution distribution = ((DistributionContainer) project.getExtensions().getByName("distributions")).maybeCreate(Pulsar.DISTRIBUTION_NAME);

    final CopySpec libraries = project.copySpec();
    libraries.into(Pulsar.LIBRARIES_DIRECTROY_NAME);

    this.extension.filter.resolve(this.extension.configurations).forEach(dependency -> dependency.into(libraries));

    if(this.extension.self) {
      final Task jar = project.getTasks().getAt(JavaPlugin.JAR_TASK_NAME);
      libraries.from(jar, spec -> spec.rename(new Identifier(project).renamingTransformer()));
    }

    distribution.getContents().with(libraries);

    final File bootstrap = new File(new File(project.getBuildDir(), "tmp"), BootstrapConstants.CONFIGURATION_FILE_NAME);
    if(this.extension.bootstrap.write(project.getLogger(), bootstrap)) {
      distribution.getContents().from(bootstrap);
    }
  }
}
