/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hbase.util;

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.hbase.LargeTests;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * A write/read/verify load test on a mini HBase cluster. Tests reading
 * and writing at the same time.
 */
@Category(LargeTests.class)
@RunWith(Parameterized.class)
public class TestMiniClusterLoadParallel
    extends TestMiniClusterLoadSequential {

  public TestMiniClusterLoadParallel(boolean isMultiPut,
      DataBlockEncoding encoding) {
    super(isMultiPut, encoding);
  }

  @Test(timeout=TIMEOUT_MS)
  public void loadTest() throws Exception {
    prepareForLoadTest();

    readerThreads.linkToWriter(writerThreads);

    writerThreads.start(0, numKeys, NUM_THREADS, false, 0, 0);
    readerThreads.start(0, numKeys, NUM_THREADS, false, 0, 0);

    writerThreads.waitForFinish();
    readerThreads.waitForFinish();

    assertEquals(0, writerThreads.getNumWriteFailures());
    assertEquals(0, readerThreads.getNumReadFailures());
    assertEquals(0, readerThreads.getNumReadErrors());
    assertEquals(numKeys, readerThreads.getNumUniqueKeysVerified());
  }

}