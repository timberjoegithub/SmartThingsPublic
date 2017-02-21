/**
 *  Spark Core
 *
 *  Copyright 2015 TOM FORTI
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
/**
 *  PHOTON RELAY
 *
 *  Author: tomforti@gmail.com
 *  Date: 2015-10-18
 */
 
preferences {
    input("token", "text", title: "Access Token")
    input("deviceId", "text", title: "Device ID")
    input("relaynum", "text", title: "Relay Number 1-8")
}
 
 // for the UI
metadata {
	definition (name: "PHOTON RELAY", author: "tonforti@gmail.com") {
    	capability "Switch"
	}

    // tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
		state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}

		main "switch"
		details "switch"
	}
}

def parse(String description) {
	log.error "This device does not support incoming events"
	return null
}

def on() {
	put '1'
    sendEvent(name: 'switch', value: 'on')
}

def off() {
	put '0'
    sendEvent(name: 'switch', value: 'off')
}

private put(relaystate) {
    //Spark Core API Call
    def relaynum = "${relaynum}"
    def relayname = ""
	if (relaynum == "1") {relayname = "hotTubSwitch"}
 	if (relaynum == "2") {relayname = "saunaSwitch"}
	if (relaynum == "3") {relayname = "HTLghtSwitch"}
	if (relaynum == "4") {relayname = "lightSwitch"}
	if (relaynum == "5") {relayname = "relay5Switch"}
	if (relaynum == "6") {relayname = "relay6Switch"}
	if (relaynum == "7") {relayname = "relay7Switch"}
	if (relaynum == "8") {relayname = "relay8Switch"}
//    log.debug ("https://api.spark.io/v1/devices/${deviceId}/${relayname}")
    httpPost(
		uri: "https://api.spark.io/v1/devices/${deviceId}/${relayname}",
//  		uri: "https://api.spark.io/v1/devices/${deviceId}/relay${relaynum}Switch",
        body: [access_token: token, command: relaystate],  
	) {response -> log.debug (response.data)
    log.debug ("https://api.spark.io/v1/devices/${deviceId}/${relayname}")}
}
