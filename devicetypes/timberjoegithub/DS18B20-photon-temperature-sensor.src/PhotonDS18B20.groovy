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
    input("deviceName", "text", title: "Name of device in the Photon cloud")
	// device name in api like:  https://api.spark.io/v1/devices/${deviceId}/${deviceName}
}
metadata {
	definition (name: "DS18B20 Photon Temperature Sensor", namespace: "timberjoegithub", author: "github@josephsteele.com") {
		capability "Temperature Measurement"
		capability "Sensor"
	}
    command "callTemp"
	// simulator metadata
	simulator {
		for (int i = -30; i <= 180; i += 10) {
			status "${i}F": "temperature: $i F"
		}
	}
	// UI tile definitions
	tiles {
		valueTile("temperature", "device.temperature", width: 3, height: 3) {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 20, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 60, color: "#90d2a7"],
					[value: 85, color: "#44b621"],
					[value: 98, color: "#f1d801"],
					[value: 103, color: "#d04e00"],
					[value: 110, color: "#bc2323"]
				]
			)
		}
		main(["temperature"])
		details(["temperature"])
	}
}
def installed() {
    initialize()
    log.debug "Installed with settings: ${settings}"
}
def updated() {
    initialize()
    log.debug "Updated with settings: ${settings}"
}
def initialize() {
log.debug "handlerMethod called at ${new Date()}"
runEvery5Minutes(callTemp)
log.debug "line after runevery5 executed at at ${new Date()}"
}
// Parse incoming device messages to generate events
def parse(String description) {
    log.info "Entered parse at ${new Date()}"
    def getdata = callTemp
    def name = parseName(description)
	def value = parseValue(description)
	def unit = name == "temperature" ? getTemperatureScale() : (name == "humidity" ? "%" : null)
	def result = createEvent(name: name, value: value, unit: unit)
	log.info "Parse returned ${result?.descriptionText}"
	return result
}
private String parseName(String description) {
	if (description?.startsWith("temperature: ")) {
		return "temperature"
	} 
}
private String parseValue(String description) {
	if (description?.startsWith("temperature: ")) {
    	def txtresult = ""
		return (description - "temperature: " - "°F" - "F").trim()
	}
}
def callTemp(resp2) {
	log.debug "callTemp called at ${new Date()}"
    try {
//    	log.debug "https://api.particle.io/v1/devices/${deviceId}/${deviceName}?access_token=${token}"
		httpGet(uri: "https://api.particle.io/v1/devices/${deviceId}/${deviceName}?access_token=${token}",
			contentType: 'application/json',)
			{resp ->           
        		log.debug "resp data: ${resp.data}"
        		log.info "result: ${resp.data.result}"
			sendEvent(name: "temperature", value: "${resp.data.result}", unit: "F" )  //sendEvent(name: "temperature", value: 72, unit: "F")
			log.debug (name: "temperature", value: "${resp.data.result}", unit: "F" ) 
            }	
    } catch (e) {log.error "something went wrong: $e"}
}
