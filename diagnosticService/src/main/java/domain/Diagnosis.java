package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class Diagnosis implements Runnable {

    private double cpuUsage;

    private double memoryUsage;

    private double diskUsage;

    private double temperature;

    private double networkSpeed;

    private java.util.Date currDate;

    Runtime rt = Runtime.getRuntime();

    @Override
    public void run() {
        try {
            setDiagnosticParameters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDiagnosticParameters() throws IOException {

        System.out.println("Setting.........");

        currDate = new java.util.Date();
        fetchCpuUsage();
        fetchDiskUsage();
        fetchMemoryUsage();
        fetchCpuTemp();
        fetchNetworkSpeed();

        MqttPublisher mqttPublisher = new MqttPublisher();

        mqttPublisher.sendMessage( getCurrDate(),getCpuUsage(),getMemoryUsage(), getDiskUsage(), getTemperature(), getNetworkSpeed());

    }


    private void fetchNetworkSpeed() throws IOException {
        Process proc = rt.exec("ethtool eth0");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Speed:")) {
                String speedString = line.replaceAll("[^\\d.]", "");
                double networkSpeed = Double.parseDouble(speedString);
                setNetworkSpeed(networkSpeed);

                System.out.println(networkSpeed);
            }
        }

    }

    private void fetchCpuTemp() throws IOException {

        Process proc = rt.exec("vcgencmd measure_temp");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = reader.readLine();
        String tempString = line.replaceAll("[^\\d.]", "");
        double cpuTemp = Double.parseDouble(tempString);

        System.out.println("Temperature" + cpuTemp);

        setTemperature(cpuTemp);
    }

    private void fetchMemoryUsage() throws IOException {

        Process proc = rt.exec("free");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Mem:")) {
                String[] parts = line.split("\\s+");
                String usedMemString = parts[2];
                double usedMem = Double.parseDouble(usedMemString);

                System.out.println("Memory Usage" + usedMem);
                setMemoryUsage(usedMem);
            }
        }
    }

    private void fetchDiskUsage() throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("df -h /");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("/dev/")) {
                String[] parts = line.split("\\s+");
                String usedDiskString = parts[4].replaceAll("%", "");
                double usedDisk = Double.parseDouble(usedDiskString);

                setDiskUsage(usedDisk);
                System.out.println("Disk usage: " + usedDisk + "%");
            }
        }

    }

    private void fetchCpuUsage() throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("top -b -n1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("%Cpu(s):")) {
                String[] parts = line.split("\\s+");
                String cpuUsageString = parts[1].replaceAll(",", ".");
                double cpuUsage = Double.parseDouble(cpuUsageString);

                setCpuUsage(cpuUsage);
                System.out.println("CPU usage: " + cpuUsage + "%");
            }
        }

    }


    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getNetworkSpeed() {
        return networkSpeed;
    }

    public void setNetworkSpeed(double networkSpeed) {
        this.networkSpeed = networkSpeed;
    }

    public Date getCurrDate() {
        return currDate;
    }

}
