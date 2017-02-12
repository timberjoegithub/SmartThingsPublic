/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
preferences {
    input("token", "text", title: "Access Token")
    input("deviceId", "text", title: "Device ID")
//    input("relaynum", "text", title: "Relay Number 1-6")
    input("deviceName", "text", title: "Name of device in the Photon cloud")
	// device name in api like:  https://api.spark.io/v1/devices/${deviceId}/${deviceName}
}
metadata {
	definition (name: "DS18B20 Temperature Sensor", namespace: "smartthings", author: "github@josephsteele.com") {
		capability "Temperature Measurement"
		capability "Sensor"

		fingerprint profileId: "0104", deviceId: "0302", inClusters: "0000,0001,0003,0009,0402,0405"
	}

	// simulator metadata
	simulator {
		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "temperature: $i F"
		}
	}

	// UI tile definitions
	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
		

		main(["temperature"])
		details(["temperature"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def name = parseName(description)
	def value = parseValue(description)
	def unit = name == "temperature" ? getTemperatureScale() : (name == "humidity" ? "%" : null)
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

private String parseName(String description) {
	if (description?.startsWith("temperature: ")) {
		return "temperature"
	} 
	null
}

private String parseValue(String description) {
	if (description?.startsWith("temperature: ")) {
		httpGet(
			uri: "https://api.spark.io/v1/devices/${deviceId}/${deviceName}",
			body: [access_token: token, command: result],  
			) {response -> log.debug (response.data)}
		return zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())
	} 
	null
}

// curl https://api.particle.io/v1/devices/XXXXXX/temperature?access_token=XXXXXX

//private put(relaystate) {
    //Spark Core API Call
//	httpPost(
//		uri: "https://api.spark.io/v1/devices/${deviceId}/strelay${relaynum}",
//        body: [access_token: token, command: relaystate],  
//	) {response -> log.debug (response.data)}
//}
