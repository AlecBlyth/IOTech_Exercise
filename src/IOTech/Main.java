package IOTech;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

class tempobj { //Temp object for sorting elements by name.

    private String objName;
    private String objType;
    private String objInfo;
    private String objUUID;
    private long objPayload;

    tempobj(String name, String type, String info, String Uuid, long Payload) {

        this.objName = name;
        this.objType = type;
        this.objInfo = info;
        this.objUUID = Uuid;
        this.objPayload = Payload;

    }
    public String getObjName() {
        return objName;
    }
    public String getObjType() {
        return objType;
    }
    public String getObjInfo() {
        return objInfo;
    }
    public String getObjUUID() {
        return objUUID;
    }
    public long getObjPayload() {
        return objPayload;
    }
}

public class Main {


    public static void main(String[] args) throws Exception {

        String name;
        String type;
        String info;
        String uuid = "";
        String formattedInfo = "";

        ArrayList<Long> nums = new ArrayList<Long>(); //Arraylist to store numbers for addition
        ArrayList<tempobj> tempobjects = new ArrayList<tempobj>(); //Arraylist to store elements from json

        long sum = 0; //initialise sum

        // parsing file "devices.json"
        Object obj = new JSONParser().parse(new FileReader("src/datafiles/data/devices.json"));

        // typecasting obj to JSONObject
        JSONObject jsonObj = (JSONObject) obj;

        JSONArray devices = (JSONArray) jsonObj.get("Devices"); //Initialise device object within json file

        System.out.println("System extracted the following... \n");

        for (Object device : devices) {
            JSONObject jsonDevice = (JSONObject) device;
            name = (String) jsonDevice.get("Name"); //Get name string
            type = (String) jsonDevice.get("Type"); //Get type string
            info = (String) jsonDevice.get("Info"); //Get info string
            int delimterIndex = info.indexOf(","); //Initalise delimter

            if (delimterIndex != -1) {
                uuid = info.substring(info.indexOf("uuid"), delimterIndex); //Gets UUID from string, by starting at "uuid" and stopping at ","
                formattedInfo = info.replace(uuid, ""); //Removes UUID from string
                formattedInfo = formattedInfo.replace(" , ", ", "); //Removes extra space from format
            }

            JSONArray sensors = (JSONArray) jsonDevice.get("Sensors"); //Initialise sensor object within json file
            for (Object sensor : sensors) {
                JSONObject jsonSensor = (JSONObject) sensor;
                nums.add((Long) jsonSensor.get("Payload"));

            }

            Iterator<Long> it = nums.iterator(); //Iterates through arralist
            while (it.hasNext()) { //while array has elements
                sum = it.next() + it.next(); //Sum is equal to element plus next elemnet
                //System.out.print(sum); //print out element for testing
            }
            tempobj newObj = new tempobj(name, type, formattedInfo, uuid, sum);
            tempobjects.add(newObj);
        }

        Collections.sort(tempobjects, new Comparator<tempobj>() {
            @Override
            public int compare(tempobj o1, tempobj o2) {
                return o1.getObjName().compareToIgnoreCase(o2.getObjName());
            }
        });

        try {
            FileWriter myWriter = new FileWriter("output.json");
            myWriter.write("{\n" +
                    "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"properties\": {\n" +
                    "    \"Devices\": {\n" +
                    "      \"type\": \"array\",\n" +
                    "      \"items\": [\n" +
                    "        {\n");


            for(tempobj objs : tempobjects){
                myWriter.write(                    "          \"object\": \"device\",\n" +
                        "          \"properties\": {\n" +
                        "            \"Name\": {\n" +
                        "              \"string\": "+ '"' + objs.getObjName() + '"' + "\n" +
                        "            },\n" +
                        "            \"Type\": {\n" +
                        "              \"string\": "+ '"' + objs.getObjType() + '"' + "\n" +
                        "            },\n" +
                        "            \"Info\": {\n" +
                        "              \"string\": "+ '"' + objs.getObjInfo() + '"' + "\n" +
                        "            },\n" +
                        "            \"Uuid\": {\n" +
                        "              \"string\": "+ '"' + objs.getObjUUID() + '"' + "\n" +
                        "            },\n" +
                        "            \"PayloadTotal\": {\n" +
                        "              \"integer\": "+ objs.getObjPayload() + "\n" +                     "            },\n");
            }

            myWriter.write(
                    "          },\n" +
                            "          \"required\": [\n" +
                            "            \"Name\",\n" +
                            "            \"Type\",\n" +
                            "            \"Info\",\n" +
                            "            \"Uuid\",\n" +
                            "            \"PayloadTotal\"\n" +
                            "          ]\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"required\": [\n" +
                            "    \"Devices\"\n" +
                            "  ]\n" +
                            "}\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
