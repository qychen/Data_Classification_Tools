@SET PATH=./jre/bin;%1;%PATH%;
java -Xms128m -Xmx256m -XX:PermSize=128M -XX:MaxPermSize=256M default package.DataMiningTools
