# Project-1.2 Titan 

##Project structure

#### Diveded in classes that:

##### a) Contain code for the GUI's
* Gui.java
* InitialGui.java
* LandingGui.java
* ResizableCanvas

#### b) Contain code for running the mission
* BodySystem.java
* SolarSystem.java
* EarthLander.java
* TitanLander.java
* PIDController.java

##### c) Store object data of facilitate calculations
* The rest of the classes


##Mission Structure 

####Trajectory 

Probe is launched from Earth in direction Titan, 
with our goal being to make trajectory as straight as possible, 
using little gravity assist. This made finding a path easier as we 
didnt have to worry about the effect of other planets 

The trajectory back to Earth starts at Titan's orbit and uses the sun
for gravity assist to swing around to the Earth. This is because since
Earth is an more inner planet it is harder to find a straight path see as
often there are other planets in the way or it is on the wrong side of the sun.

When nearing the target planet our PID comes into action and makes sure the 
probe enters the orbit

**Responsible Classes**:
* Gui.java - displays trajectory 
* BodySystem.java - calculates where planets and probe will be 
* SolarSystem.java - arranges planets (BodySystem child)
* Body.java - applies Forces

####Landing 

For landing on Titan we assumes orbit height and apply the forces of gravity 
on the lander probe. Also the wind speeds depending on altitude are applied.
We the have a PID controller for the lander which adjusts the velocity and rotation
using thrusters to land safely  

Landing on Earth is the same, however with different values for the gravity and 
the different wind speeds 

**Responsible Classes**
* LandingGui.java
* TitanLander.java
* EarthLander.java
* PIController

