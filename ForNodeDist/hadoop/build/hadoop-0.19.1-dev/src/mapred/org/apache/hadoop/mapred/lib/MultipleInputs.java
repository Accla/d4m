/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred.lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * This class supports MapReduce jobs that have multiple input paths with
 * a different {@link InputFormat} and {@link Mapper} for each path 
 */
public class MultipleInputs {
  /**
   * Add a {@link Path} with a custom {@link InputFormat} to the list of
   * inputs for the map-reduce job.
   * 
   * @param conf The configuration of the job
   * @param path {@link Path} to be added to the list of inputs for the job
   * @param inputFormatClass {@link InputFormat} class to use for this path
   */
  public static void addInputPath(JobConf conf, Path path,
      Class<? extends InputFormat> inputFormatClass) {

    String inputFormatMapping = path.toString() + ";"
       + inputFormatClass.getName();
    String inputFormats = conf.get("mapred.input.dir.formats");
    conf.set("mapred.input.dir.formats",
       inputFormats == null ? inputFormatMapping : inputFormats + ","
           + inputFormatMapping);

    conf.setInputFormat(DelegatingInputFormat.class);
  }

  /**
   * Add a {@link Path} with a custom {@link InputFormat} and
   * {@link Mapper} to the list of inputs for the map-reduce job.
   * 
   * @param conf The configuration of the job
   * @param path {@link Path} to be added to the list of inputs for the job
   * @param inputFormatClass {@link InputFormat} class to use for this path
   * @param mapperClass {@link Mapper} class to use for this path
   */
  public static void addInputPath(JobConf conf, Path path,
      Class<? extends InputFormat> inputFormatClass,
      Class<? extends Mapper> mapperClass) {

    addInputPath(conf, path, inputFormatClass);

    String mapperMapping = path.toString() + ";" + mapperClass.getName();
    String mappers = conf.get("mapred.input.dir.mappers");
    conf.set("mapred.input.dir.mappers", mappers == null ? mapperMapping
       : mappers + "," + mapperMapping);

    conf.setMapperClass(DelegatingMapper.class);
  }

  /**
   * Retrieves a map of {@link Path}s to the {@link InputFormat} class
   * that should be used for them.
   * 
   * @param conf The confuration of the job
   * @see #addInputPath(JobConf, Path, Class)
   * @return A map of paths to inputformats for the job
   */
  static Map<Path, InputFormat> getInputFormatMap(JobConf conf) {
    Map<Path, InputFormat> m = new HashMap<Path, InputFormat>();
    String[] pathMappings = conf.get("mapred.input.dir.formats").split(",");
    for (String pathMapping : pathMappings) {
      String[] split = pathMapping.split(";");
      InputFormat inputFormat;
      try {
       inputFormat = (InputFormat) ReflectionUtils.newInstance(conf
           .getClassByName(split[1]), conf);
      } catch (ClassNotFoundException e) {
       throw new RuntimeException(e);
      }
      m.put(new Path(split[0]), inputFormat);
    }
    return m;
  }

  /**
   * Retrieves a map of {@link Path}s to the {@link Mapper} class that
   * should be used for them.
   * 
   * @param conf The confuration of the job
   * @see #addInputPath(JobConf, Path, Class, Class)
   * @return A map of paths to mappers for the job
   */
  @SuppressWarnings("unchecked")
  static Map<Path, Class<? extends Mapper>> getMapperTypeMap(JobConf conf) {
    if (conf.get("mapred.input.dir.mappers") == null) {
      return Collections.emptyMap();
    }
    Map<Path, Class<? extends Mapper>> m = new HashMap<Path, Class<? extends Mapper>>();
    String[] pathMappings = conf.get("mapred.input.dir.mappers").split(",");
    for (String pathMapping : pathMappings) {
      String[] split = pathMapping.split(";");
      Class<? extends Mapper> mapClass;
      try {
       mapClass = (Class<? extends Mapper>) conf.getClassByName(split[1]);
      } catch (ClassNotFoundException e) {
       throw new RuntimeException(e);
      }
      m.put(new Path(split[0]), mapClass);
    }
    return m;
  }
}
