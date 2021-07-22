#!/bin/sh
javac -cp lib\telegrambots-3.5-jar-with-dependencies.jar -d classes src\*.java -encoding utf8
cd classes || exit
java -cp "..\lib\telegrambots-3.5-jar-with-dependencies.jar;" App
sleep 30
