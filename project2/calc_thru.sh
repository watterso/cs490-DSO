NUM=$(($2+1))
tail -n+$NUM $1 | cut -f 1 -d ' ' | cut -c 1-10 | uniq -c | awk '{ sum += $1; n++ } END { if (n > 0) print sum / n; }'
