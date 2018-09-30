/*
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.config;

import com.linkedin.kafka.cruisecontrol.common.Resource;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Unit test for {@link BrokerCapacityConfigFileResolver}
 */
public class BrokerCapacityConfigFileResolverTest {

  @Test
  public void testParseConfigFile() {
    BrokerCapacityConfigResolver configResolver = new BrokerCapacityConfigFileResolver();
    Map<String, String> configs = new HashMap<>();
    String fileName = this.getClass().getClassLoader().getResource("testCapacityConfig.json").getFile();
    configs.put(BrokerCapacityConfigFileResolver.CAPACITY_CONFIG_FILE, fileName);
    configResolver.configure(configs);

    assertEquals(200000.0, configResolver.capacityForBroker("", "", 0)
                                         .capacity().get(Resource.NW_IN), 0.01);
    assertEquals(100000.0, configResolver.capacityForBroker("", "", 2)
                                         .capacity().get(Resource.NW_IN), 0.01);
    try {
      configResolver.capacityForBroker("", "", BrokerCapacityConfigFileResolver.DEFAULT_CAPACITY_BROKER_ID);
      fail("Should have thrown exception for negative broker id");
    } catch (IllegalArgumentException e) {
      // let it go
    }

    assertTrue(configResolver.capacityForBroker("", "", 2).isEstimated());
    assertTrue(configResolver.capacityForBroker("", "", 2).estimationInfo().length() > 0);
  }

  @Test
  public void testParseConfigJBODFile() {
    BrokerCapacityConfigResolver configResolver = new BrokerCapacityConfigFileResolver();
    Map<String, String> configs = new HashMap<>();
    String fileName = this.getClass().getClassLoader().getResource("testCapacityConfigJBOD.json").getFile();
    configs.put(BrokerCapacityConfigFileResolver.CAPACITY_CONFIG_FILE, fileName);
    configResolver.configure(configs);

    assertEquals(2000000.0, configResolver.capacityForBroker("", "", 0)
                                         .capacity().get(Resource.DISK), 0.01);
    assertEquals(2200000.0, configResolver.capacityForBroker("", "", 3)
                                         .capacity().get(Resource.DISK), 0.01);
    assertEquals(200000.0, configResolver.capacityForBroker("", "", 3)
                                         .diskCapacityByLogDir().get("/tmp/kafka-logs-4"), 0.01);

    assertTrue(!configResolver.capacityForBroker("", "", 2).isEstimated());
    assertTrue(configResolver.capacityForBroker("", "", 3).isEstimated());
    assertTrue(configResolver.capacityForBroker("", "", 3).estimationInfo().length() > 0);
  }
}
