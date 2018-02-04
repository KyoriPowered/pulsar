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
package net.kyori.pulsar.distribution;

import groovy.lang.Closure;
import net.kyori.pulsar.util.Identifier;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.specs.Spec;
import org.gradle.util.ConfigureUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PulsarDistributionImpl implements PulsarDistribution {
  private final Map<Identifier, PulsarDistributionEntryImpl> dependencies = new HashMap<>();
  private final Project project;
  private boolean limitedInclude;

  public PulsarDistributionImpl(final Project project) {
    this.project = project;
  }

  @Override
  public PulsarDistribution include(final Spec<? super ResolvedDependency> spec) {
    this.push((SpecImpl) spec).include();
    return this;
  }

  @Override
  public PulsarDistribution exclude(final Spec<? super ResolvedDependency> spec) {
    this.push((SpecImpl) spec).exclude();
    return this;
  }

  @Override
  public PulsarDistribution configure(final Spec<? super ResolvedDependency> spec, final Closure<?> closure) {
    final PulsarDistributionEntryImpl dependency = this.push((SpecImpl) spec);
    ConfigureUtil.configure(closure, dependency);
    return this;
  }

  private PulsarDistributionEntryImpl push(final SpecImpl spec) {
    return this.dependencies.computeIfAbsent(spec.dependency, ignored -> new PulsarDistributionEntryImpl.Resolved(spec));
  }

  @Override
  public Spec<? super ResolvedDependency> dependency(final Object notation) {
    return this.dependency(this.project.getDependencies().create(notation));
  }

  @Override
  public Spec<? super ResolvedDependency> dependency(final Dependency dependency) {
    return new SpecImpl(new Identifier(dependency));
  }

  @Override
  public Spec<? super ResolvedDependency> project(final String notation) {
    final Map<String, String> map = new HashMap<>(2);
    map.put("path", notation);
    map.put("configuration", "default");
    return this.dependency(this.project.getDependencies().project(map));
  }

  @Override
  public Collection<PulsarDistributionEntryImpl> resolve(final Collection<Configuration> configurations) {
    this.resolveState();
    return configurations.stream()
      .flatMap(configuration -> this.resolve(configuration).stream())
      .collect(Collectors.toSet());
  }

  private void resolveState() {
    this.limitedInclude = this.dependencies.values().stream().anyMatch(dependency -> dependency.include.isPresent() && dependency.include.get());
  }

  private Collection<PulsarDistributionEntryImpl> resolve(final Configuration configuration) {
    final Set<PulsarDistributionEntryImpl> artifacts = new HashSet<>();
    this.resolve(configuration.getResolvedConfiguration().getFirstLevelModuleDependencies(), artifacts);
    return artifacts;
  }

  private void resolve(final Set<ResolvedDependency> dependencies, final Set<PulsarDistributionEntryImpl> artifacts) {
    for(final ResolvedDependency dependency : dependencies) {
      if(!this.limitedInclude) {
        this.dependencies.computeIfAbsent(new Identifier(dependency), dep -> new PulsarDistributionEntryImpl.Included(dependency));
      }

      this.dependencies.values().stream()
        .filter(it -> it.satisfiedBy(dependency))
        .limit(1)
        .filter(PulsarDistributionEntryImpl::included)
        .forEach(artifacts::add);

      this.resolve(dependency.getChildren(), artifacts);
    }
  }

  private static class SpecImpl implements Spec<ResolvedDependency> {
    private final Identifier dependency;

    private SpecImpl(final Identifier dependency) {
      this.dependency = dependency;
    }

    @Override
    public boolean isSatisfiedBy(final ResolvedDependency dependency) {
      return this.dependency.isSatisfiedBy(dependency);
    }
  }
}
