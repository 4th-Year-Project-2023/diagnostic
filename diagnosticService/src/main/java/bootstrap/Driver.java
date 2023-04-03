package bootstrap;

import domain.Diagnosis;
import domain.MqttPublisher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Driver {

    public static Properties projectProperties;

    public static void main(String[] args) throws IOException {

        projectProperties = readPropertiesFile("project.properties");

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Diagnosis diagnosis = new Diagnosis();
        scheduledExecutorService.scheduleWithFixedDelay(diagnosis,1,15000, TimeUnit.MILLISECONDS);

//        MqttPublisher mqttPublisher = new MqttPublisher();
//        mqttPublisher.sendMessage(4,48,48,48,48.0,48);
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }


}
