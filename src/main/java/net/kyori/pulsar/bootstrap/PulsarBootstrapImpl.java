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
package net.kyori.pulsar.bootstrap;

import groovy.lang.Closure;
import org.gradle.api.logging.Logger;
import org.gradle.util.ConfigureUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PulsarBootstrapImpl implements PulsarBootstrap {
  private final List<PathEntryImpl> paths = new ArrayList<>();
  private final Map<String, String> properties = new HashMap<>();
  private String moduleName;
  private String className;

  @Override
  public PulsarBootstrap setModuleName(final String moduleName) {
    this.moduleName = moduleName;
    return this;
  }

  @Override
  public PulsarBootstrap setClassName(final String className) {
    this.className = className;
    return this;
  }

  @Override
  public PulsarBootstrap paths(final Closure<?> closure) {
    ConfigureUtil.configure(closure, new PathsImpl());
    return this;
  }

  @Override
  public PulsarBootstrap properties(final Closure<?> closure) {
    ConfigureUtil.configure(closure, new PropertiesImpl());
    return this;
  }

  public boolean write(final Logger logger, final File file) {
    if(this.moduleName == null || this.className == null) {
      return false;
    }

    try {
      if(file.exists()) {
        file.delete();
      }
      file.createNewFile();
    } catch(final IOException e) {
      logger.error("Encountered an exception while writing bootstrap configuration", e);
      return false;
    }

    try {
      final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      final Element application = document.createElement("application");
      document.appendChild(application);

      application.setAttribute(BootstrapConstants.MODULE_ATTRIBUTE_NAME, this.moduleName);
      application.setAttribute(BootstrapConstants.CLASS_ATTRIBUTE_NAME, this.className);

      for(final PathEntryImpl entry : this.paths) {
        entry.write(document, application);
      }

      for(final Map.Entry<String, String> entry : this.properties.entrySet()) {
        final Element property = document.createElement(BootstrapConstants.PROPERTY_ELEMENT_NAME);
        property.setAttribute(BootstrapConstants.PROPERTY_KEY_ATTRIBUTE_NAME, entry.getKey());
        property.appendChild(document.createTextNode(entry.getValue()));
        application.appendChild(property);
      }

      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
      transformer.transform(new DOMSource(document), new StreamResult(file));
    } catch(final ParserConfigurationException | TransformerException e) {
      logger.error("Encountered an exception while writing bootstrap configuration", e);
      return false;
    }
    return true;
  }

  public class PathsImpl implements Paths {
    @Override
    public void add(final String name, final Closure<?> closure) {
      final PathEntryImpl entry = new PathEntryImpl(name);
      PulsarBootstrapImpl.this.paths.add(entry);
      if(closure != null) {
        ConfigureUtil.configure(closure, entry);
      }
    }
  }

  public static class PathEntryImpl implements Paths.Entry {
    private final String name;
    private OptionalInt minDepth = OptionalInt.empty();
    private OptionalInt maxDepth = OptionalInt.empty();

    PathEntryImpl(final String name) {
      this.name = name;
    }

    @Override
    public void setMinDepth(final int minDepth) {
      this.minDepth = OptionalInt.of(minDepth);
    }

    @Override
    public void setMaxDepth(final int maxDepth) {
      this.maxDepth = OptionalInt.of(maxDepth);
    }

    void write(final Document document, final Element application) {
      final Element path = document.createElement(BootstrapConstants.PATH_ELEMENT_NAME);
      path.appendChild(document.createTextNode(this.name));
      if(this.minDepth.isPresent()) {
        path.setAttribute(BootstrapConstants.PATH_MIN_DEPTH_ATTRIBUTE_NAME, String.valueOf(this.minDepth.getAsInt()));
      }
      if(this.maxDepth.isPresent()) {
        path.setAttribute(BootstrapConstants.PATH_MAX_DEPTH_ATTRIBUTE_NAME, String.valueOf(this.maxDepth.getAsInt()));
      }
      application.appendChild(path);
    }
  }

  public class PropertiesImpl implements Properties {
    @Override
    public void put(final String key, final String value) {
      PulsarBootstrapImpl.this.properties.put(key, value);
    }
  }
}
