for i in 1 2 4 8 16 32 64; do echo $F$i-txt;tail $F$i.txt | grep -v Async|grep FileA; tail $F$i.txt |grep Async; done
