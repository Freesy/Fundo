/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.szkct.weloopbtsmartdevice.main;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    //[vers add]
    public static String UUID_service = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public static String UUID_service_Notify = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String UUID_config = "0000ffd0-0000-1000-8000-00805f9b34fb";
    public static String UUID_Address_characteristic = "0000ffd1-0000-1000-8000-00805f9b34fb";
    //[vers end]

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "0000ffe1-0000-1000-8000-00805f9b34fb";
    //public static String UUID_characteristic = "00002a00-0000-1000-8000-00805f9b34fb"; // maybe 32bytes?
    public static String UUID_characteristic = "0000ffe6-0000-1000-8000-00805f9b34fb"; // maybe 32bytes?
    //public static String UUID_characteristic = "00002a01-0000-1000-8000-00805f9b34fb"; // 2 bytes

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        attributes.put("00001803-0000-1000-8000-00805f9b34fb", "Link loss");
        attributes.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");

        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
