#Latest commit:
Made some improvements to load the project faster, moved to android studio and gradle. I have to admit , this was my 2nd attempt at android app : ) , little buggy on the concepts of controllers and screens. I wanted to have a game and a state machine model when i started the code. I tried it keep the code classified as possible. Just email me if you want some feature or any short modifications.  If you felt something about the project, just drop a Hello email to abhinavabcd@gmail.com , would make me happy. : )

Live Version here:
https://play.google.com/store/apps/details?id=com.quizapp.tollywood

 - controllers manages screens(one or more screens).
 - A screen is just a linearlayout , when you need a screen to be shown, load the controller(by calling quizApp.loadAppController(Controller.class) ) do some logic and create the screen. quizApp.addView(Screen) will animate it.
 - All server related functionality is in ServerCalls.java , all calls are handled asynchronously with a generic listener , will more to tasks from bolt library if i find time.




#QuizApp android
This a  clone of popular trivia app QuizUp , written totally in android, without use of any graphic libraries.
The server side runs on tornado, can horizontally scale over multiple instances by deploying more servers.
QuizApp uses websockets for the multiplayer game.



Depends on:(included in libs)
	Autobahn websockets (https://github.com/tavendo/AutobahnAndroid)
	MPCharts Lib (https://github.com/PhilJay/MPAndroidChart)
	ormlite.

Most client configuration is in Config.java , do have a look into it. All the 'in game App strings' in UiUtils.java , use them to navigate though the code.

you will have to download a "google-services.json" with google-plus and gcm enables if you want to use it for a different build, i have included one by default , you can start from here to generate it.

https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In.



You will need QuizApp_tornado_server to launch the app you can clone it from here. I have deployed the mongo instances and the web server instances on google compute.
[Github quizApp Server](https://github.com/abhinavabcd/QuizApp_server),

###Steps to get the client and server working.

#Setting up Server :

- you need to have gspread.  https://github.com/burnash/gspread , go through , http://gspread.readthedocs.org/en/latest/oauth2.html for the oauth authorization part to sync sheet from google drive.
	- git clone https://github.com/burnash/gspread.git
	- python setup.py install

- python-lxml for 2.7 python , this is absolutely not needed , but good to have for crawling scripts.
	- sudo apt-get install libxml2-dev libxslt-dev python-dev
	- sudo pip install lxml

- tornado , http://www.tornadoweb.org/en/stable/
	- pip install tornado

- mongoengine, http://docs.mongoengine.org/guide/installing.html
 	- pip install mongoengine



1. Launch mongoDB. there is a one click deploy on google compute, but it will cost you price.
2. Configure Config.py , change the dbServer address to point to mongoDb.
3. launch with
	- nohup sudo python server.py --port=80 --serverId=master --serverAddr=http://quizapp.yourserver.com >/dev/null &

# you might want to initialize first the first time launch by

	- nohup sudo python server.py --port=80 --isFirstInit --serverId=master --serverAddr=http://quizapp.yourserver.com >/dev/null &


4. The web management Ui is yet to be done.
5. To remove an server instance you have to delete the entry from Db  from the "servers" collection.(This is a little insecure)
 	- get any secret key from the "secret_keys" collection.
 	-use to reload web server map in all instances.





##Loading data to server
5. You will have to download Configure load_spreadsheet.py.
[QuizApp server google SpreadSheet](https://docs.google.com/spreadsheets/d/1fXS6D8crBo9p-xWyFG4keqHI5P8-9qqi230IKlcw5Iw/edit?usp=sharing)
The process is a little heavy, create a project in https://console.developers.google.com/ , go from API's enable drive Api , then from Credentials you have to create a OAuth Service client and download that json.
Take the service account id and share the cloned spread sheet with your service account email id( this will enable api access to read from drive - https://developers.google.com/google-apps/spreadsheets/#creating_a_spreadsheet).

6 Place the downloaded credentials in a folder names config_files in the root directory. You may run "python load_spreedsheet.py"
 'python load_spreadsheet.py syncall'    to sync all the sheet.. else it will sync only the data marked with <b>"isDirty:1"</b> in the sheet.

###You can additonally load sample questions from telugu movies by
7. Load sample questions by running load_question_data.py and input as suggested in the output.
on the root , type python and enter shell.
	exec(open("questions_json_processing/load_questions_from_json.py"))
	when promtps from path enter  "questions_json_processing/questions_json"

	exec(open("questions_json_processing/song_data_processing.py"))
	when promtps from path enter  "questions_json_processing/song_data.json"

8. Once the data is loaded , start the server by running server.py

##Client Configuration
8. In ServerCalls.java change the SERVER_ADDR and CDN path variables to suit your need ,pointing to the server and the server which hosts your images and asset files respectively.

Conversations/chatting works with Google GCM notifications for now.

#Scaling the server
To launch a new server, change Config.py and add your current server to existing 'WebserverMap' and 'ExternalWebServerMap' with your server id ,  to the existing list , this list will be propagated to other servers , so update it carefully before launching the new server. 
You should be good to go, it integrates seemlessly in terms of horizontal scaling, MongoDb can be deployed in a cluster to scale the db .
For any issues you can open up an issue or email abhinavabcd@gmail.com

This was stared as a random project, needs many changes perticularly in ui. And bug fixes :P 
All contributions and modifications are copylefted.

#Screenshots

<img src="https://cloud.githubusercontent.com/assets/1831581/5027671/aec315ca-6b57-11e4-83fb-064169bcad69.png" width="150px" /><img src="https://cloud.githubusercontent.com/assets/1831581/5027672/aec36f70-6b57-11e4-90dd-9d267d5b2a43.png" width="150px" /><img src="https://cloud.githubusercontent.com/assets/1831581/5027673/aec6452e-6b57-11e4-9cac-3735bd2f3da9.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027675/aecdd190-6b57-11e4-942d-0dc99a515e6a.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027674/aecdd80c-6b57-11e4-984a-8f7bbd256381.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027676/aece3c34-6b57-11e4-9dcd-800cd67bb31b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027677/aeebc0d8-6b57-11e4-8a19-995d9d4eff95.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027678/aeecf868-6b57-11e4-9dba-9e502224c270.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027680/aef1165a-6b57-11e4-8478-f8ab24c0b481.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027679/aeed3bc0-6b57-11e4-8040-5359966457d6.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027684/aefa3ae6-6b57-11e4-9051-000e74a61ac0.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027687/aefd51d6-6b57-11e4-95cf-80592e88ac5f.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027682/aef993fc-6b57-11e4-9356-fe56b03fa9fc.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027681/aef499ec-6b57-11e4-90ef-b576a4ecf53d.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027683/aefa4d56-6b57-11e4-828c-f6f8bf4ff6ea.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027685/aefae298-6b57-11e4-82df-6623540eec52.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027686/aefd05dc-6b57-11e4-942b-681fc0504b99.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027688/af03e208-6b57-11e4-9e26-7789b7da5dda.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027689/af198716-6b57-11e4-999b-10eb5dcac85b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027690/af1a3d3c-6b57-11e4-98d2-d8262e4319de.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027691/af1f718a-6b57-11e4-8c42-71a0d62894d9.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027692/af24fc0e-6b57-11e4-8b61-b0c35f364b1d.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027694/af28c000-6b57-11e4-87e1-95c395027d93.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027693/af28273a-6b57-11e4-85f9-4a7a4eae2c5b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027695/af346ab8-6b57-11e4-89e6-8a6d0f1179d2.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027696/b2b97a34-6b57-11e4-9923-42f90ca5c29f.png" width="150px" />


Thanks Vinay for contributions to code.

#--
