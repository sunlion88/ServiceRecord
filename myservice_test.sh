#!/system/bin/sh
#./myserive_test.sh xxx(录制的时长)
echo "=========Start Record Test========="
Record_Time=$1
am start-foreground-service -n com.sunlion.myservice/.MyService
sleep $Record_Time
am stopservice -n com.sunlion.myservice/.MyService
echo "=========Record Stop========="
echo "=========Total Record $Record_Time s========="

 