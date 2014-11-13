#QuizApp android  
This a  clone of popular trivia app QuizUp , written totally in android, without use of any graphic libraries.
The server runs on tornado, can scale over multiple instances by deploying more webservers.
QuizApp uses websockets for the multiplayer game.

Its a eclipse android Project . You will need QuizApp_tornado_server to launch the app you can clone it from here.
[Github quizApp Server](https://github.com/abhinavabcd/QuizApp_server)

###Steps to get the client and server working.

#Setting up Server :
1. Launch mongoDB
2. Configure Config.py , change the dbServer address to point to mongoDb.
3. Change the WebServerMap and ExternalWebServerMap to appropriate address of how your server ip address looks from the internal and external viewpoint.
4. Give a Unique server Id in Config.py

##Loading data to server
5. Configure load_spreadsheet.py with the your spreadsheet containing your data. see this sample sheet clone it.
[QuizApp server google SpreadSheet](https://docs.google.com/spreadsheets/d/1fXS6D8crBo9p-xWyFG4keqHI5P8-9qqi230IKlcw5Iw/edit?usp=sharing)
6. 'python load_spreadsheet.py syncall'    to sync all the sheet.. else it will sync only the data marked with <b>"isDirty:1"</b> in the sheet

###You can additonally load sample questions from telugu movies by
7. Load sample questions by running load_question_data.py and input as suggested in the output.
8. Once the data is loaded , start the server by running server.py

##Client Configuration
8. In ServerCalls.java change the SERVER_ADDR and CDN path variables to suit your need ,pointing to the webserver and the server which hosts your images and asset files.

Conversations/chatting works with Google GCM notifications for now. will change to websockets at later point of time.

#Scaling the webserver
To launch a new webserver, change Config.py and add your current server to existing 'WebserveMap' and 'ExternalWebServerMap' with your server id ,  to the existing list , this list will be propagated to other servers , so update it carefully before launching the new server. 
You should be good to go, it integrates seemlessly in terms of horizontal scaling, MongoDb can be deployed in a cluster to scale the db .
For any issues you can open up an issue or email abhinavabcd@gmail.com or vinay.bhargav.reddy@gmail.com

This was stared as a random project, needs many changes perticularly in ui. And bug fixes :P 
All contributions and modifications are copylefted.

#Screenshots

<img src="https://cloud.githubusercontent.com/assets/1831581/5027671/aec315ca-6b57-11e4-83fb-064169bcad69.png" width="150px" /><img src="https://cloud.githubusercontent.com/assets/1831581/5027672/aec36f70-6b57-11e4-90dd-9d267d5b2a43.png" width="150px" /><img src="https://cloud.githubusercontent.com/assets/1831581/5027673/aec6452e-6b57-11e4-9cac-3735bd2f3da9.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027675/aecdd190-6b57-11e4-942d-0dc99a515e6a.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027674/aecdd80c-6b57-11e4-984a-8f7bbd256381.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027676/aece3c34-6b57-11e4-9dcd-800cd67bb31b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027677/aeebc0d8-6b57-11e4-8a19-995d9d4eff95.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027678/aeecf868-6b57-11e4-9dba-9e502224c270.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027680/aef1165a-6b57-11e4-8478-f8ab24c0b481.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027679/aeed3bc0-6b57-11e4-8040-5359966457d6.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027684/aefa3ae6-6b57-11e4-9051-000e74a61ac0.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027687/aefd51d6-6b57-11e4-95cf-80592e88ac5f.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027682/aef993fc-6b57-11e4-9356-fe56b03fa9fc.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027681/aef499ec-6b57-11e4-90ef-b576a4ecf53d.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027683/aefa4d56-6b57-11e4-828c-f6f8bf4ff6ea.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027685/aefae298-6b57-11e4-82df-6623540eec52.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027686/aefd05dc-6b57-11e4-942b-681fc0504b99.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027688/af03e208-6b57-11e4-9e26-7789b7da5dda.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027689/af198716-6b57-11e4-999b-10eb5dcac85b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027690/af1a3d3c-6b57-11e4-98d2-d8262e4319de.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027691/af1f718a-6b57-11e4-8c42-71a0d62894d9.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027692/af24fc0e-6b57-11e4-8b61-b0c35f364b1d.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027694/af28c000-6b57-11e4-87e1-95c395027d93.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027693/af28273a-6b57-11e4-85f9-4a7a4eae2c5b.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027695/af346ab8-6b57-11e4-89e6-8a6d0f1179d2.png" width="150px" /> <img src="https://cloud.githubusercontent.com/assets/1831581/5027696/b2b97a34-6b57-11e4-9923-42f90ca5c29f.png" width="150px" />


Thanks Vinay for contributions to code.





