# Welcome to Anger Detetection with heart beat and prosody modalities
*This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces*

## Installation
To install and use the application, you will need :

1) To install the Android Studio (for instance : Android Studio Electric Eel | 2022.1.1 Patch 2) and have a Samsung Smartwatch (for instance XXXX)
2) To clone this repository on a directory
3) To open a terminal
4) To go on ./CheckSensorAvailability/abd
5) To run this command ```adb.exe connect IP_ADDRESS_OF_THE_WATCH``` (The IP address can be found in the Wireless menu of the watch)
6) Once the watch is connected, you can run the MainActivity
7) Enjoy!

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

    command : ```**_Please watch, get the 3 last times I was angry._**
    
2) The user can asked to display the last times he/she was angry which a filter by date.

    ```command : **_Please watch, display with date when I was angry in the last hour._**
    command : **_Please watch, display with date when I was angry at 4._**
    command : **_Please watch, display with date when I was angry yesterday._**```
    
    
3) The user can asked to display the history command asked for example the last one or the 2nd one.
    
    ```command : **_Please watch, display the history of the command 2_**```
    
    
4) The user can asked to change the value of the variables/values displayed in the last command.
    
    ```command : **_Please watch, take the results of the command and change percentage by pitch_**```
    
    
5) The user can asked to start relaxation method as for example counting techniques with multimodal output (touch + voice)
    
    ```command : **_Please watch, start a method to decrease my anger_**```
    
    
6) The user can asked to start relaxation method as music to calm down.
    
    ```command : **_Please watch, play some relaxation music_**
    command : **_Please watch, stop the music_**```
    
    
7) The user can asked clean the result display or change them.
   
    ```command : **_Please watch, clean the result_**```
    
    
    
Finally a construct conversation will stand between the user and the watch after action. The watch (if the option is on) will say every result aloud to the user and asked him/her what he/she wants to do after an action.
Example : 
    user : ```**_Please watch, start a method to decrease my anger_**```
    watch: ```**_Counting method has been run_**```
    ACTIONS
    watch: ```**_"Result 82 bmps... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?_**```
    user : ```**Continue, please_**```
    ACTIONS
    watch: ```**_"Result 82 bmps... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?_**```
    user : ```**_Terminate, thanks_**```



