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
package net.kyori.pulsar.dependency;

import net.kyori.pulsar.util.Identifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.file.CopySpec;
import org.gradle.api.specs.Spec;

import java.util.Optional;

public abstract class PulsarDependency {
  ResolvedDependency dependency;
  Optional<Boolean> include = Optional.empty();
  private Optional<Boolean> exclude = Optional.empty();

  void include() {
    this.include = Optional.of(true);
    this.exclude = Optional.of(false);
  }

  void exclude() {
    this.include = Optional.of(false);
    this.exclude = Optional.of(true);
  }

  public void into(final CopySpec libraries) {
    for(final ResolvedArtifact artifact : this.dependency.getModuleArtifacts()) {
      libraries.from(artifact.getFile(), new Identifier(artifact.getModuleVersion().getId()).renamingTransformer());
    }
  }

  abstract boolean satisfiedBy(final ResolvedDependency dependency);

  boolean included() {
    final boolean include = !this.include.isPresent() || this.include.get();
    final boolean exclude = this.exclude.isPresent() && this.exclude.get();
    return include && !exclude;
  }

  static class Resolved extends PulsarDependency {
    final Spec<? super ResolvedDependency> spec;

    Resolved(final Spec<? super ResolvedDependency> spec) {
      this.spec = spec;
    }

    @Override
    boolean satisfiedBy(final ResolvedDependency dependency) {
      final boolean satisfied = this.spec.isSatisfiedBy(dependency);
      if(satisfied) {
        this.dependency = dependency;
      }
      return satisfied;
    }
  }

  static class Included extends PulsarDependency {
    Included(final ResolvedDependency dependency) {
      this.dependency = dependency;
      this.include();
    }

    @Override
    boolean satisfiedBy(final ResolvedDependency dependency) {
      return this.dependency == dependency;
    }
  }
}
