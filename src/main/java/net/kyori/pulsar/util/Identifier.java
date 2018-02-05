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
package net.kyori.pulsar.util;

import net.kyori.pulsar.bootstrap.BootstrapConstants;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedDependency;

import java.util.Objects;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public final class Identifier {
  private static final String JAR_EXTENSION = "jar";
  private final String group;
  private final String name;
  @Nullable private final String version;

  public Identifier(final Project project) {
    this(
      String.valueOf(project.getGroup()),
      String.valueOf(project.getName()),
      String.valueOf(project.getVersion())
    );
  }

  public Identifier(final Dependency dependency) {
    this(dependency.getGroup(), dependency.getName(), dependency.getVersion());
  }

  public Identifier(final ResolvedDependency dependency) {
    this(dependency.getModule().getId());
  }

  public Identifier(final ModuleVersionIdentifier id) {
    this(id.getGroup(), id.getName(), id.getVersion());
  }

  private Identifier(final String group, final String name, @Nullable final String version) {
    this.group = requireNonNull(group, "group");
    this.name = requireNonNull(name, "name");
    this.version = version;
  }

  public boolean isSatisfiedBy(final ResolvedDependency dependency) {
    return dependency.getModuleGroup().matches(this.group)
      && dependency.getModuleName().matches(this.name)
      && matches(dependency.getModuleVersion(), this.version);
  }

  public Transformer<String, String> renamingTransformer() {
    final StringBuilder sb = new StringBuilder();

    if(this.useHierarchy()) {
      sb.append(this.group.replace('.', '/'));
      sb.append('/');
      sb.append(this.name);
      sb.append('/');
      sb.append(this.version);
      sb.append('/');
    } else {
      sb.append(this.group);
      sb.append('-');
    }

    sb.append(this.name).append('-').append(this.version).append('.').append(JAR_EXTENSION);

    return original -> sb.toString();
  }

  private boolean useHierarchy() {
    return !(BootstrapConstants.BOOTSTRAP_ARTIFACT_GROUP.equals(this.group) && BootstrapConstants.BOOTSTRAP_ARTIFACT_NAME.equals(this.name));
  }

  private static boolean matches(final String a, final String b) {
    return b == null || a.matches(b);
  }

  @Override
  public String toString() {
    return this.group + ':' + this.name + ':' + this.version;
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) {
      return true;
    }
    if(other == null || this.getClass() != other.getClass()) {
      return false;
    }
    final Identifier that = (Identifier) other;
    return Objects.equals(this.group, that.group)
      && Objects.equals(this.name, that.name)
      && Objects.equals(this.version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.group, this.name, this.version);
  }
}
