package dfki.mm.paho;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

// mosquitto_sub -L mqtt://mqtt.eclipse.org:1883/strel

public class TestTalk {

    public static final String TOPIC_SEND = "strel";
    public static final String TOPIC_RECV = "strelresp";

    public static void main(String[] args) throws InterruptedException {

        JSONObject o = new JSONObject();
//        o.put("type", "request");
        o.put("id", "test" + System.currentTimeMillis());
//        o.put("request", "5+5");
        JSONArray a = new JSONArray();
        a.put(new JSONArray(Arrays.asList(0.1, 5)));
        a.put(new JSONArray(Arrays.asList(0.2, 5)));
        a.put(new JSONArray(Arrays.asList(0.3, 5)));
        a.put(new JSONArray(Arrays.asList(0.2, 5)));
        o.put("request", a);

        String topic        = TOPIC_SEND;
        String content      = o.toString();
        int qos             = 0;
//        String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://mqtt.eclipse.org:1883";
        String clientId     = "JavaSample" + System.currentTimeMillis();
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            sampleClient.subscribe(TOPIC_RECV, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(topic + ": " + message.toString());
                }
            });



            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");

            TimeUnit.SECONDS.sleep(2);

            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
