#for i in 1 2 4 8 16 32 64; do echo $F$i-txt;tail $F$i.txt | grep -a -v Async|grep -a FileA; tail $F$i.txt |grep -a Async; done
for i in 1  4  16  64; do echo $F$i-txt;tail $F$i.txt | grep -a -v Async|grep -a FileA; tail $F$i.txt |grep -a Async; done
