/*
 * Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Niklas Heidloff (@nheidloff)
 */

package net.bluemix.sphero;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.sphero.ConnectionListener;
import orbotix.sphero.Sphero;
import orbotix.view.connection.SpheroConnectionView;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public class MainActivity extends ActionBarActivity implements MqttCallback {

    MqttClient mqttClient;

    public void onConnectClick(View v) {

        // replace these values with the values you receive when registering new devices
        // with the IBM Internet of Things Foundation (IoT service dashboard in Bluemix)

        // broker: replace "irnwk2" with your org
        String broker       = "tcp://irnwk2.messaging.internetofthings.ibmcloud.com:1883";

        // clientId: replace "irnwk2" with your org, "and" with your type (if you use another one)
        // and "niklas" with your own device id
        String clientId     = "d:irnwk2:and:niklas";

        // password: replace with your own password
        String password     = "";

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setPassword(password.toCharArray());
            connOpts.setUserName("use-token-auth");
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);
            mqttClient.subscribe("iot-2/cmd/+/fmt/json");
            mqttClient.setCallback(this);
            System.out.println("Connected");
        } catch(MqttException me) {
            me.printStackTrace();
        }
    }

    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        System.out.println("Message arrived");
        if (message == null) return;
        String payload = message.toString();
        if (payload == null) return;
        if (topic == null) return;

        // drive the Sphero ball (command type: "setRoll")
        // expected JSON format of msg.payload:
        //  {"d":{
        //      "heading":0,
        //      "velocity":0.8 }
        //  }
        if (topic.equalsIgnoreCase("iot-2/cmd/setRoll/fmt/json")) {
            String headingS = payload.substring(payload.indexOf("heading") + 9,
                    payload.indexOf(","));
            String speedS = payload.substring(payload.indexOf("velocity") + 10,
                    payload.indexOf("}"));
            float heading = Float.parseFloat(headingS);
            float speed = Float.parseFloat(speedS);

            mRobot.drive(heading, speed);
        }

        // change the color of the Sphero ball (command type: "setColor")
        // expected JSON format of msg.payload:
        //  {"d":{
        //      "color":"red" }
        //  }
        if (topic.equalsIgnoreCase("iot-2/cmd/setColor/fmt/json")) {
            payload = payload.replace(" ","");
            String colorS = payload.substring(payload.indexOf("color") + 8,
                    payload.indexOf("}")-1);
            if (colorS == null) return;
            if (colorS.equalsIgnoreCase("")) return;

            if (colorS.equalsIgnoreCase("red")) {
                mRobot.setColor(220, 20, 60);
            }
            if (colorS.equalsIgnoreCase("green")) {
                mRobot.setColor(46,139,87);
            }
            if (colorS.equalsIgnoreCase("blue")) {
                mRobot.setColor(70,130,180);
            }
            if (colorS.equalsIgnoreCase("yellow")) {
                mRobot.setColor(255,255,0);
            }
            if (colorS.equalsIgnoreCase("black")) {
                mRobot.setColor(10,20,1);
            }
        }
    }

    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void onDisconnectClick(View v) {
        try {
            mqttClient.disconnect();
            System.out.println("Disconnected");
        }
        catch(MqttException me) {
            me.printStackTrace();
        }
    }

    private Sphero mRobot;
    private SpheroConnectionView mSpheroConnectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpheroConnectionView = (SpheroConnectionView) findViewById(R.id.sphero_connection_view);
        mSpheroConnectionView.addConnectionListener(new ConnectionListener() {

            @Override
            public void onConnected(Robot robot) {
                mRobot = (Sphero) robot;
            }

            @Override
            public void onConnectionFailed(Robot sphero) {
            }

            @Override
            public void onDisconnected(Robot sphero) {
                mSpheroConnectionView.startDiscovery();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpheroConnectionView.startDiscovery();
    }

    protected void onPause() {
        super.onPause();
        RobotProvider.getDefaultProvider().disconnectControlledRobots();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}