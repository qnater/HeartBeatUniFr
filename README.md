# Welcome to Anger Detetection with heart beat and prosody modalities
*This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces*

## Installation
To install and use the application, you will need :

1) To install the Android Studio (for instance : Android Studio Electric Eel | 2022.1.1 Patch 2) and have a Samsung Smartwatch (for instance Galaxy Watch4 (LC9T))
2) To open a terminal and clone this repository on the desired directory ```git clone https://github.com/qnater/HeartBeatUniFr.git```
3) To go on ./CheckSensorAvailability/abd and the terminal ```cd ./CheckSensorAvailability/abd```
4) To run this command ```adb.exe connect IP_ADDRESS_OF_THE_WATCH``` (The IP address can be found in the Wireless menu of the watch)
5) Once the watch is connected, you can run the MainActivity
6) Enjoy!

## Use the application
To use the application, slide down on the watch in the main menu and click on the "Anger Detection" application. The application is set and ready when the heart beat will be displayed on the screen.

## Features
Many features are available on the application. These will be explained bellow.

### Information display and emotional state
The watch will display in real time the information of the heartbeat modality and the prosody modality (extract from the voice on the user). To switch the information displayed, you just have to click on the screen.
In the same time, the background will change depending on your current emotional state. If the AI will detect calmness the background will be a peaceful landscape. If the stress is detected, the background will be entangled cables. For the anger state, an angry face will be displayed and the watch will vibrate.

### Interaction with the user
By speaking, the user will be able to ask many things to the watch application.
1) The user can asked to display the last X times he/she was angry.

    user command : 
    ```
    Please watch, get the 3 last times I was angry.
    Please watch, clean the result.
    ```
    
2) The user can asked to display the last times he/she was angry which a filter by date.

    user commands : 
    ```
    Please watch, display with date when I was angry in the last hour.
    Please watch, display with date when I was angry at 4.
    Please watch, display with date when I was angry yesterday.
    Please watch, clean the result.
    ```
    
    
3) The user can asked to display the history command asked for example the last one or the 2nd one.
    
    user command : 
    ```
    Please watch, display the history of the command 2.
    ```
    
    
4) The user can asked to change the value of the variables/values displayed in the last command.
    
    user command : 
    ```
    Please watch, take the results of the command and replace percentage by pitch.
    ```
    
    
5) The user can asked to start relaxation method as for example counting techniques with multimodal output (touch + voice)
    
    user command : 
    ```
    Please watch, start a method to decrease my anger.
    ```
    
    
6) The user can asked to start relaxation method as music to calm down.
        
    user command : 
    ```
    Please watch, play some relaxation music.
    Please watch, stop the music.
    ```

    
7) The user can asked clean the result display or change them.
   
    user command : 
    ```
    Please watch, clean the result.
    ```
    
    
    
Finally a construct conversation will stand between the user and the watch after action. The watch (if the option is on) will say every result aloud to the user and asked him/her what he/she wants to do after an action.
Example : 

   user :
    ```
    Please watch, start a method to decrease my anger.
    ```
    
    
   watch:
    ```
    Counting method has been run.
    ```
    
    
   ACTIONS
    
    
   watch:
    ```
    Your results are 82 bmps... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?
    ```
    
    
   user : 
    ```
    Continue, please.
    ```
    
    
   ACTIONS
    
    
   watch: 
    ```
    Result 82 bmps... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?
    ```
    
    
   user :
    ```
    Terminate, thanks.
    ```
    
## Anger Detection and Relaxation method
The anger will be detected by threshold reached by the information combined of the heartbeat of the user, the pitch (Hz) of his/her voice and the amplitude of his/her voice. The result will lead to state (calm, stress, anger) with a percentage of security (from 50% to 100%) and the noise of the result.
With this detection, relaxation methods (counting and music) can be launch by the user to calm down. These technics have been picked from actual reseaches.

## Log and data
The data can be reached at anytime by the user because it is stored in the database in real time. The data stored are the timestamp, the emtional state, the heartbeat , the pitch, the amplitude, the noise and the auc. The DB is reachable if the inner directory of the watch "./storage/0/heartbeat_data_set.csv"

## User guide and demo
The user guide and demo can be reached from this video : HHHH
[![Video Name](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)



