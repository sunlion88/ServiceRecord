#!/system/bin/sh
echo "=========MyService test Start========="
T_INDEX=0
T_COUNT=1
while [ "$T_INDEX" -lt "$T_COUNT" ]
do
am startservice -n com.sunlion.myservice/.MyService
sleep 120
am stopservice -n com.sunlion.myservice/.MyService
sleep 2
T_INDEX=$(($T_INDEX +1))
echo "=========Run $T_INDEX times finish========="
done
