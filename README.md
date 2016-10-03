# sdt-weather-simulator

# sdt-toy-simulator

It is a java library for weather simulation. This library was implemented using Yahoo Weather API. The simulator takes the input from a text file, which contains the WOEID codes for different locations and outputs the data in the below form:

Location|Lattitude,Longitude,Elevation|Time|Condition|Temperature|Pressure|humidity

where,<br />
Location refers to the location for which WOEID is given,<br />
Lattitude,Longitude,Elevation - are comma separated triple set showing the lattitude,longitude and elevation of the location <br />
Time - which is in ISO 8601 format date time <br />
Condition - Either this is Rain,Sunny and Snow <br />
Temperature - which is in C <br />
Pressure - which is in hpa <br />
Humidity - which is in % <br />

<h3> How to run the simulator: </h3>
Build the project by the following commands

$ mvn compile <br />
$ mvn clean package <br />
$ java -jar "path to this jar - \target\sdt-toy-simulator-2.0.3-SNAPSHOT-jar-with-dependencies.jar" "path/of/input text file" <br />



<h3>Input:</h3>
Sample input file can be found in src/test/resources/default directory.
The input for the simulator is a text file - A list with set of locations or lat,long values. For Example refer to the below sample <br /> 

22.54994,88.37158 <br />
7.0077,100.47053  <br />
hyderabad  <br />
55.74164,37.60506  <br />
dubai  <br />
punjab   <br />
32.1685,-95.66919  <br />
Sydney <br />
Melbourne <br />

where, <br />
Melbourne - is the location name <br />
22.54994,88.37158 - lattitude,longitude values<br />

<h3>Output:</h3>

The Ouput is given in below format: 

Kolkata|22.54399,88.37908,3.023699283599854|2016-09-17T12:30Z|Rain|30|493.11901680266385|76.0 <br />
Hat Yai|7.00726,100.47085,20.94498062133789|2016-09-17T12:30Z|Rain|32|492.13666940637313|50.0 <br />
Hyderabad|17.41841,78.45001,555.2223510742188|2016-09-17T12:30Z|Sunny|26|463.6496713659021|73.0 <br />
Moscow|55.72908,37.60236,122.0506210327148|2016-09-17T11:30Z|Rain|9|488.20744978730914|84.0 <br />
Dubai|25.26944,55.30865,3.067578554153442|2016-09-17T12:30Z|Sunny|33|493.61010551775973|64.0 <br />
Punjab|30.8603,72.3743,153.0|2016-09-17T12:30Z|Sunny|36|483.2958827719545|40.0 <br />
Larue|32.1685,-95.66919,152.696533203125|2016-09-17T12:30Z|Rain|25|489.6808858986958|88.0 <br />
Sydney|-33.85628,151.02097,16.63168907165527|2016-09-17T12:30Z|Rain|17|497.5393251368237|60.0 <br />
Melbourne|-37.8658,145.10283,44.85793304443359|2016-09-17T12:30Z|Rain|15|495.08354162914634|61.0 <br />
