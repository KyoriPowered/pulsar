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

public class BootstrapConstants {
  /**
   * The name of the configuration file.
   */
  public static final String CONFIGURATION_FILE_NAME = "bootstrap.xml";
  /**
   * The name of the element that contains paths we should search in.
   */
  public static final String PATH_ELEMENT_NAME = "path";
  /**
   * The name of the optional attribute that contains the maximum depth a path should be searched.
   */
  public static final String PATH_MAX_DEPTH_ATTRIBUTE_NAME = "max-depth";
  /**
   * The name of the attribute that contains our target module.
   */
  public static final String MODULE_ATTRIBUTE_NAME = "module";
  /**
   * The name of the attribute that contains our target class.
   */
  public static final String CLASS_ATTRIBUTE_NAME = "class";

  /**
   * The name of the group that the bootstrap artifact is in.
   */
  public static final String BOOTSTRAP_ARTIFACT_GROUP = "net.kyori";
  /**
   * The name of the bootstrap artifact.
   */
  public static final String BOOTSTRAP_ARTIFACT_NAME = "bootstrap";
}
