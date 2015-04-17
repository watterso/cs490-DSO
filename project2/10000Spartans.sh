#!/bin/bash
(
sleep 1
for i in `seq 1 100`;
	do
	echo $i
done
) | java part1/ChatClient 128.10.25.117 4269 $HOSTNAME 3456 0
