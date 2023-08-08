This app is to demonstrate working with Counters with Signoz.  
Related to this issue: https://github.com/SigNoz/signoz/issues/3240  

I am trying to track counters, but when the app resets, the counter resets.

This what I'm trying:  

1. Call addCounter with value of 3
2. Wait a couple of minutes, so the value is sent a few times.
3. Call addCounter with value of 5
4. Wait a few minutes
5. In Signoz, the only way to get the correct count of 8 is using the **NOOP** metric with the **REDUCE TO** setting set to "Latest of values in timeframe". The SUM_RATE just shows 0
6. Stop my app and re-run it.
7. Call addCounter with value of 2
8. Wait a few minutes
9. The **NOOP** counter now shows 2, instead of 8, which is what I am trying to achieve.
10. The **SUM_RATE** still shows 0.

I have added screenshots and the clickhouse query in the **Notes** folder in the repository linked above.

The way I've set my app up is in Eclipse I run it and then I can just type a number in the console and hit enter and it calls addCounter with that value.  
This allows keeping the app running so the counter values are constantly sent to signoz.  
To stop the app, I type "exit" and hit enter.  

